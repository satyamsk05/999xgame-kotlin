package com.ingames.games.core

import kotlinx.serialization.Serializable

@Serializable
data class GameConfig(
    val gameId: String,
    val minBet: Double = 1.0,
    val maxBet: Double = 10000.0,
    val houseEdge: Double = 0.03,
    val roundDurationSeconds: Int = 15,
    val enabled: Boolean = true
)
