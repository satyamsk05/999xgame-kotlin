package com.ingames.games.games.peryacolor

class PeryaColorService(private val repository: PeryaColorRepository) {
    suspend fun createRound(roundId: String): PeryaColorRound {
        val round = PeryaColorRound(roundId = roundId, serverSeed = "", hash = "")
        repository.saveRound(round)
        return round
    }
}
