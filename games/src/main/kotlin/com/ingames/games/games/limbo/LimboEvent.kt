package com.ingames.games.games.limbo

import kotlinx.serialization.Serializable

@Serializable
sealed class LimboEvent {
    @Serializable
    data class RoundCreated(val roundId: String) : LimboEvent()
    @Serializable
    data class ResultSet(val roundId: String, val result: LimboResult) : LimboEvent()
}
