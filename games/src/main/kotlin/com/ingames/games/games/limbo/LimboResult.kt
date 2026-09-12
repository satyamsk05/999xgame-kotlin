package com.ingames.games.games.limbo

import kotlinx.serialization.Serializable

@Serializable
data class LimboResult(
    val roundId: String,
    val outcome: String,
    val multiplier: Double = 1.0
)
