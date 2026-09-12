package com.ingames.games.games.mines

import kotlinx.serialization.json.Json

object MinesSerializer {
    private val json = Json { ignoreUnknownKeys = true }
    fun serializeResult(result: MinesResult): String = json.encodeToString(MinesResult.serializer(), result)
}
