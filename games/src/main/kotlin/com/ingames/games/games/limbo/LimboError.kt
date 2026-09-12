package com.ingames.games.games.limbo

sealed class LimboError(message: String) : Exception(message) {
    class InvalidBet(msg: String) : LimboError(msg)
}
