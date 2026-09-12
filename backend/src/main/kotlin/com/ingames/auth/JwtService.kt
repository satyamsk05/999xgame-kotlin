package com.ingames.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import com.ingames.config.ConfigManager
import java.util.Date

object JwtService {
    private val config = ConfigManager.config
    private val userAlgorithm = Algorithm.HMAC256(config.jwtSecret)
    private val adminAlgorithm = Algorithm.HMAC256(config.adminJwtSecret)

    val userVerifier = JWT.require(userAlgorithm)
        .withIssuer(config.jwtIssuer)
        .withAudience(config.jwtAudience)
        .build()

    val adminVerifier = JWT.require(adminAlgorithm)
        .withIssuer(config.jwtIssuer)
        .withAudience(config.jwtAudience)
        .build()

    fun generateUserToken(userId: String, phone: String): String {
        return JWT.create()
            .withIssuer(config.jwtIssuer)
            .withAudience(config.jwtAudience)
            .withSubject(userId)
            .withClaim("phone", phone)
            .withClaim("type", "USER")
            .withExpiresAt(Date(System.currentTimeMillis() + config.jwtExpirationMs))
            .sign(userAlgorithm)
    }

    fun generateAdminToken(adminId: String, username: String, role: String): String {
        return JWT.create()
            .withIssuer(config.jwtIssuer)
            .withAudience(config.jwtAudience)
            .withSubject(adminId)
            .withClaim("username", username)
            .withClaim("role", role)
            .withClaim("type", "ADMIN")
            .withExpiresAt(Date(System.currentTimeMillis() + config.jwtExpirationMs))
            .sign(adminAlgorithm)
    }

    fun verifyUserToken(token: String): DecodedJWT? {
        return try {
            userVerifier.verify(token)
        } catch (e: Exception) {
            null
        }
    }

    fun verifyAdminToken(token: String): DecodedJWT? {
        return try {
            adminVerifier.verify(token)
        } catch (e: Exception) {
            null
        }
    }
}
