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

class CrushEngine(
    private val scope: CoroutineScope,
    private val wsHub: WebSocketHub
) {
    private val logger = LoggerFactory.getLogger(CrushEngine::class.java)

    var currentRoundNumber: Long = MemoryDataStore.nextRoundNumber()
        private set
    var currentRoundId: String = "cru_${currentRoundNumber}_${UUID.randomUUID().toString().take(6)}"
        private set
    private var serverSeed: String = ProvablyFair.generateServerSeed()
    var serverSeedHash: String = ProvablyFair.sha256("$serverSeed:$currentRoundNumber")
        private set
    var status: RoundStatus = RoundStatus.BETTING_OPEN
        private set

    var currentMultiplier: Double = 1.00
        private set
    var crashPoint: Double = 1.00
        private set

    val recentCrashHistory = CopyOnWriteArrayList<Double>(listOf(2.45, 1.12, 5.80, 1.00, 3.20, 12.4, 1.88))
    private val activeBets = ConcurrentHashMap<String, CrushBet>()

    data class CrushBet(
        val userId: String,
        val amountPaise: Long,
        val autoCashoutMultiplier: Double?,
        val idempotencyKey: String,
        var cashedOut: Boolean = false,
        var cashedOutMultiplier: Double = 0.0
    )

    fun start() {
        scope.launch {
            while (isActive) {
                runRoundCycle()
            }
        }
    }

    private suspend fun runRoundCycle() {
        // 1. BETTING_OPEN (8 seconds)
        currentRoundNumber = MemoryDataStore.nextRoundNumber()
        currentRoundId = "cru_${currentRoundNumber}_${UUID.randomUUID().toString().take(6)}"
        serverSeed = ProvablyFair.generateServerSeed()
        serverSeedHash = ProvablyFair.sha256("$serverSeed:$currentRoundNumber")
        crashPoint = ProvablyFair.deriveCrashPoint(serverSeed, currentRoundNumber)
        status = RoundStatus.BETTING_OPEN
        currentMultiplier = 1.00
        activeBets.clear()

        for (sec in 8 downTo 1) {
            broadcastState(remainingMs = sec * 1000L)
            delay(1000)
        }

        // 2. FLYING (Multiplier curves upwards until crash point)
        status = RoundStatus.RESULT // In flight
        val startTime = System.currentTimeMillis()

        while (currentCoroutineContext().isActive) {
            val elapsedSec = (System.currentTimeMillis() - startTime) / 1000.0
            // Formula: M(t) = e^(0.06 * t)
            val mult = kotlin.math.floor(Math.exp(0.06 * elapsedSec * 1.5) * 100.0) / 100.0
            currentMultiplier = mult

            // Check auto-cashouts
            for ((userId, bet) in activeBets) {
                if (!bet.cashedOut && bet.autoCashoutMultiplier != null && mult >= bet.autoCashoutMultiplier) {
                    cashout(userId)
                }
            }

            if (currentMultiplier >= crashPoint) {
                currentMultiplier = crashPoint
                break
            }

            broadcastState(remainingMs = 0)
            delay(75) // 75ms tick
        }

        // 3. CRASHED / SETTLING
        status = RoundStatus.SETTLED
        recentCrashHistory.add(0, crashPoint)
        if (recentCrashHistory.size > 20) recentCrashHistory.removeAt(recentCrashHistory.size - 1)

        broadcastState(remainingMs = 3000)
        delay(3000)
    }

    fun placeBet(userId: String, amountPaise: Long, autoCashout: Double?, idempotencyKey: String): Result<WalletBalance> {
        if (status != RoundStatus.BETTING_OPEN) {
            return Result.failure(IllegalStateException("BETTING_CLOSED"))
        }
        val debitRes = FinancialService.debitForBet(
            userId = userId,
            amountPaise = amountPaise,
            gameId = "crush",
            roundId = currentRoundId,
            idempotencyKey = idempotencyKey
        )
        if (debitRes.isSuccess) {
            activeBets[userId] = CrushBet(
                userId = userId,
                amountPaise = amountPaise,
                autoCashoutMultiplier = autoCashout,
                idempotencyKey = idempotencyKey
            )
        }
        return debitRes
    }

    @Synchronized
    fun cashout(userId: String): Result<Double> {
        if (status != RoundStatus.RESULT) {
            return Result.failure(IllegalStateException("ROUND_NOT_FLYING"))
        }
        val bet = activeBets[userId] ?: return Result.failure(IllegalStateException("NO_ACTIVE_BET"))
        if (bet.cashedOut) return Result.success(bet.cashedOutMultiplier)

        val multiplier = currentMultiplier
        bet.cashedOut = true
        bet.cashedOutMultiplier = multiplier

        val payoutPaise = (bet.amountPaise * multiplier).toLong()
        val payoutIdempotency = "win_${bet.idempotencyKey}"

        FinancialService.creditWinnings(
            userId = userId,
            amountPaise = payoutPaise,
            gameId = "crush",
            roundId = currentRoundId,
            idempotencyKey = payoutIdempotency
        )

        val updatedBal = FinancialService.getWalletBalance(userId)
        wsHub.sendToUser(
            userId,
            Json.encodeToString(
                WsMessage(
                    event = "payout_credited",
                    gameId = "crush",
                    data = "{\"won\":true,\"multiplier\":$multiplier,\"payoutPaise\":$payoutPaise,\"newBalance\":${updatedBal.totalPaise}}"
                )
            )
        )

        return Result.success(multiplier)
    }

    fun getState(remainingMs: Long = 0): CrushState {
        return CrushState(
            roundId = currentRoundId,
            roundNumber = currentRoundNumber,
            status = status,
            serverSeedHash = serverSeedHash,
            currentMultiplier = currentMultiplier,
            crashPoint = if (status == RoundStatus.SETTLED) crashPoint else null,
            remainingMs = remainingMs,
            recentCrashPoints = recentCrashHistory.toList()
        )
    }

    private fun broadcastState(remainingMs: Long) {
        val st = getState(remainingMs)
        val json = Json.encodeToString(
            WsMessage(
                event = "game_state",
                gameId = "crush",
                room = "game:crush",
                data = Json.encodeToString(st)
            )
        )
        wsHub.broadcastToRoom("game:crush", json)
    }
}
