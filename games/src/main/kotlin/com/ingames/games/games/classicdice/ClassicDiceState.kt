package com.ingames.games.games.classicdice

import com.ingames.games.core.GameState
import com.ingames.games.core.RoundPhase
import kotlinx.serialization.Serializable

@Serializable
data class ClassicDiceState(
    override val roundId: String,
    override val phase: RoundPhase = RoundPhase.CREATED,
    override val startTime: Long
) : GameState
