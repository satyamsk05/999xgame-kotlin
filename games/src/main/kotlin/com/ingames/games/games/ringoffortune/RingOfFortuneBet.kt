package com.ingames.games.games.ringoffortune

import kotlinx.serialization.Serializable

@Serializable
data class RingOfFortuneBet(
    val betId: String,
    val userId: String,
    val amount: Double,
    val selection: String
)
