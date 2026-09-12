package com.ingames.games.games.classicdice

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertNotNull

class ClassicDiceEngineTest {
    @Test
    fun testEngineInitialization() {
        val engine = ClassicDiceEngine()
        assertNotNull(engine)
    }
}
