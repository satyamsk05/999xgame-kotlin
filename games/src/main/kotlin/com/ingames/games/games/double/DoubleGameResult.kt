package com.ingames.games.games.double

import kotlinx.serialization.Serializable

@Serializable
data class DoubleGameResult(
    val roundId: String,
    val outcome: String,
    val multiplier: Double = 1.0
)
