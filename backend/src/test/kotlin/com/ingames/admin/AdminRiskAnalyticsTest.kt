package com.ingames.admin

import com.ingames.models.ApiResponse
import com.ingames.models.WithdrawalRecord
import com.ingames.models.WithdrawalRequestPayload
import com.ingames.module
import com.ingames.wallet.FinancialService
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class AdminRiskAnalyticsTest {

    private val testJson = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Test
    fun `test risk scoring and automated risk lock on withdrawal`() {
        val userId = "user_risk_test_1"
        
        // 1. Initially LOW risk
        val initialProfile = RiskScoringService.getRiskProfile(userId)
        assertEquals(RiskLevel.LOW, initialProfile.riskLevel)

        // 2. Ban user -> CRITICAL risk
        RiskScoringService.setBanned(userId, true, "admin_1", "Test ban")
        val bannedProfile = RiskScoringService.getRiskProfile(userId)
        assertEquals(RiskLevel.CRITICAL, bannedProfile.riskLevel)
        assertTrue(bannedProfile.isBanned)

        // 3. Request withdrawal for banned user -> status should be RISK_LOCKED
        val req = WithdrawalRequestPayload(amountRupees = 100.0, payoutMethod = "UPI", upiId = "user@upi")
        val result = FinancialService.requestWithdrawal(userId, req)
        assertTrue(result.isSuccess)
        assertEquals("RISK_LOCKED", result.getOrNull()?.status)

        // Unban for cleanup
        RiskScoringService.setBanned(userId, false, "admin_1", "Test unban")
    }

    @Test
    fun `test admin analytics summary endpoint`() = testApplication {
        application {
            module()
        }

        val adminLoginRes = AdminAuthService.login("admin", "admin123")
        assertNotNull(adminLoginRes)
        val adminToken = adminLoginRes!!.token

        val res = client.get("/api/admin/analytics/summary") {
            header(HttpHeaders.Authorization, "Bearer $adminToken")
        }
        assertEquals(HttpStatusCode.OK, res.status)
        val summary = testJson.decodeFromString<ApiResponse<PlatformAnalyticsSummary>>(res.bodyAsText()).data!!
        assertNotNull(summary)
        assertTrue(summary.activeUsersCount >= 0)
    }
}
