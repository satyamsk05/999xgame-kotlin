package com.ingames.games.core

interface GameScheduler {
    fun scheduleNextRound(gameId: String, delayMs: Long)
    fun cancelScheduledRound(gameId: String)
}
