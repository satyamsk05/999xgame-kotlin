package com.ingames.games.games.classicdice

import kotlinx.serialization.json.Json

object ClassicDiceSerializer {
    private val json = Json { ignoreUnknownKeys = true }
    fun serializeResult(result: ClassicDiceResult): String = json.encodeToString(ClassicDiceResult.serializer(), result)
}
