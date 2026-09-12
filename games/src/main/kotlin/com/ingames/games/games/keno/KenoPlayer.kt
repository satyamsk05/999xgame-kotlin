package com.ingames.games.games.keno

import kotlinx.serialization.Serializable

@Serializable
data class KenoPlayer(
    val userId: String,
    val username: String
)
