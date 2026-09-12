package com.ingames.games.games.coinflip

class CoinFlipValidator {
    fun validateBet(bet: CoinFlipBet): Boolean = bet.amount > 0
}
