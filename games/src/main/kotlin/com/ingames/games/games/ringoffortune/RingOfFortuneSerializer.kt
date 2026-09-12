package com.ingames.games.games.ringoffortune

import kotlinx.serialization.json.Json

object RingOfFortuneSerializer {
    private val json = Json { ignoreUnknownKeys = true }
    fun serializeResult(result: RingOfFortuneResult): String = json.encodeToString(RingOfFortuneResult.serializer(), result)
}
