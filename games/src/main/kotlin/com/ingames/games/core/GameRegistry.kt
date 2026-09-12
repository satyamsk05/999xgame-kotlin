package com.ingames.games.core

object GameRegistry {
    private val registeredGames = mutableMapOf<String, Game>()

    fun register(game: Game) {
        registeredGames[game.id] = game
    }

    fun getGame(gameId: String): Game? = registeredGames[gameId]

    fun getAllGames(): List<Game> = registeredGames.values.toList()
}
