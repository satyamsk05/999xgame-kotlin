package com.ingames.games.common

object ResultVerifier {
    fun verify(serverSeed: String, clientSeed: String, nonce: Long, expectedHash: String): Boolean {
        val actualHash = SeedProvider.hashSeed("$serverSeed:$clientSeed:$nonce")
        return actualHash.equals(expectedHash, ignoreCase = true)
    }
}
