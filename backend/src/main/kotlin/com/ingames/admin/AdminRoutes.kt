package com.ingames.admin

import com.ingames.models.*
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.adminRoutes() {
    route("/api/admin") {
        // --- PUBLIC ADMIN LOGIN ---
        post("/auth/login") {
            try {
                val req = call.receive<AdminLoginRequest>()
                val res = AdminAuthService.login(req.username, req.password)
                if (res != null) {
                    call.respond(HttpStatusCode.OK, ApiResponse(data = res))
                } else {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        ApiResponse<Unit>(status = "error", message = "Invalid admin credentials")
                    )
                }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse<Unit>(status = "error", message = e.message ?: "Invalid request")
                )
            }
        }

        // Financial Withdrawals
        get("/withdrawals") {
            val authHeader = call.request.headers["Authorization"]
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                call.respond(HttpStatusCode.Unauthorized, ApiResponse<Unit>(status = "error", message = "Admin token required"))
                return@get
            }
            val token = authHeader.removePrefix("Bearer ").trim()
            val decoded = AdminAuthService.verifyAdminToken(token)
            if (decoded == null) {
                call.respond(HttpStatusCode.Unauthorized, ApiResponse<Unit>(status = "error", message = "Invalid or expired admin token"))
                return@get
            }

            val pending = AdminWithdrawalService.getPendingWithdrawals()
            call.respond(ApiResponse(data = pending))
        }

        post("/withdrawals/action") {
            val authHeader = call.request.headers["Authorization"]
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                call.respond(HttpStatusCode.Unauthorized, ApiResponse<Unit>(status = "error", message = "Admin token required"))
                return@post
            }
            val token = authHeader.removePrefix("Bearer ").trim()
            val decoded = AdminAuthService.verifyAdminToken(token)
            if (decoded == null) {
                call.respond(HttpStatusCode.Unauthorized, ApiResponse<Unit>(status = "error", message = "Invalid or expired admin token"))
                return@post
            }
            val adminId = decoded.subject ?: "admin_system"

            val req = call.receive<AdminWithdrawalActionRequest>()
            val success = if (req.action.equals("APPROVE", ignoreCase = true)) {
                AdminWithdrawalService.approveWithdrawal(adminId, req.withdrawalId, req.note)
            } else {
                AdminWithdrawalService.rejectWithdrawal(adminId, req.withdrawalId, req.note)
            }
            if (success) {
                call.respond(ApiResponse(data = mapOf("status" to req.action.uppercase())))
            } else {
                call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(status = "error", message = "Failed to process withdrawal"))
            }
        }

        // Financial Deposits
        get("/deposits") {
            val authHeader = call.request.headers["Authorization"]
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                call.respond(HttpStatusCode.Unauthorized, ApiResponse<Unit>(status = "error", message = "Admin token required"))
                return@get
            }
            val token = authHeader.removePrefix("Bearer ").trim()
            val decoded = AdminAuthService.verifyAdminToken(token)
            if (decoded == null) {
                call.respond(HttpStatusCode.Unauthorized, ApiResponse<Unit>(status = "error", message = "Invalid or expired admin token"))
                return@get
            }

            val pending = AdminDepositService.getPendingDeposits()
            call.respond(ApiResponse(data = pending))
        }

        post("/deposits/action") {
            val authHeader = call.request.headers["Authorization"]
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                call.respond(HttpStatusCode.Unauthorized, ApiResponse<Unit>(status = "error", message = "Admin token required"))
                return@post
            }
            val token = authHeader.removePrefix("Bearer ").trim()
            val decoded = AdminAuthService.verifyAdminToken(token)
            if (decoded == null) {
                call.respond(HttpStatusCode.Unauthorized, ApiResponse<Unit>(status = "error", message = "Invalid or expired admin token"))
                return@post
            }
            val adminId = decoded.subject ?: "admin_system"

            val req = call.receive<AdminDepositActionRequest>()
            val success = if (req.action.equals("APPROVE", ignoreCase = true)) {
                AdminDepositService.approveDeposit(adminId, req.depositId, req.note)
            } else {
                AdminDepositService.rejectDeposit(adminId, req.depositId, req.note)
            }
            if (success) {
                call.respond(ApiResponse(data = mapOf("status" to req.action.uppercase())))
            } else {
                call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(status = "error", message = "Failed to process deposit"))
            }
        }

        // User Management
        get("/users") {
            val authHeader = call.request.headers["Authorization"]
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                call.respond(HttpStatusCode.Unauthorized, ApiResponse<Unit>(status = "error", message = "Admin token required"))
                return@get
            }
            val token = authHeader.removePrefix("Bearer ").trim()
            val decoded = AdminAuthService.verifyAdminToken(token)
            if (decoded == null) {
                call.respond(HttpStatusCode.Unauthorized, ApiResponse<Unit>(status = "error", message = "Invalid or expired admin token"))
                return@get
            }

            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 50
            val offset = call.request.queryParameters["offset"]?.toIntOrNull() ?: 0
            val users = AdminUserService.getUsers(limit, offset)
            call.respond(ApiResponse(data = users))
        }

        post("/users/block") {
            val authHeader = call.request.headers["Authorization"]
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                call.respond(HttpStatusCode.Unauthorized, ApiResponse<Unit>(status = "error", message = "Admin token required"))
                return@post
            }
            val token = authHeader.removePrefix("Bearer ").trim()
            val decoded = AdminAuthService.verifyAdminToken(token)
            if (decoded == null) {
                call.respond(HttpStatusCode.Unauthorized, ApiResponse<Unit>(status = "error", message = "Invalid or expired admin token"))
                return@post
            }
            val adminId = decoded.subject ?: "admin_system"

            val req = call.receive<AdminBlockUserRequest>()
            val ok = AdminUserService.setBlockStatus(adminId, req.userId, req.block, req.reason)
            if (ok) {
                call.respond(ApiResponse(data = mapOf("isBlocked" to req.block)))
            } else {
                call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(status = "error", message = "User not found"))
            }
        }

        // Game Configurations
        post("/games/config") {
            val authHeader = call.request.headers["Authorization"]
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                call.respond(HttpStatusCode.Unauthorized, ApiResponse<Unit>(status = "error", message = "Admin token required"))
                return@post
            }
            val token = authHeader.removePrefix("Bearer ").trim()
            val decoded = AdminAuthService.verifyAdminToken(token)
            if (decoded == null) {
                call.respond(HttpStatusCode.Unauthorized, ApiResponse<Unit>(status = "error", message = "Invalid or expired admin token"))
                return@post
            }
            val adminId = decoded.subject ?: "admin_system"

            val req = call.receive<AdminUpdateGameConfigRequest>()
            val ok = AdminGameConfigService.updateGameConfig(
                adminId = adminId,
                gameId = req.gameId,
                minStakePaise = req.minStakePaise,
                maxStakePaise = req.maxStakePaise,
                isEnabled = req.isEnabled
            )
            if (ok) {
                call.respond(ApiResponse(data = mapOf("gameId" to req.gameId, "updated" to true)))
            } else {
                call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(status = "error", message = "Game not found"))
            }
        }

        // Audit Logs & Analytics
        get("/audit-logs") {
            val authHeader = call.request.headers["Authorization"]
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                call.respond(HttpStatusCode.Unauthorized, ApiResponse<Unit>(status = "error", message = "Admin token required"))
                return@get
            }
            val token = authHeader.removePrefix("Bearer ").trim()
            val decoded = AdminAuthService.verifyAdminToken(token)
            if (decoded == null) {
                call.respond(HttpStatusCode.Unauthorized, ApiResponse<Unit>(status = "error", message = "Invalid or expired admin token"))
                return@get
            }

            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 50
            val logs = AuditLogService.getLogs(limit)
            call.respond(ApiResponse(data = logs))
        }

        get("/analytics/overview") {
            val authHeader = call.request.headers["Authorization"]
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                call.respond(HttpStatusCode.Unauthorized, ApiResponse<Unit>(status = "error", message = "Admin token required"))
                return@get
            }
            val token = authHeader.removePrefix("Bearer ").trim()
            val decoded = AdminAuthService.verifyAdminToken(token)
            if (decoded == null) {
                call.respond(HttpStatusCode.Unauthorized, ApiResponse<Unit>(status = "error", message = "Invalid or expired admin token"))
                return@get
            }

            val analytics = AdminUserService.getAnalyticsOverview()
            call.respond(ApiResponse(data = analytics))
        }

        get("/analytics/summary") {
            val authHeader = call.request.headers["Authorization"]
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                call.respond(HttpStatusCode.Unauthorized, ApiResponse<Unit>(status = "error", message = "Admin token required"))
                return@get
            }
            val token = authHeader.removePrefix("Bearer ").trim()
            val decoded = AdminAuthService.verifyAdminToken(token)
            if (decoded == null) {
                call.respond(HttpStatusCode.Unauthorized, ApiResponse<Unit>(status = "error", message = "Invalid or expired admin token"))
                return@get
            }

            val summary = AdminAnalyticsService.getSummary()
            call.respond(ApiResponse(data = summary))
        }

        post("/users/risk-action") {
            val authHeader = call.request.headers["Authorization"]
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                call.respond(HttpStatusCode.Unauthorized, ApiResponse<Unit>(status = "error", message = "Admin token required"))
                return@post
            }
            val token = authHeader.removePrefix("Bearer ").trim()
            val decoded = AdminAuthService.verifyAdminToken(token)
            if (decoded == null) {
                call.respond(HttpStatusCode.Unauthorized, ApiResponse<Unit>(status = "error", message = "Invalid or expired admin token"))
                return@post
            }
            val adminId = decoded.subject ?: "admin_system"

            val req = call.receive<Map<String, String>>()
            val targetUserId = req["userId"] ?: return@post call.respond(HttpStatusCode.BadRequest)
            val action = req["action"] ?: return@post call.respond(HttpStatusCode.BadRequest)
            val reason = req["reason"] ?: "Admin manual risk action"

            if (action == "BAN") {
                RiskScoringService.setBanned(targetUserId, true, adminId, reason)
            } else if (action == "UNBAN") {
                RiskScoringService.setBanned(targetUserId, false, adminId, reason)
            } else if (action == "SET_SCORE") {
                val score = req["score"]?.toIntOrNull() ?: 50
                RiskScoringService.setOverride(targetUserId, score, adminId, reason)
            }

            call.respond(ApiResponse(data = mapOf("userId" to targetUserId, "action" to action, "applied" to true)))
        }
    }
}
