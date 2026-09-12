package com.ingames.auth

import com.ingames.models.UserProfile
import com.ingames.users.UserRepository
import kotlinx.serialization.Serializable

@Serializable
data class LogginTokenResponse(
    val success: Boolean,
    val token: String? = null,
    val verificationUrl: String? = null,
    val expiresAt: Long? = null,
    val message: String? = null
)

@Serializable
data class LogginStatusCheckResponse(
    val success: Boolean,
    val status: String,
    val verifiedPhone: String? = null,
    val message: String? = null
)

@Serializable
data class LogginVerifyRequest(
    val token: String = ""
)

@Serializable
data class LogginVerifyResponse(
    val success: Boolean,
    val status: String,
    val token: String? = null,
    val isNewUser: Boolean = false,
    val user: UserProfile? = null,
    val message: String? = null
)

object AuthController {

    fun createLogginToken(): LogginTokenResponse {
        val res = LogginService.createToken()
        return LogginTokenResponse(
            success = true,
            token = res.token,
            verificationUrl = res.verificationUrl,
            expiresAt = res.expiresAt
        )
    }

    fun checkLogginStatus(token: String): LogginStatusCheckResponse {
        val res = LogginService.checkStatus(token)
        return LogginStatusCheckResponse(
            success = res.status != LogginSessionStatus.EXPIRED,
            status = res.status.name,
            verifiedPhone = res.verifiedPhone,
            message = res.message
        )
    }

    fun verifyLogginSession(token: String): LogginVerifyResponse {
        val consumeResult = LogginSessionStore.atomicConsume(token)

        return when (consumeResult) {
            is ConsumeSessionResult.Success -> {
                processVerifiedUser(consumeResult.phone)
            }
            is ConsumeSessionResult.Pending -> {
                LogginVerifyResponse(
                    success = false,
                    status = LogginSessionStatus.PENDING.name,
                    message = "Verification is pending in WhatsApp"
                )
            }
            is ConsumeSessionResult.Expired -> {
                LogginVerifyResponse(
                    success = false,
                    status = LogginSessionStatus.EXPIRED.name,
                    message = "Verification token expired. Please try again."
                )
            }
            is ConsumeSessionResult.AlreadyConsumed -> {
                LogginVerifyResponse(
                    success = false,
                    status = LogginSessionStatus.ALREADY_CONSUMED.name,
                    message = "Verification session already consumed."
                )
            }
            is ConsumeSessionResult.NotFound -> {
                LogginVerifyResponse(
                    success = false,
                    status = LogginSessionStatus.EXPIRED.name,
                    message = "Invalid or expired session token."
                )
            }
        }
    }

    fun processVerifiedUser(phone: String): LogginVerifyResponse {
        val user = UserRepository.getOrCreateUserByPhone(phone)
        if (user.isBlocked) {
            return LogginVerifyResponse(
                success = false,
                status = LogginSessionStatus.ACCOUNT_BLOCKED.name,
                message = "Account is blocked. Please contact support."
            )
        }

        val appToken = JwtService.generateUserToken(user.id, user.phone)
        val isNewUser = user.name.startsWith("Player_") || user.name == "Player"

        return LogginVerifyResponse(
            success = true,
            status = LogginSessionStatus.VERIFIED.name,
            token = appToken,
            isNewUser = isNewUser,
            user = user
        )
    }
}
