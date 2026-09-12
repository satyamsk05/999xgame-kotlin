package com.ingames.realtime

import io.ktor.websocket.DefaultWebSocketSession
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap

@Serializable
data class RealtimeMessage(
    val event: String,
    val gameId: String? = null,
    val data: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

object WebSocketManager {
    private val scope = CoroutineScope(Dispatchers.Default)
    private val json = Json { ignoreUnknownKeys = true }

    // gameId -> set of sessions
    private val gameChannels = ConcurrentHashMap<String, MutableSet<DefaultWebSocketSession>>()
    
    // userId -> session
    private val userSessions = ConcurrentHashMap<String, DefaultWebSocketSession>()

    fun joinGameChannel(gameId: String, session: DefaultWebSocketSession) {
        gameChannels.getOrPut(gameId) { ConcurrentHashMap.newKeySet() }.add(session)
    }

    fun leaveGameChannel(gameId: String, session: DefaultWebSocketSession) {
        gameChannels[gameId]?.remove(session)
    }

    fun registerUserSession(userId: String, session: DefaultWebSocketSession) {
        userSessions[userId] = session
    }

    fun unregisterUserSession(userId: String) {
        userSessions.remove(userId)
    }

    fun broadcastToGame(gameId: String, event: String, data: String) {
        val sessions = gameChannels[gameId] ?: return
        val msg = json.encodeToString(RealtimeMessage(event = event, gameId = gameId, data = data))
        scope.launch {
            sessions.forEach { session ->
                try {
                    session.send(Frame.Text(msg))
                } catch (e: Exception) {
                    // Session stale/disconnected
                }
            }
        }
    }

    fun sendToUser(userId: String, event: String, data: String) {
        val session = userSessions[userId] ?: return
        val msg = json.encodeToString(RealtimeMessage(event = event, data = data))
        scope.launch {
            try {
                session.send(Frame.Text(msg))
            } catch (e: Exception) {
                userSessions.remove(userId)
            }
        }
    }

    fun getOnlineUsersCount(): Int = userSessions.size
}
