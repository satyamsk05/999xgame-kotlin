package com.ingames.games.games.limbo

class LimboService(private val repository: LimboRepository) {
    suspend fun createRound(roundId: String): LimboRound {
        val round = LimboRound(roundId = roundId, serverSeed = "", hash = "")
        repository.saveRound(round)
        return round
    }
}
