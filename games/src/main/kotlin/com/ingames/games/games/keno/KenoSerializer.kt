package com.ingames.games.games.keno

import kotlinx.serialization.json.Json

object KenoSerializer {
    private val json = Json { ignoreUnknownKeys = true }
    fun serializeResult(result: KenoResult): String = json.encodeToString(KenoResult.serializer(), result)
}
