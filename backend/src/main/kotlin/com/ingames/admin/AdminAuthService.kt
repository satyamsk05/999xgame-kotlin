package com.ingames.admin

import at.favre.lib.crypto.bcrypt.BCrypt
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import com.ingames.database.DatabaseFactory
import com.ingames.database.MemoryDataStore
import com.ingames.models.AdminLoginResponse
import com.ingames.models.AdminRole
import com.ingames.models.AdminUser
import java.util.Date
import java.util.UUID

object AdminAuthService {

    private const val SECRET = "999xgame-admin-secret-key-production-2026"
    private const val ISSUER = "ingames-admin-auth"
    private val algorithm = Algorithm.HMAC256(SECRET)

    private val demoAdminHash: String by lazy {
        BCrypt.withDefaults().hashToString(12, "admin123".toCharArray())
    }

    fun login(username: String, password: String): AdminLoginResponse? {
        val admin = getAdminByUsername(username) ?: return null
        val storedHash = getAdminPasswordHash(username) ?: return null

        val result = BCrypt.verifyer().verify(password.toCharArray(), storedHash)
        if (!result.verified) return null

        val token = generateAdminToken(admin)
        return AdminLoginResponse(token = token, admin = admin)
    }

    fun generateAdminToken(admin: AdminUser): String {
        return JWT.create()
            .withIssuer(ISSUER)
            .withSubject(admin.id)
            .withClaim("username", admin.username)
            .withClaim("role", admin.role.name)
            .withExpiresAt(Date(System.currentTimeMillis() + 86400000L * 7)) // 7 days
            .sign(algorithm)
    }

    fun verifyAdminToken(token: String): DecodedJWT? {
        return try {
            val verifier = JWT.require(algorithm)
                .withIssuer(ISSUER)
                .build()
            verifier.verify(token)
        } catch (e: Exception) {
            null
        }
    }

    private fun getAdminByUsername(username: String): AdminUser? {
        if (DatabaseFactory.isConnectedToPostgres) {
            return try {
                DatabaseFactory.withConnection { conn ->
                    val stmt = conn.prepareStatement("SELECT * FROM admins WHERE username = ? AND is_active = true")
                    stmt.setString(1, username)
                    val rs = stmt.executeQuery()
                    if (rs.next()) {
                        val roleStr = rs.getString("role") ?: "SUPER_ADMIN"
                        AdminUser(
                            id = rs.getString("id"),
                            username = rs.getString("username"),
                            role = try { AdminRole.valueOf(roleStr) } catch (e: Exception) { AdminRole.SUPER_ADMIN },
                            isActive = rs.getBoolean("is_active")
                        )
                    } else getMemoryAdmin(username)
                }
            } catch (e: Exception) {
                getMemoryAdmin(username)
            }
        } else {
            return getMemoryAdmin(username)
        }
    }

    private fun getAdminPasswordHash(username: String): String? {
        if (DatabaseFactory.isConnectedToPostgres) {
            return try {
                DatabaseFactory.withConnection { conn ->
                    val stmt = conn.prepareStatement("SELECT password_hash FROM admins WHERE username = ?")
                    stmt.setString(1, username)
                    val rs = stmt.executeQuery()
                    if (rs.next()) rs.getString("password_hash") else demoAdminHash
                }
            } catch (e: Exception) {
                demoAdminHash
            }
        } else {
            return demoAdminHash
        }
    }

    private fun getMemoryAdmin(username: String): AdminUser? {
        if (username == "admin" || username == "superadmin") {
            return AdminUser(
                id = "admin_super_01",
                username = username,
                role = AdminRole.SUPER_ADMIN,
                isActive = true
            )
        }
        return null
    }
}
