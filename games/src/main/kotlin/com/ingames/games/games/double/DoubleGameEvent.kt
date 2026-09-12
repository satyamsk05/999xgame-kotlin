package com.ingames.games.games.double

import kotlinx.serialization.Serializable

@Serializable
sealed class DoubleGameEvent {
    @Serializable
    data class RoundCreated(val roundId: String) : DoubleGameEvent()
    @Serializable
    data class ResultSet(val roundId: String, val result: DoubleGameResult) : DoubleGameEvent()
}
