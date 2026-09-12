package com.ingames.games.games.classicdice

import kotlinx.serialization.Serializable

@Serializable
sealed class ClassicDiceEvent {
    @Serializable
    data class RoundCreated(val roundId: String) : ClassicDiceEvent()
    @Serializable
    data class ResultSet(val roundId: String, val result: ClassicDiceResult) : ClassicDiceEvent()
}
