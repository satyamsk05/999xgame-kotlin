package com.ingames.games.games.mines

import kotlinx.serialization.Serializable

@Serializable
data class MinesRound(
    val roundId: String,
    val serverSeed: String,
    val hash: String,
    val result: String? = null
)
