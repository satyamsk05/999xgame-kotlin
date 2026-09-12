package com.ingames

import com.ingames.admin.AdminAuthService

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class AdminAuthServiceTest {

    @Test
    fun `test admin login with valid demo credentials returns JWT`() {
        val res = AdminAuthService.login("admin", "admin123")
        assertNotNull(res)
        assertEquals("admin", res?.admin?.username)
        assertTrue((res?.token ?: "").isNotBlank())
    }

    @Test
    fun `test admin login with invalid password returns null`() {
        val res = AdminAuthService.login("admin", "wrongpassword")
        assertNull(res)
    }

    @Test
    fun `test admin token verification decodes valid claims`() {
        val loginRes = AdminAuthService.login("admin", "admin123")
        assertNotNull(loginRes)
        val decoded = AdminAuthService.verifyAdminToken(loginRes!!.token)
        assertNotNull(decoded)
        assertEquals("admin", decoded?.getClaim("username")?.asString())
    }
}
