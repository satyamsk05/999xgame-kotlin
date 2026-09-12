package com.ingames.games.games.mines

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertNotNull

class MinesEngineTest {
    @Test
    fun testEngineInitialization() {
        val engine = MinesEngine()
        assertNotNull(engine)
    }
}
