package com.ingames.games.games.peryacolor

import com.ingames.games.core.Game
import com.ingames.games.core.GameStatus

class PeryaColorGame : Game {
    override val id: String = PeryaColorConstants.GAME_ID
    override val name: String = PeryaColorConstants.GAME_NAME
    override val category: String = "CASINO"
    override val status: GameStatus = GameStatus.ACTIVE
}
