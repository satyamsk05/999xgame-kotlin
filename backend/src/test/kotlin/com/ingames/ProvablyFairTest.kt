package com.ingames

import com.ingames.games.ProvablyFair
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ProvablyFairTest {

    @Test
    fun `test SHA-256 seed hashing is deterministic`() {
        val seed = "abc123def456"
        val hash1 = ProvablyFair.sha256(seed)
        val hash2 = ProvablyFair.sha256(seed)
        assertEquals(hash1, hash2)
        assertEquals(64, hash1.length)
    }

    @Test
    fun `test dice derivation produces valid 1 to 6 range and 2 to 12 total`() {
        for (i in 1..100L) {
            val seed = ProvablyFair.generateServerSeed()
            val (d1, d2) = ProvablyFair.deriveDice(seed, i)
            assertTrue(d1 in 1..6, "Die 1 was $d1")
            assertTrue(d2 in 1..6, "Die 2 was $d2")
            val total = d1 + d2
            assertTrue(total in 2..12, "Dice total was $total")
        }
    }

    @Test
    fun `test crash point derivation respects 1x minimum and multiplier scale`() {
        for (i in 1..100L) {
            val seed = ProvablyFair.generateServerSeed()
            val cp = ProvablyFair.deriveCrashPoint(seed, i)
            assertTrue(cp >= 1.00, "Crash point $cp was less than 1.00x")
        }
    }

    @Test
    fun `test dragon tiger card values are in 1 to 13 range`() {
        for (i in 1..100L) {
            val seed = ProvablyFair.generateServerSeed()
            val (dragon, tiger) = ProvablyFair.deriveDragonTiger(seed, i)
            assertTrue(dragon in 1..13, "Dragon was $dragon")
            assertTrue(tiger in 1..13, "Tiger was $tiger")
        }
    }
}
