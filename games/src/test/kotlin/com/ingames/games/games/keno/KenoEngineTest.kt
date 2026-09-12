package com.ingames.games.games.keno

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertNotNull

class KenoEngineTest {
    @Test
    fun testEngineInitialization() {
        val engine = KenoEngine()
        assertNotNull(engine)
    }
}
