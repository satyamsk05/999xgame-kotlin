package com.ingames.games.games.limbo

import kotlinx.serialization.json.Json

object LimboSerializer {
    private val json = Json { ignoreUnknownKeys = true }
    fun serializeResult(result: LimboResult): String = json.encodeToString(LimboResult.serializer(), result)
}
