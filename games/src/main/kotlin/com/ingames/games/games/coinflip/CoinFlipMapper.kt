package com.ingames.games.games.coinflip

class CoinFlipMapper {
    fun toDto(round: CoinFlipRound): String = round.roundId
}
