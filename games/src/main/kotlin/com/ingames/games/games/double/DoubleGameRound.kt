package com.ingames.games.games.double

import kotlinx.serialization.Serializable

@Serializable
data class DoubleGameRound(
    val roundId: String,
    val serverSeed: String,
    val hash: String,
    val result: String? = null
)
