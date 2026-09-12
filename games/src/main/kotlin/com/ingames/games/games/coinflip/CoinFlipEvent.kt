package com.ingames.games.games.coinflip

import kotlinx.serialization.Serializable

@Serializable
sealed class CoinFlipEvent {
    @Serializable
    data class RoundCreated(val roundId: String) : CoinFlipEvent()
    @Serializable
    data class ResultSet(val roundId: String, val result: CoinFlipResult) : CoinFlipEvent()
}
