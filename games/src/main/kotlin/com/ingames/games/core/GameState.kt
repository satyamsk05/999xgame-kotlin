package com.ingames.games.core

import kotlinx.serialization.Serializable

@Serializable
enum class RoundPhase {
    CREATED,
    BETTING_OPEN,
    BETTING_CLOSED,
    EVALUATING,
    SETTLED,
    CANCELLED
}

interface GameState {
    val roundId: String
    val phase: RoundPhase
    val startTime: Long
}
