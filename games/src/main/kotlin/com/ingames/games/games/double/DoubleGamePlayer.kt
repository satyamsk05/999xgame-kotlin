package com.ingames.games.games.double

import kotlinx.serialization.Serializable

@Serializable
data class DoubleGamePlayer(
    val userId: String,
    val username: String
)
