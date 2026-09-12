package com.ingames.games.games.coinflip

class CoinFlipService(private val repository: CoinFlipRepository) {
    suspend fun createRound(roundId: String): CoinFlipRound {
        val round = CoinFlipRound(roundId = roundId, serverSeed = "", hash = "")
        repository.saveRound(round)
        return round
    }
}
