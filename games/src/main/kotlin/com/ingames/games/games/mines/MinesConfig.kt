package com.ingames.games.games.mines

import com.ingames.games.core.GameConfig
import kotlinx.serialization.Serializable

@Serializable
data class MinesConfig(
    val baseConfig: GameConfig = GameConfig(gameId = MinesConstants.GAME_ID)
)
