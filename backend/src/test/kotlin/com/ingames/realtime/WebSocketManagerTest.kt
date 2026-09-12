package com.ingames.realtime

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class WebSocketManagerTest {

    @Test
    fun testWebSocketManagerMethods() {
        val manager = WebSocketManager
        assertNotNull(manager)
        assertEquals(0, manager.getOnlineUsersCount())
    }

    @Test
    fun testRealtimeMessageDataClass() {
        val msg = RealtimeMessage(event = "ROUND_TIMER", gameId = "coin_flip", data = "15")
        assertEquals("ROUND_TIMER", msg.event)
        assertEquals("coin_flip", msg.gameId)
        assertEquals("15", msg.data)
    }
}
