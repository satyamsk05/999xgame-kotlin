package com.ingames.games.games.mines

import com.ingames.games.core.Game
import com.ingames.games.core.GameStatus

class MinesGame : Game {
    override val id: String = MinesConstants.GAME_ID
    override val name: String = MinesConstants.GAME_NAME
    override val category: String = "CASINO"
    override val status: GameStatus = GameStatus.ACTIVE
}
