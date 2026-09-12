package com.ingames.games.games.ringoffortune

import kotlinx.serialization.Serializable

@Serializable
data class RingOfFortunePlayer(
    val userId: String,
    val username: String
)
