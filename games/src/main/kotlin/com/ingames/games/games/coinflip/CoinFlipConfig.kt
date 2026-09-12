package com.ingames.games.games.coinflip

import com.ingames.games.core.GameConfig
import kotlinx.serialization.Serializable

@Serializable
data class CoinFlipConfig(
    val baseConfig: GameConfig = GameConfig(gameId = CoinFlipConstants.GAME_ID)
)
