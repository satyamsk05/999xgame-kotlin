package com.ingames.auth

import com.ingames.database.DatabaseFactory
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class AuthTest {

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
    }

    @Test
    fun testDatabaseFactoryInit() {
        DatabaseFactory.init()
        // DatabaseFactory initializes cleanly without throwing exceptions
        assertTrue(true)
    }
}
