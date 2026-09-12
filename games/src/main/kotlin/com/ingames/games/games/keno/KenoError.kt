package com.ingames.games.games.keno

sealed class KenoError(message: String) : Exception(message) {
    class InvalidBet(msg: String) : KenoError(msg)
}
