package com.ingames.games.games.limbo

class LimboPayout {
    fun calculate(bet: LimboBet, result: LimboResult): Double = bet.amount * result.multiplier
}
