package com.ingames

import com.ingames.models.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ApiRoutesTest {

    private val testJson = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Test
    fun `test health check endpoint returns 200 ok`() = testApplication {
        application {
            module()
        }
        val response = client.get("/health")
        assertEquals(HttpStatusCode.OK, response.status)
        val text = response.bodyAsText()
        assertTrue(text.contains("\"status\": \"ok\"") || text.contains("\"status\":\"ok\""))
    }

    @Test
    fun `test public config and online ticker endpoints`() = testApplication {
        application {
            module()
        }
        val configRes = client.get("/api/config")
        assertEquals(HttpStatusCode.OK, configRes.status)

        val tickerRes = client.get("/api/online-ticker")
        assertEquals(HttpStatusCode.OK, tickerRes.status)
    }

    @Test
    fun `test auth otp verify returns JWT and user profile`() = testApplication {
        application {
            module()
        }
        val verifyRes = client.post("/api/auth/otp/verify") {
            contentType(ContentType.Application.Json)
            setBody(testJson.encodeToString(VerifyOtpRequest.serializer(), VerifyOtpRequest(phone = "9876543210", otp = "123456")))
        }
        assertEquals(HttpStatusCode.OK, verifyRes.status)
        val bodyText = verifyRes.bodyAsText()
        val auth = testJson.decodeFromString<ApiResponse<AuthResponse>>(bodyText)
        assertEquals("success", auth.status)
        assertNotNull(auth.data?.token)
        assertEquals("9876543210", auth.data?.user?.phone)
    }
}
