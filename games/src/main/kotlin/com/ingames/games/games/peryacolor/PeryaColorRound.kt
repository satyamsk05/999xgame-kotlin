package com.ingames.games.games.peryacolor

import kotlinx.serialization.Serializable

@Serializable
data class PeryaColorRound(
    val roundId: String,
    val serverSeed: String,
    val hash: String,
    val result: String? = null
)
