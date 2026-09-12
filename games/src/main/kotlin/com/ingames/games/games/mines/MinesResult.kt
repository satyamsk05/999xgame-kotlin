package com.ingames.games.games.mines

import kotlinx.serialization.Serializable

@Serializable
data class MinesResult(
    val roundId: String,
    val outcome: String,
    val multiplier: Double = 1.0
)
