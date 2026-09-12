package com.ingames.games.games.coinflip

import kotlinx.serialization.Serializable

@Serializable
data class CoinFlipBet(
    val betId: String,
    val userId: String,
    val amount: Double,
    val selection: String
)
