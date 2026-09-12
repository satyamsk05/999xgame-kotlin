package com.ingames.games.games.limbo

import com.ingames.games.common.SeedProvider
import com.ingames.games.core.GameEngine

class LimboEngine : GameEngine<LimboConfig, LimboState, LimboBet, LimboResult> {
    override fun initialize(config: LimboConfig): LimboState {
        return LimboState(roundId = "limbo_" + System.currentTimeMillis(), startTime = System.currentTimeMillis())
    }

    override fun validateBet(state: LimboState, bet: LimboBet): Boolean {
        return bet.amount > 0
    }

    override fun processRound(state: LimboState, bets: List<LimboBet>): LimboResult {
        val serverSeed = SeedProvider.generateServerSeed()
        val hash = SeedProvider.hashSeed(serverSeed + ":" + state.roundId)
        val rawInt = hash.take(8).toLong(16)
        val mult = maxOf(1.01, 99.0 / (1.0 + (rawInt % 100)))
        return LimboResult(roundId = state.roundId, outcome = "%.2fx".format(mult), multiplier = mult)
    }
}
