package com.ingames.games.games.ringoffortune

import kotlinx.serialization.Serializable

@Serializable
data class RingOfFortuneResult(
    val roundId: String,
    val outcome: String,
    val multiplier: Double = 1.0
)
