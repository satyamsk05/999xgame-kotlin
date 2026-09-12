package com.ingames.games.common

import java.security.SecureRandom

object RandomProvider {
    private val secureRandom = SecureRandom()

    fun nextInt(bound: Int): Int = secureRandom.nextInt(bound)
    fun nextDouble(): Double = secureRandom.nextDouble()
    fun nextBytes(bytes: ByteArray) = secureRandom.nextBytes(bytes)
}
