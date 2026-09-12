package com.ingames.games.games.mines

import kotlinx.serialization.Serializable

@Serializable
data class MinesBet(
    val betId: String,
    val userId: String,
    val amount: Double,
    val selection: String
)
