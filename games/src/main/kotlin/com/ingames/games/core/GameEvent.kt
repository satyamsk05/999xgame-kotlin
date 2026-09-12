package com.ingames.games.core

import kotlinx.serialization.Serializable

@Serializable
sealed class GameEvent {
    @Serializable
    data class RoundStarted(val roundId: String, val gameId: String, val startTime: Long) : GameEvent()
    
    @Serializable
    data class BetPlaced(val bet: GameBet) : GameEvent()
    
    @Serializable
    data class RoundEnded(val roundId: String, val result: GameResult) : GameEvent()

    @Serializable
    data class ErrorOccurred(val code: String, val message: String) : GameEvent()
}
