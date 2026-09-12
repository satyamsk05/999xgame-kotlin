package com.ingames.games.games.keno

import com.ingames.games.common.SeedProvider
import com.ingames.games.core.GameEngine

class KenoEngine : GameEngine<KenoConfig, KenoState, KenoBet, KenoResult> {
    override fun initialize(config: KenoConfig): KenoState {
        return KenoState(roundId = "keno_" + System.currentTimeMillis(), startTime = System.currentTimeMillis())
    }

    override fun validateBet(state: KenoState, bet: KenoBet): Boolean {
        return bet.amount > 0
    }

    override fun processRound(state: KenoState, bets: List<KenoBet>): KenoResult {
        val serverSeed = SeedProvider.generateServerSeed()
        val hash = SeedProvider.hashSeed(serverSeed + ":" + state.roundId)
        val numbers = (1..40).toList().shuffled().take(10).sorted()
        val outcome = numbers.joinToString(",")
        return KenoResult(roundId = state.roundId, outcome = outcome, multiplier = 2.5)
    }
}
