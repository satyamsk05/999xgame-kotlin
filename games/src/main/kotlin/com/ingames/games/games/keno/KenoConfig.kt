package com.ingames.games.games.keno

import com.ingames.games.core.GameConfig
import kotlinx.serialization.Serializable

@Serializable
data class KenoConfig(
    val baseConfig: GameConfig = GameConfig(gameId = KenoConstants.GAME_ID)
)
