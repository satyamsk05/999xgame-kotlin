package com.ingames.games.games.mines

class MinesService(private val repository: MinesRepository) {
    suspend fun createRound(roundId: String): MinesRound {
        val round = MinesRound(roundId = roundId, serverSeed = "", hash = "")
        repository.saveRound(round)
        return round
    }
}
