package com.ingames.games.games.coinflip

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CoinFlipEngineTest {

    @Test
    fun testEngineInitialization() {
        val engine = CoinFlipEngine()
        val config = CoinFlipConfig()
        val state = engine.initialize(config)
        assertNotNull(state)
        assertNotNull(state.roundId)
    }

    @Test
    fun testBetValidation() {
        val engine = CoinFlipEngine()
        val config = CoinFlipConfig()
        val state = engine.initialize(config)
        val validBet = CoinFlipBet(betId = "b1", userId = "u1", amount = 100.0, selection = "HEADS")
        assertTrue(engine.validateBet(state, validBet))
    }

    @Test
    fun testProcessRoundOutcome() {
        val engine = CoinFlipEngine()
        val config = CoinFlipConfig()
        val state = engine.initialize(config)
        val result = engine.processRound(state, emptyList())
        assertNotNull(result)
        assertTrue(result.outcome == "HEADS" || result.outcome == "TAILS")
        assertTrue(result.multiplier > 0)
    }
}
