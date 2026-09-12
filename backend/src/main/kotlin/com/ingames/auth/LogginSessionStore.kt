package com.ingames.auth

import java.util.concurrent.ConcurrentHashMap

enum class LogginSessionStatus {
    PENDING,
    VERIFIED,
    EXPIRED,
    ALREADY_CONSUMED,
    ACCOUNT_BLOCKED
}

data class LogginSession(
    val token: String,
    val verificationUrl: String,
    var status: LogginSessionStatus = LogginSessionStatus.PENDING,
    var verifiedPhone: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val expiresAt: Long = System.currentTimeMillis() + 300_000L // 5 minutes lifetime
) {
    fun isExpired(): Boolean = System.currentTimeMillis() > expiresAt
}

sealed class ConsumeSessionResult {
    data class Success(val phone: String) : ConsumeSessionResult()
    object Pending : ConsumeSessionResult()
    object Expired : ConsumeSessionResult()
    object AlreadyConsumed : ConsumeSessionResult()
    object NotFound : ConsumeSessionResult()
}

object LogginSessionStore {
    private val sessions = ConcurrentHashMap<String, LogginSession>()

    fun createSession(token: String, verificationUrl: String): LogginSession {
        val session = LogginSession(token = token, verificationUrl = verificationUrl)
        sessions[token] = session
        return session
    }

    fun getSession(token: String): LogginSession? {
        val session = sessions[token] ?: return null
        if (session.status == LogginSessionStatus.PENDING && session.isExpired()) {
            session.status = LogginSessionStatus.EXPIRED
        }
        return session
    }

    fun markVerified(token: String, phone: String) {
        val session = sessions[token] ?: return
        if (session.status == LogginSessionStatus.PENDING && !session.isExpired()) {
            session.status = LogginSessionStatus.VERIFIED
            session.verifiedPhone = phone
        }
    }

    /**
     * Atomically consumes a verified session ensuring single-time usage.
     */
    @Synchronized
    fun atomicConsume(token: String): ConsumeSessionResult {
        val session = sessions[token] ?: return ConsumeSessionResult.NotFound

        if (session.isExpired()) {
            session.status = LogginSessionStatus.EXPIRED
            return ConsumeSessionResult.Expired
        }

        return when (session.status) {
            LogginSessionStatus.VERIFIED -> {
                val phone = session.verifiedPhone
                if (phone.isNullOrBlank()) {
                    ConsumeSessionResult.Pending
                } else {
                    session.status = LogginSessionStatus.ALREADY_CONSUMED
                    ConsumeSessionResult.Success(phone)
                }
            }
            LogginSessionStatus.ALREADY_CONSUMED -> ConsumeSessionResult.AlreadyConsumed
            LogginSessionStatus.EXPIRED -> ConsumeSessionResult.Expired
            LogginSessionStatus.PENDING -> ConsumeSessionResult.Pending
            LogginSessionStatus.ACCOUNT_BLOCKED -> ConsumeSessionResult.AlreadyConsumed
        }
    }

    fun clearAll() {
        sessions.clear()
    }
}
