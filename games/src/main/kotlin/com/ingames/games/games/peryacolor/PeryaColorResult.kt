package com.ingames.games.games.peryacolor

import kotlinx.serialization.Serializable

@Serializable
data class PeryaColorResult(
    val roundId: String,
    val outcome: String,
    val multiplier: Double = 1.0
)
