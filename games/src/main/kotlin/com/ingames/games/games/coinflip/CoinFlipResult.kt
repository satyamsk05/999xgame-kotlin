package com.ingames.games.games.coinflip

import kotlinx.serialization.Serializable

@Serializable
data class CoinFlipResult(
    val roundId: String,
    val outcome: String,
    val multiplier: Double = 1.0
)
