package com.ingames.games.games.classicdice

import kotlinx.serialization.Serializable

@Serializable
data class ClassicDiceBet(
    val betId: String,
    val userId: String,
    val amount: Double,
    val selection: String
)
