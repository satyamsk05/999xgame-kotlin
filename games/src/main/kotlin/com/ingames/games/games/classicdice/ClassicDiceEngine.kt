package com.ingames.games.games.classicdice

import com.ingames.games.common.SeedProvider
import com.ingames.games.core.GameEngine

class ClassicDiceEngine : GameEngine<ClassicDiceConfig, ClassicDiceState, ClassicDiceBet, ClassicDiceResult> {
    override fun initialize(config: ClassicDiceConfig): ClassicDiceState {
        return ClassicDiceState(roundId = "dice_" + System.currentTimeMillis(), startTime = System.currentTimeMillis())
    }

    override fun validateBet(state: ClassicDiceState, bet: ClassicDiceBet): Boolean {
        return bet.amount > 0
    }

    override fun processRound(state: ClassicDiceState, bets: List<ClassicDiceBet>): ClassicDiceResult {
        val serverSeed = SeedProvider.generateServerSeed()
        val outcomeHash = SeedProvider.hashSeed(serverSeed + ":" + state.roundId)
        val rollValue = (outcomeHash.take(4).toInt(16) % 10000) / 100.0
        val outcome = "%.2f".format(rollValue)
        val multiplier = 1.98
        return ClassicDiceResult(roundId = state.roundId, outcome = outcome, multiplier = multiplier)
    }
}
