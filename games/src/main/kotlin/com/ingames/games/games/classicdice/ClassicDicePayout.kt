package com.ingames.games.games.classicdice

class ClassicDicePayout {
    fun calculate(bet: ClassicDiceBet, result: ClassicDiceResult): Double = bet.amount * result.multiplier
}
