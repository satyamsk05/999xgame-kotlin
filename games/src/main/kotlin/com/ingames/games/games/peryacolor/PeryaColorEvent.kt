package com.ingames.games.games.peryacolor

import kotlinx.serialization.Serializable

@Serializable
sealed class PeryaColorEvent {
    @Serializable
    data class RoundCreated(val roundId: String) : PeryaColorEvent()
    @Serializable
    data class ResultSet(val roundId: String, val result: PeryaColorResult) : PeryaColorEvent()
}
