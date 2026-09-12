package com.ingames.games.games.ringoffortune

class RingOfFortunePayout {
    fun calculate(bet: RingOfFortuneBet, result: RingOfFortuneResult): Double = bet.amount * result.multiplier
}
