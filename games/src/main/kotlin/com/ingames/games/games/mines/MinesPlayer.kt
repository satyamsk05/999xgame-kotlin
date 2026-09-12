package com.ingames.games.games.mines

import kotlinx.serialization.Serializable

@Serializable
data class MinesPlayer(
    val userId: String,
    val username: String
)
