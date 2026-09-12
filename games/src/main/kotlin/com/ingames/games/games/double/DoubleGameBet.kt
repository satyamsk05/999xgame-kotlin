package com.ingames.games.games.double

import kotlinx.serialization.Serializable

@Serializable
data class DoubleGameBet(
    val betId: String,
    val userId: String,
    val amount: Double,
    val selection: String
)
