package com.ingames.games.games.double

import com.ingames.games.common.SeedProvider
import com.ingames.games.core.GameEngine

class DoubleGameEngine : GameEngine<DoubleGameConfig, DoubleGameState, DoubleGameBet, DoubleGameResult> {
    override fun initialize(config: DoubleGameConfig): DoubleGameState {
        return DoubleGameState(roundId = "dbl_" + System.currentTimeMillis(), startTime = System.currentTimeMillis())
    }

    override fun validateBet(state: DoubleGameState, bet: DoubleGameBet): Boolean {
        return bet.amount > 0
    }

    override fun processRound(state: DoubleGameState, bets: List<DoubleGameBet>): DoubleGameResult {
        val serverSeed = SeedProvider.generateServerSeed()
        val hash = SeedProvider.hashSeed(serverSeed + ":" + state.roundId)
        val rollSlot = hash.take(4).toInt(16) % 15
        val (color, mult) = when (rollSlot) {
            0 -> Pair("WHITE", 14.0)
            in 1..7 -> Pair("RED", 2.0)
            else -> Pair("BLACK", 2.0)
        }
        return DoubleGameResult(roundId = state.roundId, outcome = "$color ($rollSlot)", multiplier = mult)
    }
}
