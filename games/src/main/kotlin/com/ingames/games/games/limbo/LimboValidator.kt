package com.ingames.games.games.limbo

class LimboValidator {
    fun validateBet(bet: LimboBet): Boolean = bet.amount > 0
}
