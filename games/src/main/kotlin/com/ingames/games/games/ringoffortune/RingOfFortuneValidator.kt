package com.ingames.games.games.ringoffortune

class RingOfFortuneValidator {
    fun validateBet(bet: RingOfFortuneBet): Boolean = bet.amount > 0
}
