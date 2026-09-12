package com.ingames.games.games.ringoffortune

import kotlinx.serialization.Serializable

@Serializable
data class RingOfFortuneRound(
    val roundId: String,
    val serverSeed: String,
    val hash: String,
    val result: String? = null
)
