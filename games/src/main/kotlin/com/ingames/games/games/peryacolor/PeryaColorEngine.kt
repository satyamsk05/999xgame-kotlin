package com.ingames.games.games.peryacolor

import com.ingames.games.common.SeedProvider
import com.ingames.games.core.GameEngine

class PeryaColorEngine : GameEngine<PeryaColorConfig, PeryaColorState, PeryaColorBet, PeryaColorResult> {
    private val colors = listOf("RED", "BLUE", "YELLOW", "GREEN", "PINK", "WHITE")

    override fun initialize(config: PeryaColorConfig): PeryaColorState {
        return PeryaColorState(roundId = "perya_" + System.currentTimeMillis(), startTime = System.currentTimeMillis())
    }

    override fun validateBet(state: PeryaColorState, bet: PeryaColorBet): Boolean {
        return bet.amount > 0
    }

    override fun processRound(state: PeryaColorState, bets: List<PeryaColorBet>): PeryaColorResult {
        val serverSeed = SeedProvider.generateServerSeed()
        val hash = SeedProvider.hashSeed(serverSeed + ":" + state.roundId)
        val c1 = colors[hash.substring(0, 2).toInt(16) % 6]
        val c2 = colors[hash.substring(2, 4).toInt(16) % 6]
        val c3 = colors[hash.substring(4, 6).toInt(16) % 6]
        val outcome = "$c1,$c2,$c3"
        return PeryaColorResult(roundId = state.roundId, outcome = outcome, multiplier = 2.0)
    }
}
