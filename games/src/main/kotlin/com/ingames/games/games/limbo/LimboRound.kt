package com.ingames.games.games.limbo

import kotlinx.serialization.Serializable

@Serializable
data class LimboRound(
    val roundId: String,
    val serverSeed: String,
    val hash: String,
    val result: String? = null
)
