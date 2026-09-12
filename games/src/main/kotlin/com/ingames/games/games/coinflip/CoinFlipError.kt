package com.ingames.games.games.coinflip

sealed class CoinFlipError(message: String) : Exception(message) {
    class InvalidBet(msg: String) : CoinFlipError(msg)
}
