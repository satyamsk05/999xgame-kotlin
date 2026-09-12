package com.ingames.games.games.keno

class KenoService(private val repository: KenoRepository) {
    suspend fun createRound(roundId: String): KenoRound {
        val round = KenoRound(roundId = roundId, serverSeed = "", hash = "")
        repository.saveRound(round)
        return round
    }
}
