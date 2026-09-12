package com.ingames.games.games.coinflip

import kotlinx.serialization.Serializable

@Serializable
data class CoinFlipPlayer(
    val userId: String,
    val username: String
)
