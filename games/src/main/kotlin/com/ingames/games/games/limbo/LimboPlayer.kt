package com.ingames.games.games.limbo

import kotlinx.serialization.Serializable

@Serializable
data class LimboPlayer(
    val userId: String,
    val username: String
)
