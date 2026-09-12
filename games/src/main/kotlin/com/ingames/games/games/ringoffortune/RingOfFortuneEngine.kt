package com.ingames.games.games.ringoffortune

import com.ingames.games.common.SeedProvider
import com.ingames.games.core.GameEngine

class RingOfFortuneEngine : GameEngine<RingOfFortuneConfig, RingOfFortuneState, RingOfFortuneBet, RingOfFortuneResult> {
    private val multipliers = listOf(1.0, 2.0, 5.0, 10.0, 20.0, 45.0)

    override fun initialize(config: RingOfFortuneConfig): RingOfFortuneState {
        return RingOfFortuneState(roundId = "ring_" + System.currentTimeMillis(), startTime = System.currentTimeMillis())
    }

    override fun validateBet(state: RingOfFortuneState, bet: RingOfFortuneBet): Boolean {
        return bet.amount > 0
    }

    override fun processRound(state: RingOfFortuneState, bets: List<RingOfFortuneBet>): RingOfFortuneResult {
        val serverSeed = SeedProvider.generateServerSeed()
        val hash = SeedProvider.hashSeed(serverSeed + ":" + state.roundId)
        val mult = multipliers[hash.take(2).toInt(16) % multipliers.size]
        return RingOfFortuneResult(roundId = state.roundId, outcome = mult.toString() + "x", multiplier = mult)
    }
}
