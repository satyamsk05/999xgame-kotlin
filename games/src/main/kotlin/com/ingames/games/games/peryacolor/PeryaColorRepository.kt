package com.ingames.games.games.peryacolor

interface PeryaColorRepository {
    suspend fun saveRound(round: PeryaColorRound)
    suspend fun getRound(roundId: String): PeryaColorRound?
}
