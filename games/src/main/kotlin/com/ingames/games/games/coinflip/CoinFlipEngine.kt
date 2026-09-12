package com.ingames.games.games.coinflip

import com.ingames.games.common.SeedProvider
import com.ingames.games.core.GameEngine

class CoinFlipEngine : GameEngine<CoinFlipConfig, CoinFlipState, CoinFlipBet, CoinFlipResult> {
    override fun initialize(config: CoinFlipConfig): CoinFlipState {
        val serverSeed = SeedProvider.generateServerSeed()
        val hash = SeedProvider.hashSeed(serverSeed)
        return CoinFlipState(roundId = "cf_" + System.currentTimeMillis(), startTime = System.currentTimeMillis())
    }

    override fun validateBet(state: CoinFlipState, bet: CoinFlipBet): Boolean {
        return bet.amount > 0 && (bet.selection.equals("HEADS", ignoreCase = true) || bet.selection.equals("TAILS", ignoreCase = true))
    }

    override fun processRound(state: CoinFlipState, bets: List<CoinFlipBet>): CoinFlipResult {
        val serverSeed = SeedProvider.generateServerSeed()
        val outcomeHash = SeedProvider.hashSeed(serverSeed + ":" + state.roundId)
        val outcomeByte = outcomeHash.take(2).toInt(16)
        val outcome = if (outcomeByte % 2 == 0) "HEADS" else "TAILS"
        val multiplier = 1.96
        return CoinFlipResult(roundId = state.roundId, outcome = outcome, multiplier = multiplier)
    }
}
