package com.ingames.games.games.coinflip

import kotlinx.serialization.Serializable

@Serializable
data class CoinFlipRound(
    val roundId: String,
    val serverSeed: String,
    val hash: String,
    val result: String? = null
)
