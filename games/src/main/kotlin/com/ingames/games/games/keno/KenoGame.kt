package com.ingames.games.games.keno

import com.ingames.games.core.Game
import com.ingames.games.core.GameStatus

class KenoGame : Game {
    override val id: String = KenoConstants.GAME_ID
    override val name: String = KenoConstants.GAME_NAME
    override val category: String = "CASINO"
    override val status: GameStatus = GameStatus.ACTIVE
}
