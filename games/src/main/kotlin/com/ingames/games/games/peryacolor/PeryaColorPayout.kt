package com.ingames.games.games.peryacolor

class PeryaColorPayout {
    fun calculate(bet: PeryaColorBet, result: PeryaColorResult): Double = bet.amount * result.multiplier
}
