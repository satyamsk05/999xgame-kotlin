package com.ingames.games.games.double

class DoubleGameService(private val repository: DoubleGameRepository) {
    suspend fun createRound(roundId: String): DoubleGameRound {
        val round = DoubleGameRound(roundId = roundId, serverSeed = "", hash = "")
        repository.saveRound(round)
        return round
    }
}
