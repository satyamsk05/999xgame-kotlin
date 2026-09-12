package com.ingames.games.games.keno

import kotlinx.serialization.Serializable

@Serializable
data class KenoResult(
    val roundId: String,
    val outcome: String,
    val multiplier: Double = 1.0
)
