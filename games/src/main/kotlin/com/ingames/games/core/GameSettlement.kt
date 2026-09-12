package com.ingames.games.core

import kotlinx.serialization.Serializable

@Serializable
data class GameSettlement(
    val roundId: String,
    val totalBetsCount: Int,
    val totalBetAmount: Double,
    val totalPayoutAmount: Double,
    val houseProfit: Double,
    val settledAt: Long = System.currentTimeMillis()
)
