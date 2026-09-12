package com.ingames.games.games.classicdice

import com.ingames.games.core.Game
import com.ingames.games.core.GameStatus

class ClassicDiceGame : Game {
    override val id: String = ClassicDiceConstants.GAME_ID
    override val name: String = ClassicDiceConstants.GAME_NAME
    override val category: String = "CASINO"
    override val status: GameStatus = GameStatus.ACTIVE
}
