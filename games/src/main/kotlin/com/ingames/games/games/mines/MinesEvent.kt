package com.ingames.games.games.mines

import kotlinx.serialization.Serializable

@Serializable
sealed class MinesEvent {
    @Serializable
    data class RoundCreated(val roundId: String) : MinesEvent()
    @Serializable
    data class ResultSet(val roundId: String, val result: MinesResult) : MinesEvent()
}
