package com.ingames.app.data

import com.ingames.models.WsMessage
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

enum class ConnectionState {
    CONNECTED,
    RECONNECTING,
    DISCONNECTED
}

object RealtimeClient {

    private var gameSession: WebSocketSession? = null
    private var userSession: WebSocketSession? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState = _connectionState.asStateFlow()

    private val _messagesFlow = MutableSharedFlow<WsMessage>()
    val messagesFlow = _messagesFlow.asSharedFlow()

    fun connectGameWebSocket(gameId: String, token: String? = null) {
        scope.launch {
            var retryDelayMs = 1000L
            while (true) {
                try {
                    _connectionState.value = ConnectionState.RECONNECTING
                    val url = "${ApiClient.baseUrl.replace("http", "ws")}/ws/game/$gameId" +
                            if (!token.isNullOrBlank()) "?token=$token" else ""
                    val session = ApiClient.httpClient.webSocketSession(url)
                    gameSession = session
                    _connectionState.value = ConnectionState.CONNECTED
                    retryDelayMs = 1000L // Reset delay on successful connection

                    // Subscribe to room
                    val subMsg = Json.encodeToString(WsMessage(action = "subscribe", room = gameId))
                    session.send(Frame.Text(subMsg))

                    for (frame in session.incoming) {
                        if (frame is Frame.Text) {
                            try {
                                val msg = Json.decodeFromString<WsMessage>(frame.readText())
                                _messagesFlow.emit(msg)
                            } catch (e: Exception) {
                                // Non-json ping frame
                            }
                        }
                    }
                } catch (e: Exception) {
                    _connectionState.value = ConnectionState.RECONNECTING
                }
                
                // Connection lost or failed: Exponential backoff
                delay(retryDelayMs)
                retryDelayMs = (retryDelayMs * 2).coerceAtMost(10000L)
            }
        }
    }

    fun connectUserWebSocket(userId: String, token: String) {
        scope.launch {
            var retryDelayMs = 1000L
            while (true) {
                try {
                    val url = "${ApiClient.baseUrl.replace("http", "ws")}/ws/user/$userId?token=$token"
                    val session = ApiClient.httpClient.webSocketSession(url)
                    userSession = session

                    for (frame in session.incoming) {
                        if (frame is Frame.Text) {
                            try {
                                val msg = Json.decodeFromString<WsMessage>(frame.readText())
                                _messagesFlow.emit(msg)
                            } catch (e: Exception) {
                                // Non-json ping frame
                            }
                        }
                    }
                } catch (e: Exception) {
                    // Fallback
                }
                delay(retryDelayMs)
                retryDelayMs = (retryDelayMs * 2).coerceAtMost(10000L)
            }
        }
    }

    fun disconnect() {
        scope.launch {
            try {
                _connectionState.value = ConnectionState.DISCONNECTED
                gameSession?.close()
                userSession?.close()
            } catch (e: Exception) {
                // Closed
            }
        }
    }
}
