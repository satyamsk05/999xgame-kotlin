package com.ingames.games.games.limbo

import com.ingames.games.core.GameConfig
import kotlinx.serialization.Serializable

@Serializable
data class LimboConfig(
    val baseConfig: GameConfig = GameConfig(gameId = LimboConstants.GAME_ID)
)
