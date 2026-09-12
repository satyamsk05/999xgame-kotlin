package com.ingames.games.games.double

interface DoubleGameRepository {
    suspend fun saveRound(round: DoubleGameRound)
    suspend fun getRound(roundId: String): DoubleGameRound?
}
