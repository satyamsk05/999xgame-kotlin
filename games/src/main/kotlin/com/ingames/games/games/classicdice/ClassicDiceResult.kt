package com.ingames.games.games.classicdice

import kotlinx.serialization.Serializable

@Serializable
data class ClassicDiceResult(
    val roundId: String,
    val outcome: String,
    val multiplier: Double = 1.0
)
