package com.ingames.games.games.ringoffortune

sealed class RingOfFortuneError(message: String) : Exception(message) {
    class InvalidBet(msg: String) : RingOfFortuneError(msg)
}
