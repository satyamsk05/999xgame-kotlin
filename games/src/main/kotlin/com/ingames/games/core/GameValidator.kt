package com.ingames.games.core

interface GameValidator<B : GameBet> {
    fun validate(bet: B, config: GameConfig): Result<Unit>
}
