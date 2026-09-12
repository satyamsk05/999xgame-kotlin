package com.ingames.games.games.mines

class MinesPayout {
    fun calculate(bet: MinesBet, result: MinesResult): Double = bet.amount * result.multiplier
}
