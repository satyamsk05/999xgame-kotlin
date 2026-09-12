package com.ingames.games.games.classicdice

import kotlinx.serialization.Serializable

@Serializable
data class ClassicDicePlayer(
    val userId: String,
    val username: String
)
