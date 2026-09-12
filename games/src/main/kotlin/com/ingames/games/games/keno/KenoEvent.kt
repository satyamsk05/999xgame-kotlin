package com.ingames.games.games.keno

import kotlinx.serialization.Serializable

@Serializable
sealed class KenoEvent {
    @Serializable
    data class RoundCreated(val roundId: String) : KenoEvent()
    @Serializable
    data class ResultSet(val roundId: String, val result: KenoResult) : KenoEvent()
}
