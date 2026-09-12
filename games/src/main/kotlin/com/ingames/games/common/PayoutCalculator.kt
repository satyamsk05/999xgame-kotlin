package com.ingames.games.common

object PayoutCalculator {
    fun calculatePayout(betAmount: Double, multiplier: Double, houseEdge: Double): Double {
        val grossPayout = betAmount * multiplier
        return grossPayout * (1.0 - houseEdge)
    }
}
