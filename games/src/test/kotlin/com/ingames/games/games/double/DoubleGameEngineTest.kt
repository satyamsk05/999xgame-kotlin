package com.ingames.games.games.double

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertNotNull

class DoubleGameEngineTest {
    @Test
    fun testEngineInitialization() {
        val engine = DoubleGameEngine()
        assertNotNull(engine)
    }
}
