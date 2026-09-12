package com.ingames.games.games.coinflip

import kotlinx.serialization.json.Json

object CoinFlipSerializer {
    private val json = Json { ignoreUnknownKeys = true }
    fun serializeResult(result: CoinFlipResult): String = json.encodeToString(CoinFlipResult.serializer(), result)
}
