package com.ingames.games.games.classicdice

import kotlinx.serialization.Serializable

@Serializable
data class ClassicDiceRound(
    val roundId: String,
    val serverSeed: String,
    val hash: String,
    val result: String? = null
)
