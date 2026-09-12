package com.ingames.games.games.peryacolor

import kotlinx.serialization.Serializable

@Serializable
data class PeryaColorPlayer(
    val userId: String,
    val username: String
)
