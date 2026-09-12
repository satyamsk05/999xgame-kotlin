package com.ingames.games.core

import kotlinx.serialization.Serializable

@Serializable
data class GameRound(
    val roundId: String,
    val gameId: String,
    val phase: RoundPhase,
    val seed: String,
    val hash: String,
    val result: String? = null,
    val createdAt: Long,
    val closedAt: Long? = null
)
