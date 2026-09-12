package com.ingames.games.common

import java.security.MessageDigest
import java.util.UUID

object SeedProvider {
    fun generateServerSeed(): String = UUID.randomUUID().toString().replace("-", "")

    fun hashSeed(seed: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(seed.toByteArray(Charsets.UTF_8))
        return hash.joinToString("") { "%02x".format(it) }
    }
}
