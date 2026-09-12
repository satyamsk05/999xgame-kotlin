package com.ingames.games

import java.math.BigInteger
import java.security.MessageDigest
import java.security.SecureRandom

object ProvablyFair {
    private val random = SecureRandom()

    fun generateServerSeed(): String {
        val bytes = ByteArray(32)
        random.nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun sha256(input: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(input.toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { "%02x".format(it) }
    }

    fun deriveDice(serverSeed: String, roundNumber: Long): Pair<Int, Int> {
        val hash = sha256("$serverSeed:$roundNumber")
        val num = BigInteger(hash.take(16), 16)
        val d1 = ((num % BigInteger.valueOf(6)).toInt()) + 1
        val d2 = (((num / BigInteger.valueOf(6)) % BigInteger.valueOf(6)).toInt()) + 1
        return Pair(d1, d2)
    }

    fun deriveCrashPoint(serverSeed: String, roundNumber: Long): Double {
        val hash = sha256("$serverSeed:$roundNumber")
        val num = BigInteger(hash.take(13), 16)
        val max13 = BigInteger("10000000000000", 16)
        val fraction = num.toDouble() / max13.toDouble()
        // 1% house edge
        val result = (0.99 / (1.0 - fraction * 0.99)).coerceAtLeast(1.0)
        return kotlin.math.floor(result * 100.0) / 100.0
    }

    fun deriveDragonTiger(serverSeed: String, roundNumber: Long): Pair<Int, Int> {
        val hash = sha256("$serverSeed:$roundNumber")
        val num = BigInteger(hash.take(16), 16)
        val dragonVal = ((num % BigInteger.valueOf(13)).toInt()) + 1
        val tigerVal = (((num / BigInteger.valueOf(13)) % BigInteger.valueOf(13)).toInt()) + 1
        return Pair(dragonVal, tigerVal)
    }
}
