package com.ingames.games.games.keno

class KenoMapper {
    fun toDto(round: KenoRound): String = round.roundId
}
