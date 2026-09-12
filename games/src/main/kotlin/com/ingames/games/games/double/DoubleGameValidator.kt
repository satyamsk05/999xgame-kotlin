package com.ingames.games.games.double

class DoubleGameValidator {
    fun validateBet(bet: DoubleGameBet): Boolean = bet.amount > 0
}
