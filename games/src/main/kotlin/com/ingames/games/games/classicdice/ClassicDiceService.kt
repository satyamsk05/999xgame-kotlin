package com.ingames.games.games.classicdice

class ClassicDiceService(private val repository: ClassicDiceRepository) {
    suspend fun createRound(roundId: String): ClassicDiceRound {
        val round = ClassicDiceRound(roundId = roundId, serverSeed = "", hash = "")
        repository.saveRound(round)
        return round
    }
}
