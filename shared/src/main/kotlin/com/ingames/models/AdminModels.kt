package com.ingames.models

import kotlinx.serialization.Serializable

@Serializable
enum class AdminRole {
    SUPER_ADMIN,
    FINANCE_ADMIN,
    GAME_ADMIN,
    SUPPORT_AGENT
}

@Serializable
data class AdminUser(
    val id: String,
    val username: String,
    val role: AdminRole = AdminRole.SUPER_ADMIN,
    val isActive: Boolean = true,
    val createdAt: String? = null
)

@Serializable
data class AdminLoginRequest(
    val username: String,
    val password: String
)

@Serializable
data class AdminLoginResponse(
    val token: String,
    val admin: AdminUser
)

@Serializable
data class AdminWithdrawalActionRequest(
    val withdrawalId: String,
    val action: String, // "APPROVE" or "REJECT"
    val note: String? = null
)

@Serializable
data class AdminDepositActionRequest(
    val depositId: String,
    val action: String, // "APPROVE" or "REJECT"
    val note: String? = null
)

@Serializable
data class AdminBlockUserRequest(
    val userId: String,
    val block: Boolean,
    val reason: String? = null
)

@Serializable
data class AdminUpdateGameConfigRequest(
    val gameId: String,
    val minStakePaise: Long? = null,
    val maxStakePaise: Long? = null,
    val isEnabled: Boolean? = null
)

@Serializable
data class AdminAuditLogEntry(
    val id: String,
    val adminId: String?,
    val action: String,
    val target: String?,
    val detailsJson: String? = null,
    val createdAt: String
)

@Serializable
data class AdminAnalyticsOverview(
    val totalUsers: Long = 0,
    val totalDepositsPaise: Long = 0,
    val totalWithdrawalsPaise: Long = 0,
    val totalBetsPaise: Long = 0,
    val netPlatformRevenuePaise: Long = 0
)
