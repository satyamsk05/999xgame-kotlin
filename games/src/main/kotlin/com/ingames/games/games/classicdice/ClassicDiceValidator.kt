package com.ingames.games.games.classicdice

class ClassicDiceValidator {
    fun validateBet(bet: ClassicDiceBet): Boolean = bet.amount > 0
}
