package com.ingames.games.games.limbo

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertNotNull

class LimboEngineTest {
    @Test
    fun testEngineInitialization() {
        val engine = LimboEngine()
        assertNotNull(engine)
    }
}
