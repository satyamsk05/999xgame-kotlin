package com.ingames.games.games.peryacolor

sealed class PeryaColorError(message: String) : Exception(message) {
    class InvalidBet(msg: String) : PeryaColorError(msg)
}
