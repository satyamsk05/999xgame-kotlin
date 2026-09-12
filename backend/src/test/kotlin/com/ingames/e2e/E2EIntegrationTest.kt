package com.ingames.e2e

import com.ingames.admin.AdminAuthService
import com.ingames.auth.LogginSessionStore
import com.ingames.models.*
import com.ingames.module
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.UUID

class E2EIntegrationTest {

    private val testJson = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Test
    fun `test end to end user lifecycle - auth deposit gameplay admin approval and withdrawal`() = testApplication {
        application {
            module()
        }

        val phone = "9988776655"

        // 1. Loggin OTP-less WhatsApp Auth Flow
        val createTokenRes = client.post("/api/auth/loggin/create-token")
        assertEquals(HttpStatusCode.OK, createTokenRes.status)
        val createData = testJson.decodeFromString<ApiResponse<com.ingames.auth.LogginTokenResponse>>(createTokenRes.bodyAsText()).data
        assertNotNull(createData)
        val logginToken = createData!!.token!!

        // Mark verified in Loggin session
        LogginSessionStore.markVerified(logginToken, phone)

        val verifyRes = client.post("/api/auth/loggin/verify") {
            contentType(ContentType.Application.Json)
            setBody("""{"token":"$logginToken"}""")
        }
        assertEquals(HttpStatusCode.OK, verifyRes.status)
        val verifyData = testJson.decodeFromString<ApiResponse<com.ingames.auth.LogginVerifyResponse>>(verifyRes.bodyAsText()).data
        assertNotNull(verifyData)
        val userToken = verifyData!!.token!!
        val userId = verifyData.user!!.id

        // 2. Initiate Deposit & Submit UTR
        val depRes = client.post("/api/deposits/initiate") {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, "Bearer $userToken")
            setBody(testJson.encodeToString(InitiateDepositRequest.serializer(), InitiateDepositRequest(500.0)))
        }
        assertEquals(HttpStatusCode.OK, depRes.status)
        val depData = testJson.decodeFromString<ApiResponse<InitiateDepositResponse>>(depRes.bodyAsText()).data!!
        val depositId = depData.depositId

        val utrRes = client.post("/api/deposits/submit-utr") {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, "Bearer $userToken")
            setBody(testJson.encodeToString(SubmitUtrRequest.serializer(), SubmitUtrRequest(depositId = depositId, utr = "UTR_E2E_998877")))
        }
        assertEquals(HttpStatusCode.OK, utrRes.status)

        // 3. Admin Login & Approve Deposit
        val adminLoginRes = AdminAuthService.login("admin", "admin123")
        assertNotNull(adminLoginRes)
        val adminToken = adminLoginRes!!.token

        val approveDepRes = client.post("/api/admin/deposits/action") {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, "Bearer $adminToken")
            setBody(testJson.encodeToString(AdminDepositActionRequest.serializer(), AdminDepositActionRequest(depositId = depositId, action = "APPROVE")))
        }
        assertEquals(HttpStatusCode.OK, approveDepRes.status)

        // 4. Verify Wallet Balance Credited
        val balRes = client.get("/api/wallet/balance") {
            header(HttpHeaders.Authorization, "Bearer $userToken")
        }
        assertEquals(HttpStatusCode.OK, balRes.status)
        val balance = testJson.decodeFromString<ApiResponse<WalletBalance>>(balRes.bodyAsText()).data!!
        assertTrue(balance.totalPaise > 0L)

        // 5. Place Bet on Seven Up Down
        val betRes = client.post("/api/games/seven-up-down/bet") {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, "Bearer $userToken")
            setBody(testJson.encodeToString(
                SevenUpDownBetRequest.serializer(),
                SevenUpDownBetRequest(
                    roundId = "rnd_e2e_1",
                    area = SevenUpDownBetArea.UP,
                    amountPaise = 1000L, // ₹10.00
                    idempotencyKey = "idemp_" + UUID.randomUUID().toString()
                )
            ))
        }
        assertEquals(HttpStatusCode.OK, betRes.status)

        // 6. Submit Withdrawal Request
        val withRes = client.post("/api/withdrawals/request") {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, "Bearer $userToken")
            setBody(testJson.encodeToString(
                WithdrawalRequestPayload.serializer(),
                WithdrawalRequestPayload(amountRupees = 100.0, payoutMethod = "UPI", upiId = "user@upi")
            ))
        }
        assertEquals(HttpStatusCode.OK, withRes.status)
        val withdrawalRecord = testJson.decodeFromString<ApiResponse<WithdrawalRecord>>(withRes.bodyAsText()).data!!

        // 7. Admin Approve Withdrawal
        val approveWithRes = client.post("/api/admin/withdrawals/action") {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, "Bearer $adminToken")
            setBody(testJson.encodeToString(
                AdminWithdrawalActionRequest.serializer(),
                AdminWithdrawalActionRequest(withdrawalId = withdrawalRecord.withdrawalId, action = "APPROVE")
            ))
        }
        assertEquals(HttpStatusCode.OK, approveWithRes.status)

        // 8. Verify Admin Audit Logs
        val auditRes = client.get("/api/admin/audit-logs") {
            header(HttpHeaders.Authorization, "Bearer $adminToken")
        }
        assertEquals(HttpStatusCode.OK, auditRes.status)
        val auditLogs = testJson.decodeFromString<ApiResponse<List<AdminAuditLogEntry>>>(auditRes.bodyAsText()).data!!
        assertTrue(auditLogs.isNotEmpty())
    }
}
