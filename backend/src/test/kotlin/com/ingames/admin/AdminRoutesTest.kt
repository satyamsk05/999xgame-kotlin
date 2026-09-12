package com.ingames

import com.ingames.admin.AdminAuthService
import com.ingames.models.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class AdminRoutesTest {

    private val testJson = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Test
    fun `test admin login via REST endpoint`() = testApplication {
        application {
            module()
        }

        val res = client.post("/api/admin/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(testJson.encodeToString(AdminLoginRequest.serializer(), AdminLoginRequest("admin", "admin123")))
        }

        assertEquals(HttpStatusCode.OK, res.status)
        val body = testJson.decodeFromString<ApiResponse<AdminLoginResponse>>(res.bodyAsText())
        assertEquals("success", body.status)
        assertNotNull(body.data?.token)
        assertEquals("admin", body.data?.admin?.username)
    }

    @Test
    fun `test protected admin endpoint rejects request without token`() = testApplication {
        application {
            module()
        }

        val res = client.get("/api/admin/withdrawals")
        assertEquals(HttpStatusCode.Unauthorized, res.status)
    }

    @Test
    fun `test admin analytics overview with valid bearer token`() = testApplication {
        application {
            module()
        }

        val loginRes = client.post("/api/admin/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(testJson.encodeToString(AdminLoginRequest.serializer(), AdminLoginRequest("admin", "admin123")))
        }
        val loginBody = testJson.decodeFromString<ApiResponse<AdminLoginResponse>>(loginRes.bodyAsText())
        val token = loginBody.data?.token ?: fail("Token null")

        val res = client.get("/api/admin/analytics/overview") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        assertEquals(HttpStatusCode.OK, res.status)
        val overviewBody = testJson.decodeFromString<ApiResponse<AdminAnalyticsOverview>>(res.bodyAsText())
        assertEquals("success", overviewBody.status)
        assertTrue((overviewBody.data?.totalUsers ?: 0) > 0)
    }
}
