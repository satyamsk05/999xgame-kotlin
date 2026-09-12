package com.ingames.games.games.peryacolor

import kotlinx.serialization.Serializable

@Serializable
data class PeryaColorBet(
    val betId: String,
    val userId: String,
    val amount: Double,
    val selection: String
)
