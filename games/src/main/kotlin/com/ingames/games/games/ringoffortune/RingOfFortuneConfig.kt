package com.ingames.games.games.ringoffortune

import com.ingames.games.core.GameConfig
import kotlinx.serialization.Serializable

@Serializable
data class RingOfFortuneConfig(
    val baseConfig: GameConfig = GameConfig(gameId = RingOfFortuneConstants.GAME_ID)
)
