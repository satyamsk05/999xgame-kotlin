package com.ingames.games.games.double

class DoubleGamePayout {
    fun calculate(bet: DoubleGameBet, result: DoubleGameResult): Double = bet.amount * result.multiplier
}
