package com.ingames.games.games.ringoffortune

interface RingOfFortuneRepository {
    suspend fun saveRound(round: RingOfFortuneRound)
    suspend fun getRound(roundId: String): RingOfFortuneRound?
}
