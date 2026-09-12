package com.ingames.games.games.ringoffortune

import com.ingames.games.core.Game
import com.ingames.games.core.GameStatus

class RingOfFortuneGame : Game {
    override val id: String = RingOfFortuneConstants.GAME_ID
    override val name: String = RingOfFortuneConstants.GAME_NAME
    override val category: String = "CASINO"
    override val status: GameStatus = GameStatus.ACTIVE
}
