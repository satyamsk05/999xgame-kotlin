package com.ingames.auth

import com.ingames.database.MemoryDataStore
import java.util.UUID

data class RegisterRequest(val phone: String, val password: String, val name: String? = null)
data class LoginRequest(val phone: String, val password: String)
data class AuthResponse(val success: Boolean, val token: String? = null, val userId: String? = null, val message: String? = null)

object AuthController {
    fun register(req: RegisterRequest): AuthResponse {
        if (req.phone.isBlank() || req.password.isBlank()) {
            return AuthResponse(success = false, message = "Phone and password are required")
        }
        val existing = MemoryDataStore.users.values.find { it["phone"] == req.phone }
        if (existing != null) {
            return AuthResponse(success = false, message = "User with this phone already exists")
        }
        val userId = "usr_${UUID.randomUUID().toString().take(8)}"
        val hashedPw = PasswordHasher.hashPassword(req.password)
        MemoryDataStore.users[userId] = mutableMapOf(
            "id" to userId,
            "phone" to req.phone,
            "passwordHash" to hashedPw,
            "name" to (req.name ?: "Player"),
            "createdAt" to System.currentTimeMillis()
        )
        val token = JwtService.generateUserToken(userId, req.phone)
        return AuthResponse(success = true, token = token, userId = userId)
    }

    fun login(req: LoginRequest): AuthResponse {
        val userRecord = MemoryDataStore.users.values.find { it["phone"] == req.phone }
            ?: return AuthResponse(success = false, message = "Invalid phone or password")
        val hashedPw = userRecord["passwordHash"] as? String ?: ""
        if (!PasswordHasher.checkPassword(req.password, hashedPw)) {
            return AuthResponse(success = false, message = "Invalid phone or password")
        }
        val userId = userRecord["id"] as String
        val token = JwtService.generateUserToken(userId, req.phone)
        return AuthResponse(success = true, token = token, userId = userId)
    }
}
