package com.ingames.realtime

import io.ktor.server.routing.Route
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText

fun Route.realtimeRoutes() {
    webSocket("/ws/game/{gameId}") {
        val gameId = call.parameters["gameId"] ?: "default"
        WebSocketManager.joinGameChannel(gameId, this)
        try {
            for (frame in incoming) {
                if (frame is Frame.Text) {
                    val text = frame.readText()
                    // Echo or handle client ping
                    if (text.contains("ping", ignoreCase = true)) {
                        send(Frame.Text("{\"event\":\"pong\"}"))
                    }
                }
            }
        } finally {
            WebSocketManager.leaveGameChannel(gameId, this)
        }
    }

    webSocket("/ws/user/{userId}") {
        val userId = call.parameters["userId"] ?: "anon"
        WebSocketManager.registerUserSession(userId, this)
        try {
            for (frame in incoming) {
                if (frame is Frame.Text) {
                    val text = frame.readText()
                    if (text.contains("ping", ignoreCase = true)) {
                        send(Frame.Text("{\"event\":\"pong\"}"))
                    }
                }
            }
        } finally {
            WebSocketManager.unregisterUserSession(userId)
        }
    }
}
