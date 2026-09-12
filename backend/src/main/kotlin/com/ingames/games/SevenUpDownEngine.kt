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

class SevenUpDownEngine(
    private val scope: CoroutineScope,
    private val wsHub: WebSocketHub
) {
    private val logger = LoggerFactory.getLogger(SevenUpDownEngine::class.java)

    var currentRoundNumber: Long = MemoryDataStore.nextRoundNumber()
        private set
    var currentRoundId: String = "7ud_${currentRoundNumber}_${UUID.randomUUID().toString().take(6)}"
        private set
    private var serverSeed: String = ProvablyFair.generateServerSeed()
    var serverSeedHash: String = ProvablyFair.sha256("$serverSeed:$currentRoundNumber")
        private set
    var status: RoundStatus = RoundStatus.BETTING_OPEN
        private set

    private var die1: Int = 1
    private var die2: Int = 1
    private var winningArea: SevenUpDownBetArea = SevenUpDownBetArea.DOWN

    val recentHistory = CopyOnWriteArrayList<Int>(listOf(6, 7, 10, 4, 8, 7, 3, 11))
    private val activeBets = ConcurrentHashMap<String, MutableList<SevenUpDownBet>>()

    data class SevenUpDownBet(
        val userId: String,
        val area: SevenUpDownBetArea,
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

    private suspend fun runRoundCycle() {
        // 1. BETTING_OPEN (15 seconds)
        currentRoundNumber = MemoryDataStore.nextRoundNumber()
        currentRoundId = "7ud_${currentRoundNumber}_${UUID.randomUUID().toString().take(6)}"
        serverSeed = ProvablyFair.generateServerSeed()
        serverSeedHash = ProvablyFair.sha256("$serverSeed:$currentRoundNumber")
        status = RoundStatus.BETTING_OPEN
        activeBets.clear()

        broadcastState(remainingMs = 15000)

        for (sec in 15 downTo 1) {
            delay(1000)
            broadcastState(remainingMs = (sec - 1) * 1000L)
        }

        // 2. BETTING_CLOSED & ROLL (2 seconds)
        status = RoundStatus.BETTING_CLOSED
        broadcastState(remainingMs = 2000)
        delay(2000)

        // 3. RESULT (3 seconds)
        val (d1, d2) = ProvablyFair.deriveDice(serverSeed, currentRoundNumber)
        die1 = d1
        die2 = d2
        val total = d1 + d2
        winningArea = when {
            total < 7 -> SevenUpDownBetArea.DOWN
            total == 7 -> SevenUpDownBetArea.SEVEN
            else -> SevenUpDownBetArea.UP
        }
        recentHistory.add(0, total)
        if (recentHistory.size > 20) recentHistory.removeAt(recentHistory.size - 1)

        status = RoundStatus.RESULT
        broadcastState(remainingMs = 3000)
        delay(3000)

        // 4. SETTLING
        status = RoundStatus.SETTLING
        settleRound(winningArea)
        status = RoundStatus.SETTLED
        broadcastState(remainingMs = 1000)
        delay(1000)
    }

    private fun settleRound(area: SevenUpDownBetArea) {
        val multiplier = if (area == SevenUpDownBetArea.SEVEN) 5.0 else 2.0
        for ((userId, bets) in activeBets) {
            for (bet in bets) {
                if (bet.area == area) {
                    val payoutPaise = (bet.amountPaise * multiplier).toLong()
                    val payoutIdempotency = "win_${bet.idempotencyKey}"
                    FinancialService.creditWinnings(
                        userId = userId,
                        amountPaise = payoutPaise,
                        gameId = "seven_up_down",
                        roundId = currentRoundId,
                        idempotencyKey = payoutIdempotency
                    )
                    // Notify user directly
                    val updatedBal = FinancialService.getWalletBalance(userId)
                    wsHub.sendToUser(
                        userId,
                        Json.encodeToString(
                            WsMessage(
                                event = "payout_credited",
                                gameId = "seven_up_down",
                                data = "{\"won\":true,\"payoutPaise\":$payoutPaise,\"newBalance\":${updatedBal.totalPaise}}"
                            )
                        )
                    )
                }
            }
        }
    }

    fun placeBet(userId: String, area: SevenUpDownBetArea, amountPaise: Long, idempotencyKey: String): Result<WalletBalance> {
        if (status != RoundStatus.BETTING_OPEN) {
            return Result.failure(IllegalStateException("BETTING_CLOSED"))
        }
        val debitRes = FinancialService.debitForBet(
            userId = userId,
            amountPaise = amountPaise,
            gameId = "seven_up_down",
            roundId = currentRoundId,
            idempotencyKey = idempotencyKey
        )
        if (debitRes.isSuccess) {
            val userBets = activeBets.getOrPut(userId) { CopyOnWriteArrayList() }
            userBets.add(SevenUpDownBet(userId, area, amountPaise, idempotencyKey))
        }
        return debitRes
    }

    fun getState(remainingMs: Long = 0): SevenUpDownState {
        return SevenUpDownState(
            roundId = currentRoundId,
            roundNumber = currentRoundNumber,
            status = status,
            serverSeedHash = serverSeedHash,
            remainingMs = remainingMs,
            dice = if (status == RoundStatus.RESULT || status == RoundStatus.SETTLING || status == RoundStatus.SETTLED) listOf(die1, die2) else null,
            diceTotal = if (status == RoundStatus.RESULT || status == RoundStatus.SETTLING || status == RoundStatus.SETTLED) (die1 + die2) else null,
            winningArea = if (status == RoundStatus.RESULT || status == RoundStatus.SETTLING || status == RoundStatus.SETTLED) winningArea else null,
            recentOutcomes = recentHistory.toList(),
            totalBetsCount = activeBets.values.sumOf { it.size }
        )
    }

    private fun broadcastState(remainingMs: Long) {
        val st = getState(remainingMs)
        val json = Json.encodeToString(
            WsMessage(
                event = "game_state",
                gameId = "seven_up_down",
                room = "game:seven_up_down",
                data = Json.encodeToString(st)
            )
        )
        wsHub.broadcastToRoom("game:seven_up_down", json)
    }
}
