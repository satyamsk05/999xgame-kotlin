package com.ingames.games.games.classicdice

sealed class ClassicDiceError(message: String) : Exception(message) {
    class InvalidBet(msg: String) : ClassicDiceError(msg)
}
