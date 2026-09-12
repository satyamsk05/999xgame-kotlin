package com.ingames.games.games.ringoffortune

class RingOfFortuneService(private val repository: RingOfFortuneRepository) {
    suspend fun createRound(roundId: String): RingOfFortuneRound {
        val round = RingOfFortuneRound(roundId = roundId, serverSeed = "", hash = "")
        repository.saveRound(round)
        return round
    }
}
