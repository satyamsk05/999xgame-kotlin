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
        var finalToken = "lgn_tok_" + UUID.randomUUID().toString().replace("-", "")
        var finalVerificationUrl = "https://loggin.dev/verify/$finalToken"

        if (appKey.isNotBlank()) {
            val reqBody = """{"appKey":"$appKey","token":"$finalToken"}"""
            val request = HttpRequest.newBuilder()
                .uri(URI.create("${config.logginApiUrl}/auth/token"))
                .header("Content-Type", "application/json")
                .header("X-Loggin-App-Key", appKey)
                .POST(HttpRequest.BodyPublishers.ofString(reqBody))
                .timeout(Duration.ofSeconds(5))
                .build()

            try {
                val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
                if (response.statusCode() in 200..299) {
                    logger.info("Successfully created Loggin.dev token from remote service")
                    val body = response.body()
                    val tokenMatch = "\"token\"\\s*:\\s*\"([^\"]+)\"".toRegex().find(body)
                    if (tokenMatch != null) {
                        finalToken = tokenMatch.groupValues[1]
                    }
                    val urlMatch = "\"verificationUrl\"\\s*:\\s*\"([^\"]+)\"".toRegex().find(body)
                        ?: "\"link\"\\s*:\\s*\"([^\"]+)\"".toRegex().find(body)
                    if (urlMatch != null) {
                        finalVerificationUrl = urlMatch.groupValues[1]
                    } else {
                        finalVerificationUrl = "https://loggin.dev/verify/$finalToken"
                    }
                } else {
                    logger.warn("Loggin.dev API returned HTTP {}: {}", response.statusCode(), response.body())
                    throw IllegalStateException("Loggin provider API error: HTTP ${response.statusCode()}")
                }
            } catch (e: Exception) {
                if (e is IllegalStateException) throw e
                logger.warn("Loggin.dev remote request exception: {}", e.message)
                throw IllegalStateException("Failed to communicate with Loggin provider: ${e.message}")
            }
        }

        val session = LogginSessionStore.createSession(finalToken, finalVerificationUrl)

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
                if (response.statusCode() == 200) {
                    val body = response.body()
                    val isVerified = body.contains("\"status\":\"verified\"", ignoreCase = true) ||
                            body.contains("\"status\":\"VERIFIED\"", ignoreCase = true) ||
                            body.contains("\"verified\":true", ignoreCase = true)
                    val phoneMatch = "\"phone\"\\s*:\\s*\"([^\"]+)\"".toRegex().find(body)
                        ?: "\"verifiedPhone\"\\s*:\\s*\"([^\"]+)\"".toRegex().find(body)

                    if (isVerified && phoneMatch != null) {
                        val phone = phoneMatch.groupValues[1]
                        LogginSessionStore.markVerified(token, phone)
                        return LogginStatusResponse(
                            status = LogginSessionStatus.VERIFIED,
                            verifiedPhone = phone
                        )
                    }
                }
            } catch (e: Exception) {
                logger.debug("Remote Loggin status check exception: {}", e.message)
            }
        }

        return LogginStatusResponse(status = session.status, verifiedPhone = session.verifiedPhone)
    }
}
