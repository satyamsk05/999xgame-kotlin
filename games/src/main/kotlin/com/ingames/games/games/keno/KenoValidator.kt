package com.ingames.games.games.keno

class KenoValidator {
    fun validateBet(bet: KenoBet): Boolean = bet.amount > 0
}
