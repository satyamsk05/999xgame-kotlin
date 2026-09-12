package com.ingames.games.games.limbo

import com.ingames.games.core.Game
import com.ingames.games.core.GameStatus

class LimboGame : Game {
    override val id: String = LimboConstants.GAME_ID
    override val name: String = LimboConstants.GAME_NAME
    override val category: String = "CASINO"
    override val status: GameStatus = GameStatus.ACTIVE
}
