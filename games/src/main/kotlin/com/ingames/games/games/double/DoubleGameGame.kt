package com.ingames.games.games.double

import com.ingames.games.core.Game
import com.ingames.games.core.GameStatus

class DoubleGameGame : Game {
    override val id: String = DoubleGameConstants.GAME_ID
    override val name: String = DoubleGameConstants.GAME_NAME
    override val category: String = "CASINO"
    override val status: GameStatus = GameStatus.ACTIVE
}
