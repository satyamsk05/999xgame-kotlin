package com.ingames.admin

import com.ingames.database.MemoryDataStore

enum class RiskLevel {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

data class UserRiskProfile(
    val userId: String,
    val riskScore: Int, // 0 to 100
    val riskLevel: RiskLevel,
    val flaggedFactors: List<String>,
    val isBanned: Boolean = false
)

object RiskScoringService {

    private val userOverrides = mutableMapOf<String, Int>()
    private val bannedUsers = mutableSetOf<String>()

    fun getRiskProfile(userId: String): UserRiskProfile {
        if (bannedUsers.contains(userId)) {
            return UserRiskProfile(
                userId = userId,
                riskScore = 100,
                riskLevel = RiskLevel.CRITICAL,
                flaggedFactors = listOf("USER_BANNED"),
                isBanned = true
            )
        }

        userOverrides[userId]?.let { score ->
            val level = scoreToLevel(score)
            return UserRiskProfile(
                userId = userId,
                riskScore = score,
                riskLevel = level,
                flaggedFactors = listOf("ADMIN_OVERRIDE")
            )
        }

        val factors = mutableListOf<String>()
        var score = 10

        // Check deposit history
        val deposits = MemoryDataStore.deposits.values.filter { it["user_id"] == userId }
        if (deposits.size > 5) {
            score += 25
            factors.add("RAPID_DEPOSITS")
        }

        // Check withdrawals history
        val withdrawals = MemoryDataStore.withdrawals.values.filter { it["user_id"] == userId }
        if (withdrawals.size > 3) {
            score += 30
            factors.add("HIGH_WITHDRAWAL_FREQUENCY")
        }

        val finalScore = score.coerceIn(0, 100)
        val level = scoreToLevel(finalScore)

        return UserRiskProfile(
            userId = userId,
            riskScore = finalScore,
            riskLevel = level,
            flaggedFactors = factors
        )
    }

    fun setOverride(userId: String, score: Int, adminId: String, reason: String) {
        userOverrides[userId] = score.coerceIn(0, 100)
        AuditLogService.log(adminId, "SET_RISK_SCORE", userId, "Score set to $score. Reason: $reason")
    }

    fun setBanned(userId: String, banned: Boolean, adminId: String, reason: String) {
        if (banned) {
            bannedUsers.add(userId)
            AuditLogService.log(adminId, "BAN_USER", userId, "Reason: $reason")
        } else {
            bannedUsers.remove(userId)
            AuditLogService.log(adminId, "UNBAN_USER", userId, "Reason: $reason")
        }
    }

    private fun scoreToLevel(score: Int): RiskLevel {
        return when {
            score >= 80 -> RiskLevel.CRITICAL
            score >= 60 -> RiskLevel.HIGH
            score >= 30 -> RiskLevel.MEDIUM
            else -> RiskLevel.LOW
        }
    }
}
