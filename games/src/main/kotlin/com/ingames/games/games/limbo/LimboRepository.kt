package com.ingames.games.games.limbo

interface LimboRepository {
    suspend fun saveRound(round: LimboRound)
    suspend fun getRound(roundId: String): LimboRound?
}
