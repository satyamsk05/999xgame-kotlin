package com.ingames.games.games.limbo

import kotlinx.serialization.Serializable

@Serializable
data class LimboBet(
    val betId: String,
    val userId: String,
    val amount: Double,
    val selection: String
)
