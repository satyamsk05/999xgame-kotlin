package com.ingames.games.core

import kotlinx.serialization.Serializable

@Serializable
data class GamePlayer(
    val userId: String,
    val username: String,
    val avatarUrl: String? = null,
    val balance: Double
)
