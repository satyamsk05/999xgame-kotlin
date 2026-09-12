package com.ingames.games.core

import kotlinx.serialization.Serializable

@Serializable
enum class GameStatus {
    ACTIVE,
    MAINTENANCE,
    DISABLED
}

interface Game {
    val id: String
    val name: String
    val category: String
    val status: GameStatus
}
