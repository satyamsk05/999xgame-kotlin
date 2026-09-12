package com.ingames.games.games.keno

import kotlinx.serialization.Serializable

@Serializable
data class KenoBet(
    val betId: String,
    val userId: String,
    val amount: Double,
    val selection: String
)
