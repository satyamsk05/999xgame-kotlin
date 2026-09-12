package com.ingames.games.games.keno

class KenoPayout {
    fun calculate(bet: KenoBet, result: KenoResult): Double = bet.amount * result.multiplier
}
