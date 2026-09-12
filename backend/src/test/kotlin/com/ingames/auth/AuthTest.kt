package com.ingames.auth

import com.ingames.database.DatabaseFactory
import com.ingames.users.UserRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AuthTest {

    @BeforeEach
    fun setUp() {
        LogginSessionStore.clearAll()
    }

    @Test
    fun testPasswordHasher() {
        val raw = "secret123"
        val hash = PasswordHasher.hashPassword(raw)
        assertTrue(PasswordHasher.checkPassword(raw, hash))
    }

    @Test
    fun testJwtGenerationAndVerification() {
        val userId = "usr_test_123"
        val phone = "9876543210"
        val token = JwtService.generateUserToken(userId, phone)
        assertNotNull(token)
        val decoded = JwtService.verifyUserToken(token)
        assertNotNull(decoded)
        assertEquals(userId, decoded?.subject)
    }

    @Test
    fun testDatabaseFactoryInit() {
        DatabaseFactory.init()
        assertTrue(true)
    }

    @Test
    fun `test loggin token creation creates pending session`() {
        val res = AuthController.createLogginToken()
        assertTrue(res.success)
        assertNotNull(res.token)
        assertNotNull(res.verificationUrl)
        assertTrue(res.verificationUrl!!.contains(res.token!!))

        val statusRes = AuthController.checkLogginStatus(res.token!!)
        assertEquals(LogginSessionStatus.PENDING.name, statusRes.status)
    }

    @Test
    fun `test loggin whatsapp verification and atomic session consumption`() {
        val tokenRes = AuthController.createLogginToken()
        val token = tokenRes.token!!
        val phone = "919876543210"

        // Simulate WhatsApp callback marking phone verified
        LogginSessionStore.markVerified(token, phone)

        val statusCheck = AuthController.checkLogginStatus(token)
        assertEquals(LogginSessionStatus.VERIFIED.name, statusCheck.status)
        assertEquals(phone, statusCheck.verifiedPhone)

        // First verification call should consume session and issue JWT
        val verifyRes1 = AuthController.verifyLogginSession(token)
        assertTrue(verifyRes1.success)
        assertEquals(LogginSessionStatus.VERIFIED.name, verifyRes1.status)
        assertNotNull(verifyRes1.token)
        assertNotNull(verifyRes1.user)
        assertEquals(phone, verifyRes1.user?.phone)

        // Second verification call on same token MUST return ALREADY_CONSUMED
        val verifyRes2 = AuthController.verifyLogginSession(token)
        assertFalse(verifyRes2.success)
        assertEquals(LogginSessionStatus.ALREADY_CONSUMED.name, verifyRes2.status)
    }

    @Test
    fun `test blocked user rejection during loggin verification`() {
        val tokenRes = AuthController.createLogginToken()
        val token = tokenRes.token!!
        val phone = "919999999999"

        // Create user and set isBlocked = true
        val user = UserRepository.getOrCreateUserByPhone(phone)
        com.ingames.admin.AdminUserService.setBlockStatus("admin", user.id, true)

        LogginSessionStore.markVerified(token, phone)
        val verifyRes = AuthController.verifyLogginSession(token)
        assertFalse(verifyRes.success)
        assertEquals(LogginSessionStatus.ACCOUNT_BLOCKED.name, verifyRes.status)
        assertNull(verifyRes.token)
    }
}
