package com.ingames.games.games.ringoffortune

import kotlinx.serialization.Serializable

@Serializable
sealed class RingOfFortuneEvent {
    @Serializable
    data class RoundCreated(val roundId: String) : RingOfFortuneEvent()
    @Serializable
    data class ResultSet(val roundId: String, val result: RingOfFortuneResult) : RingOfFortuneEvent()
}
