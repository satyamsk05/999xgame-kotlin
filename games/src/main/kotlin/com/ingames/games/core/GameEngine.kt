package com.ingames.games.core

interface GameEngine<C, S : GameState, B, R> {
    fun initialize(config: C): S
    fun validateBet(state: S, bet: B): Boolean
    fun processRound(state: S, bets: List<B>): R
}
