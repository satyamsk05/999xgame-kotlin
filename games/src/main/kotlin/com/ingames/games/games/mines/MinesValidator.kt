package com.ingames.games.games.mines

class MinesValidator {
    fun validateBet(bet: MinesBet): Boolean = bet.amount > 0
}
