package com.ingames.games.games.double

sealed class DoubleGameError(message: String) : Exception(message) {
    class InvalidBet(msg: String) : DoubleGameError(msg)
}
