package com.ingames.games.games.coinflip

class CoinFlipPayout {
    fun calculate(bet: CoinFlipBet, result: CoinFlipResult): Double = bet.amount * result.multiplier
}
