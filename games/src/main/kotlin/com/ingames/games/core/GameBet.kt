package com.ingames.games.core

import kotlinx.serialization.Serializable

@Serializable
enum class BetStatus {
    PENDING,
    WON,
    LOST,
    CANCELLED,
    REFUNDED
}

@Serializable
data class GameBet(
    val betId: String,
    val userId: String,
    val roundId: String,
    val gameId: String,
    val amount: Double,
    val selection: String,
    val multiplier: Double = 1.0,
    val payout: Double = 0.0,
    val status: BetStatus = BetStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis()
)
