package com.ingames.games.games.peryacolor

import kotlinx.serialization.json.Json

object PeryaColorSerializer {
    private val json = Json { ignoreUnknownKeys = true }
    fun serializeResult(result: PeryaColorResult): String = json.encodeToString(PeryaColorResult.serializer(), result)
}
