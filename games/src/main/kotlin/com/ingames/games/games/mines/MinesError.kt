package com.ingames.games.games.mines

sealed class MinesError(message: String) : Exception(message) {
    class InvalidBet(msg: String) : MinesError(msg)
}
