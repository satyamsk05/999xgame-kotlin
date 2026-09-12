package com.ingames.games.games.ringoffortune

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertNotNull

class RingOfFortuneEngineTest {
    @Test
    fun testEngineInitialization() {
        val engine = RingOfFortuneEngine()
        assertNotNull(engine)
    }
}
