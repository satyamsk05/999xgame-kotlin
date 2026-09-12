package com.ingames

import com.ingames.auth.LogginSessionStore
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
    fun `test loggin OTP-less auth workflow endpoints`() = testApplication {
        application {
            module()
        }

        // 1. Create Token
        val createRes = client.post("/api/auth/loggin/create-token")
        assertEquals(HttpStatusCode.OK, createRes.status)
        val createBody = createRes.bodyAsText()
        assertTrue(createBody.contains("verificationUrl"))
        assertTrue(createBody.contains("token"))

        val createData = testJson.decodeFromString<ApiResponse<com.ingames.auth.LogginTokenResponse>>(createBody).data
        assertNotNull(createData)
        val token = createData!!.token!!
        assertNotNull(token)

        // 2. Status Check (PENDING)
        val statusRes1 = client.get("/api/auth/loggin/status/$token")
        assertEquals(HttpStatusCode.OK, statusRes1.status)
        assertTrue(statusRes1.bodyAsText().contains("PENDING"))

        // 3. Mark session verified (simulating WhatsApp verification webhook)
        LogginSessionStore.markVerified(token, "9876543210")

        // 4. Verify & Obtain Application JWT
        val verifyRes = client.post("/api/auth/loggin/verify") {
            contentType(ContentType.Application.Json)
            setBody("""{"token":"$token"}""")
        }
        assertEquals(HttpStatusCode.OK, verifyRes.status)
        val verifyBody = verifyRes.bodyAsText()
        assertTrue(verifyBody.contains("VERIFIED"))
        assertTrue(verifyBody.contains("9876543210"))
    }
}
