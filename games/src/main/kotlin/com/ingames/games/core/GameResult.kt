package com.ingames.games.core

import kotlinx.serialization.Serializable

@Serializable
data class GameResult(
    val roundId: String,
    val gameId: String,
    val outcome: String,
    val multipliers: Map<String, Double> = emptyMap(),
    val timestamp: Long = System.currentTimeMillis()
)
