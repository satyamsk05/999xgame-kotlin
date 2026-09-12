package com.ingames.games.games.mines

import com.ingames.games.common.SeedProvider
import com.ingames.games.core.GameEngine

class MinesEngine : GameEngine<MinesConfig, MinesState, MinesBet, MinesResult> {
    override fun initialize(config: MinesConfig): MinesState {
        return MinesState(roundId = "mines_" + System.currentTimeMillis(), startTime = System.currentTimeMillis())
    }

    override fun validateBet(state: MinesState, bet: MinesBet): Boolean {
        return bet.amount > 0
    }

    override fun processRound(state: MinesState, bets: List<MinesBet>): MinesResult {
        val minePositions = (0..24).toList().shuffled().take(3).sorted()
        val outcome = minePositions.joinToString(",")
        return MinesResult(roundId = state.roundId, outcome = outcome, multiplier = 1.48)
    }
}
