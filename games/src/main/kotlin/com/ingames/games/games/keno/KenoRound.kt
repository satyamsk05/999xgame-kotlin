package com.ingames.games.games.keno

import kotlinx.serialization.Serializable

@Serializable
data class KenoRound(
    val roundId: String,
    val serverSeed: String,
    val hash: String,
    val result: String? = null
)
