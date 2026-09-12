package com.ingames.games.games.classicdice

interface ClassicDiceRepository {
    suspend fun saveRound(round: ClassicDiceRound)
    suspend fun getRound(roundId: String): ClassicDiceRound?
}
