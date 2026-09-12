package com.ingames.games.games.coinflip

import com.ingames.games.core.Game
import com.ingames.games.core.GameStatus

class CoinFlipGame : Game {
    override val id: String = CoinFlipConstants.GAME_ID
    override val name: String = CoinFlipConstants.GAME_NAME
    override val category: String = "CASINO"
    override val status: GameStatus = GameStatus.ACTIVE
}
