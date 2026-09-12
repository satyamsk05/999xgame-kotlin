package com.ingames.games.games.coinflip

data class CoinFlipSettlement(
    val roundId: String,
    val totalBetAmount: Double,
    val totalPayout: Double
)
