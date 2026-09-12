package com.ingames.games.games.double

import kotlinx.serialization.json.Json

object DoubleGameSerializer {
    private val json = Json { ignoreUnknownKeys = true }
    fun serializeResult(result: DoubleGameResult): String = json.encodeToString(DoubleGameResult.serializer(), result)
}
