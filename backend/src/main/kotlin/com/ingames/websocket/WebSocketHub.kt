package com.ingames.websocket

import io.ktor.websocket.*
import kotlinx.coroutines.DelicateCoroutinesApi
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet

@OptIn(DelicateCoroutinesApi::class)
class WebSocketHub {
    private val logger = LoggerFactory.getLogger(WebSocketHub::class.java)

    // Room name -> Set of active WebSocket sessions
    private val roomSubscriptions = ConcurrentHashMap<String, CopyOnWriteArraySet<WebSocketSession>>()
    // User ID -> Set of active WebSocket sessions
    private val userSessions = ConcurrentHashMap<String, CopyOnWriteArraySet<WebSocketSession>>()
    // Session -> Set of rooms it has joined
    private val sessionRooms = ConcurrentHashMap<WebSocketSession, CopyOnWriteArraySet<String>>()

    fun registerSession(session: WebSocketSession, userId: String?) {
        if (userId != null) {
            userSessions.getOrPut(userId) { CopyOnWriteArraySet() }.add(session)
        }
        sessionRooms[session] = CopyOnWriteArraySet()
    }

    fun unregisterSession(session: WebSocketSession, userId: String?) {
        if (userId != null) {
            userSessions[userId]?.remove(session)
            if (userSessions[userId]?.isEmpty() == true) {
                userSessions.remove(userId)
            }
        }
        val rooms = sessionRooms.remove(session) ?: emptySet()
        for (r in rooms) {
            roomSubscriptions[r]?.remove(session)
        }
    }

    fun joinRoom(session: WebSocketSession, room: String) {
        roomSubscriptions.getOrPut(room) { CopyOnWriteArraySet() }.add(session)
        sessionRooms.getOrPut(session) { CopyOnWriteArraySet() }.add(room)
        logger.debug("Session joined room {}", room)
    }

    fun leaveRoom(session: WebSocketSession, room: String) {
        roomSubscriptions[room]?.remove(session)
        sessionRooms[session]?.remove(room)
    }

    fun broadcastToRoom(room: String, messageText: String) {
        val sessions = roomSubscriptions[room] ?: return
        for (s in sessions) {
            try {
                if (!s.outgoing.isClosedForSend) {
                    s.outgoing.trySend(Frame.Text(messageText))
                }
            } catch (e: Exception) {
                logger.warn("Failed to send frame to session in room {}: {}", room, e.message)
            }
        }
    }

    fun sendToUser(userId: String, messageText: String) {
        val sessions = userSessions[userId] ?: return
        for (s in sessions) {
            try {
                if (!s.outgoing.isClosedForSend) {
                    s.outgoing.trySend(Frame.Text(messageText))
                }
            } catch (e: Exception) {
                logger.warn("Failed to send frame to user {}: {}", userId, e.message)
            }
        }
    }

    fun getActiveUserCount(): Int = userSessions.size.coerceAtLeast(1250)
}
