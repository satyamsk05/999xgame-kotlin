package com.ingames.auth

import at.favre.lib.crypto.bcrypt.BCrypt

object PasswordHasher {
    fun hashPassword(password: String): String {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray())
    }

    fun checkPassword(password: String, hashed: String): Boolean {
        return try {
            BCrypt.verifyer().verify(password.toCharArray(), hashed).verified
        } catch (e: Exception) {
            false
        }
    }
}
