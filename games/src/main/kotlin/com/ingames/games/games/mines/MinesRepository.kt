package com.ingames.games.games.mines

interface MinesRepository {
    suspend fun saveRound(round: MinesRound)
    suspend fun getRound(roundId: String): MinesRound?
}
