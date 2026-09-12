package com.ingames.games.core

import kotlinx.serialization.Serializable

@Serializable
data class GameSession(
    val sessionId: String,
    val userId: String,
    val gameId: String,
    val activeRoundId: String?,
    val connectedAt: Long = System.currentTimeMillis()
)
