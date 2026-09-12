package com.ingames.games.games.double

import com.ingames.games.core.GameConfig
import kotlinx.serialization.Serializable

@Serializable
data class DoubleGameConfig(
    val baseConfig: GameConfig = GameConfig(gameId = DoubleGameConstants.GAME_ID)
)
