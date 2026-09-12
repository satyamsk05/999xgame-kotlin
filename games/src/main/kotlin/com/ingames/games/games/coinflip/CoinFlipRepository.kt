package com.ingames.games.games.coinflip

interface CoinFlipRepository {
    suspend fun saveRound(round: CoinFlipRound)
    suspend fun getRound(roundId: String): CoinFlipRound?
}
