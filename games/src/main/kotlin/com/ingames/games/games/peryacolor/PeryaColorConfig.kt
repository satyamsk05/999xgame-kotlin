package com.ingames.games.games.peryacolor

import com.ingames.games.core.GameConfig
import kotlinx.serialization.Serializable

@Serializable
data class PeryaColorConfig(
    val baseConfig: GameConfig = GameConfig(gameId = PeryaColorConstants.GAME_ID)
)
