package com.ingames.games.games.keno

interface KenoRepository {
    suspend fun saveRound(round: KenoRound)
    suspend fun getRound(roundId: String): KenoRound?
}
