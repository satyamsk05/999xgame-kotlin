package com.ingames.games

import com.ingames.database.MemoryDataStore
import com.ingames.models.*
import com.ingames.wallet.FinancialService
import com.ingames.websocket.WebSocketHub
import kotlinx.coroutines.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

class DragonTigerEngine(
    private val scope: CoroutineScope,
    private val wsHub: WebSocketHub
) {
    private val logger = LoggerFactory.getLogger(DragonTigerEngine::class.java)

    var currentRoundNumber: Long = MemoryDataStore.nextRoundNumber()
        private set
    var currentRoundId: String = "dt_${currentRoundNumber}_${UUID.randomUUID().toString().take(6)}"
        private set
    private var serverSeed: String = ProvablyFair.generateServerSeed()
    var serverSeedHash: String = ProvablyFair.sha256("$serverSeed:$currentRoundNumber")
        private set
    var status: RoundStatus = RoundStatus.BETTING_OPEN
        private set

    private var dragonCard: Card? = null
    private var tigerCard: Card? = null
    private var winningArea: DragonTigerBetArea? = null

    val recentOutcomes = CopyOnWriteArrayList<DragonTigerBetArea>(
        listOf(
            DragonTigerBetArea.DRAGON,
            DragonTigerBetArea.TIGER,
            DragonTigerBetArea.DRAGON,
            DragonTigerBetArea.DRAGON,
            DragonTigerBetArea.TIE,
            DragonTigerBetArea.TIGER
        )
    )
    private val activeBets = ConcurrentHashMap<String, MutableList<DragonTigerBet>>()

    data class DragonTigerBet(
        val userId: String,
        val area: DragonTigerBetArea,
        val amountPaise: Long,
        val idempotencyKey: String
    )

    fun start() {
        scope.launch {
            while (isActive) {
                runRoundCycle()
            }
        }
    }

    private val cardRanks = listOf("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")
    private val cardSuits = listOf("SPADES", "HEARTS", "DIAMONDS", "CLUBS")

    private suspend fun runRoundCycle() {
        // 1. BETTING_OPEN (15s)
        currentRoundNumber = MemoryDataStore.nextRoundNumber()
        currentRoundId = "dt_${currentRoundNumber}_${UUID.randomUUID().toString().take(6)}"
        serverSeed = ProvablyFair.generateServerSeed()
        serverSeedHash = ProvablyFair.sha256("$serverSeed:$currentRoundNumber")
        status = RoundStatus.BETTING_OPEN
        activeBets.clear()

        for (sec in 15 downTo 1) {
            broadcastState(remainingMs = sec * 1000L)
            delay(1000)
        }

        // 2. BETTING_CLOSED & DEAL (2s)
        status = RoundStatus.BETTING_CLOSED
        broadcastState(remainingMs = 2000)
        delay(2000)

        // 3. RESULT (3s)
        val (dVal, tVal) = ProvablyFair.deriveDragonTiger(serverSeed, currentRoundNumber)
        dragonCard = Card(rank = cardRanks[dVal - 1], suit = cardSuits[(dVal + 1) % 4], value = dVal)
        tigerCard = Card(rank = cardRanks[tVal - 1], suit = cardSuits[(tVal + 2) % 4], value = tVal)

        winningArea = when {
            dVal > tVal -> DragonTigerBetArea.DRAGON
            tVal > dVal -> DragonTigerBetArea.TIGER
            else -> DragonTigerBetArea.TIE
        }
        recentOutcomes.add(0, winningArea!!)
        if (recentOutcomes.size > 20) recentOutcomes.removeAt(recentOutcomes.size - 1)

        status = RoundStatus.RESULT
        broadcastState(remainingMs = 3000)
        delay(3000)

        // 4. SETTLING
        status = RoundStatus.SETTLING
        settleRound(winningArea!!)
        status = RoundStatus.SETTLED
        broadcastState(remainingMs = 1000)
        delay(1000)
    }

    private fun settleRound(winner: DragonTigerBetArea) {
        val multiplier = if (winner == DragonTigerBetArea.TIE) 8.0 else 2.0
        for ((userId, bets) in activeBets) {
            for (bet in bets) {
                if (bet.area == winner) {
                    val payoutPaise = (bet.amountPaise * multiplier).toLong()
                    val payoutIdempotency = "win_${bet.idempotencyKey}"
                    FinancialService.creditWinnings(
                        userId = userId,
                        amountPaise = payoutPaise,
                        gameId = "dragon_tiger",
                        roundId = currentRoundId,
                        idempotencyKey = payoutIdempotency
                    )

                    val updatedBal = FinancialService.getWalletBalance(userId)
                    wsHub.sendToUser(
                        userId,
                        Json.encodeToString(
                            WsMessage(
                                event = "payout_credited",
                                gameId = "dragon_tiger",
                                data = "{\"won\":true,\"payoutPaise\":$payoutPaise,\"newBalance\":${updatedBal.totalPaise}}"
                            )
                        )
                    )
                }
            }
        }
    }

    fun placeBet(userId: String, area: DragonTigerBetArea, amountPaise: Long, idempotencyKey: String): Result<WalletBalance> {
        if (status != RoundStatus.BETTING_OPEN) {
            return Result.failure(IllegalStateException("BETTING_CLOSED"))
        }
        val debitRes = FinancialService.debitForBet(
            userId = userId,
            amountPaise = amountPaise,
            gameId = "dragon_tiger",
            roundId = currentRoundId,
            idempotencyKey = idempotencyKey
        )
        if (debitRes.isSuccess) {
            val userBets = activeBets.getOrPut(userId) { CopyOnWriteArrayList() }
            userBets.add(DragonTigerBet(userId, area, amountPaise, idempotencyKey))
        }
        return debitRes
    }

    fun getState(remainingMs: Long = 0): DragonTigerState {
        val isRevealed = status == RoundStatus.RESULT || status == RoundStatus.SETTLING || status == RoundStatus.SETTLED
        return DragonTigerState(
            roundId = currentRoundId,
            roundNumber = currentRoundNumber,
            status = status,
            serverSeedHash = serverSeedHash,
            remainingMs = remainingMs,
            dragonCard = if (isRevealed) dragonCard else null,
            tigerCard = if (isRevealed) tigerCard else null,
            winningArea = if (isRevealed) winningArea else null,
            recentOutcomes = recentOutcomes.toList()
        )
    }

    private fun broadcastState(remainingMs: Long) {
        val st = getState(remainingMs)
        val json = Json.encodeToString(
            WsMessage(
                event = "game_state",
                gameId = "dragon_tiger",
                room = "game:dragon_tiger",
                data = Json.encodeToString(st)
            )
        )
        wsHub.broadcastToRoom("game:dragon_tiger", json)
    }
}
