package com.ingames.games.core

sealed class GameException(message: String, val errorCode: String) : Exception(message) {
    class InvalidBetException(message: String) : GameException(message, "INVALID_BET")
    class RoundClosedException(message: String) : GameException(message, "ROUND_CLOSED")
    class InsufficientBalanceException(message: String) : GameException(message, "INSUFFICIENT_BALANCE")
    class GameMaintenanceException(message: String) : GameException(message, "GAME_MAINTENANCE")
}
