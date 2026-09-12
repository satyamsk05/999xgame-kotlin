package com.ingames.games.games.peryacolor

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertNotNull

class PeryaColorEngineTest {
    @Test
    fun testEngineInitialization() {
        val engine = PeryaColorEngine()
        assertNotNull(engine)
    }
}
