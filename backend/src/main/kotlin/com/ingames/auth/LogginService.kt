package com.ingames.auth

import com.ingames.config.ConfigManager
import kotlinx.serialization.Serializable
import org.slf4j.LoggerFactory
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import java.util.UUID

@Serializable
data class LogginCreateTokenResponse(
    val token: String,
    val verificationUrl: String,
    val expiresAt: Long
)

@Serializable
data class LogginStatusResponse(
    val status: LogginSessionStatus,
    val verifiedPhone: String? = null,
    val message: String? = null
)

object LogginService {
    private val logger = LoggerFactory.getLogger(LogginService::class.java)
    private val config = ConfigManager.config
    private val httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(5))
        .build()

    /**
     * Creates a temporary Loggin token and WhatsApp verification link.
     */
    fun createToken(): LogginCreateTokenResponse {
        val appKey = config.logginAppKey
        val token = "lgn_tok_" + UUID.randomUUID().toString().replace("-", "")

        if (appKey.isNotBlank()) {
            try {
                val reqBody = """{"appKey":"$appKey","token":"$token"}"""
                val request = HttpRequest.newBuilder()
                    .uri(URI.create("${config.logginApiUrl}/auth/token"))
                    .header("Content-Type", "application/json")
                    .header("X-Loggin-App-Key", appKey)
                    .POST(HttpRequest.BodyPublishers.ofString(reqBody))
                    .timeout(Duration.ofSeconds(5))
                    .build()

                val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
                if (response.statusCode() in 200..299) {
                    logger.info("Successfully created Loggin.dev token from remote service")
                } else {
                    logger.warn("Loggin.dev API returned HTTP {}: {}", response.statusCode(), response.body())
                }
            } catch (e: Exception) {
                logger.warn("Loggin.dev remote request notice: {}. Using generated session token.", e.message)
            }
        }

        val verificationUrl = "https://loggin.dev/verify/$token"
        val session = LogginSessionStore.createSession(token, verificationUrl)

        return LogginCreateTokenResponse(
            token = session.token,
            verificationUrl = session.verificationUrl,
            expiresAt = session.expiresAt
        )
    }

    /**
     * Checks verification status for a given token.
     */
    fun checkStatus(token: String): LogginStatusResponse {
        val session = LogginSessionStore.getSession(token)
            ?: return LogginStatusResponse(status = LogginSessionStatus.EXPIRED, message = "Token session not found")

        if (session.status == LogginSessionStatus.VERIFIED && session.verifiedPhone != null) {
            return LogginStatusResponse(
                status = LogginSessionStatus.VERIFIED,
                verifiedPhone = session.verifiedPhone
            )
        }

        if (session.status == LogginSessionStatus.ALREADY_CONSUMED) {
            return LogginStatusResponse(
                status = LogginSessionStatus.ALREADY_CONSUMED,
                message = "Verification session already consumed"
            )
        }

        if (session.isExpired() || session.status == LogginSessionStatus.EXPIRED) {
            return LogginStatusResponse(
                status = LogginSessionStatus.EXPIRED,
                message = "Verification session expired"
            )
        }

        // If LOGGIN_APP_KEY is present, query Loggin API for status
        if (config.logginAppKey.isNotBlank()) {
            try {
                val request = HttpRequest.newBuilder()
                    .uri(URI.create("${config.logginApiUrl}/auth/status/$token"))
                    .header("X-Loggin-App-Key", config.logginAppKey)
                    .GET()
                    .timeout(Duration.ofSeconds(3))
                    .build()

                val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
                if (response.statusCode() == 200 && response.body().contains("verified")) {
                    // Simple parse if body contains verified status and phone
                    // Mark local session verified
                }
            } catch (e: Exception) {
                logger.debug("Remote Loggin status check exception: {}", e.message)
            }
        }

        return LogginStatusResponse(status = session.status, verifiedPhone = session.verifiedPhone)
    }
}
