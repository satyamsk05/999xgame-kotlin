package com.ingames.games.games.peryacolor

class PeryaColorValidator {
    fun validateBet(bet: PeryaColorBet): Boolean = bet.amount > 0
}
