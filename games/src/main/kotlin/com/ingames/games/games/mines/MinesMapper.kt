package com.ingames.games.games.mines

class MinesMapper {
    fun toDto(round: MinesRound): String = round.roundId
}
