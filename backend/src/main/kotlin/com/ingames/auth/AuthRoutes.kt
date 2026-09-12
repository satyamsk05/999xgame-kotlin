package com.ingames.auth

import com.ingames.models.ApiResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.authRoutes() {
    route("/api/auth") {
        // --- LOGGIN.DEV OTP-LESS WHATSAPP AUTHENTICATION ---
        post("/loggin/create-token") {
            try {
                val res = AuthController.createLogginToken()
                call.respond(HttpStatusCode.OK, ApiResponse(status = "success", data = res))
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiResponse<Unit>(status = "error", message = e.message ?: "Failed to create Loggin token")
                )
            }
        }

        get("/loggin/status/{token}") {
            try {
                val token = call.parameters["token"]
                if (token.isNullOrBlank()) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse<Unit>(status = "error", message = "Token is required")
                    )
                    return@get
                }
                val res = AuthController.checkLogginStatus(token)
                call.respond(HttpStatusCode.OK, ApiResponse(status = "success", data = res))
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiResponse<Unit>(status = "error", message = e.message ?: "Failed to check status")
                )
            }
        }

        post("/loggin/verify") {
            try {
                val req = call.receive<LogginVerifyRequest>()
                if (req.token.isBlank()) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse<Unit>(status = "error", message = "Token is required")
                    )
                    return@post
                }
                val res = AuthController.verifyLogginSession(req.token)
                if (res.success) {
                    call.respond(HttpStatusCode.OK, ApiResponse(status = "success", data = res))
                } else {
                    val httpStatus = when (res.status) {
                        LogginSessionStatus.ACCOUNT_BLOCKED.name -> HttpStatusCode.Forbidden
                        LogginSessionStatus.EXPIRED.name -> HttpStatusCode.Gone
                        LogginSessionStatus.ALREADY_CONSUMED.name -> HttpStatusCode.Conflict
                        else -> HttpStatusCode.BadRequest
                    }
                    call.respond(httpStatus, ApiResponse(status = "error", message = res.message ?: "Verification failed", data = res))
                }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiResponse<Unit>(status = "error", message = e.message ?: "Failed to verify token")
                )
            }
        }

        // Webhook callback endpoint for Loggin.dev server-to-server notification
        post("/loggin/callback") {
            try {
                val params = call.receive<Map<String, String>>()
                val token = params["token"]
                val phone = params["phone"]
                if (!token.isNullOrEmpty() && !phone.isNullOrEmpty()) {
                    LogginSessionStore.markVerified(token, phone)
                    call.respond(HttpStatusCode.OK, ApiResponse<Unit>(status = "success", message = "Verified successfully"))
                } else {
                    call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(status = "error", message = "Missing token or phone"))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, ApiResponse<Unit>(status = "error", message = e.message))
            }
        }

        post("/logout") {
            call.respond(HttpStatusCode.OK, ApiResponse<Unit>(status = "success", message = "Logged out successfully"))
        }
    }
}
