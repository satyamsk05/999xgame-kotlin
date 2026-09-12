package com.ingames.games.games.classicdice

import com.ingames.games.core.GameConfig
import kotlinx.serialization.Serializable

@Serializable
data class ClassicDiceConfig(
    val baseConfig: GameConfig = GameConfig(gameId = ClassicDiceConstants.GAME_ID)
)
