package com.ingames.routes

import com.ingames.admin.adminRoutes
import com.ingames.auth.authRoutes
import com.ingames.auth.JwtService
import com.ingames.database.DatabaseFactory
import com.ingames.database.MemoryDataStore
import com.ingames.games.GameManager
import com.ingames.models.*
import com.ingames.users.UserRepository
import com.ingames.wallet.FinancialService
import com.ingames.websocket.WebSocketHub
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

fun Application.configureRoutes(gameManager: GameManager, wsHub: WebSocketHub) {
    routing {
        adminRoutes()

        // --- PUBLIC & PROBES ---
        get("/health") {
            call.respond(
                mapOf(
                    "status" to "ok",
                    "service" to "ingames-backend",
                    "uptime" to (System.currentTimeMillis() / 1000).toString()
                )
            )
        }

        get("/ready") {
            if (DatabaseFactory.isConnectedToPostgres) {
                call.respond(mapOf("status" to "ready", "db" to "connected"))
            } else {
                call.respond(mapOf("status" to "ready", "db" to "in_memory_mode"))
            }
        }

        get("/api/config") {
            call.respond(
                ApiResponse(
                    data = AppConfigData(
                        onlineUsers = wsHub.getActiveUserCount(),
                        maintenanceMode = false,
                        minimumAppVersion = "1.0.0"
                    )
                )
            )
        }

        get("/api/online-ticker") {
            val count = wsHub.getActiveUserCount()
            call.respond(
                ApiResponse(
                    data = OnlineTickerData(
                        totalOnline = count,
                        formattedText = "$count online",
                        isLive = true
                    )
                )
            )
        }

        get("/api/banners") {
            val list = MemoryDataStore.promotions.values.map {
                BannerItem(
                    id = it["id"] as String,
                    tag = it["tag"] as? String ?: "DEPOSIT",
                    title = it["title"] as? String ?: "",
                    subtitle = it["subtitle"] as? String ?: "",
                    buttonText = it["button_text"] as? String ?: "DEPOSIT NOW",
                    imageUrl = it["image_url"] as? String ?: "banners/deposit_banner.png",
                    targetScreen = it["target_screen"] as? String ?: "/add-cash"
                )
            }
            call.respond(ApiResponse(data = list))
        }

        // --- AUTH ---
        authRoutes()

        // --- GAMES (PUBLIC READ) ---
        get("/api/games") {
            call.respond(ApiResponse(data = gameManager.gamesList))
        }

        get("/api/games/seven-up-down/state") {
            call.respond(ApiResponse(data = gameManager.sevenUpDownEngine.getState()))
        }

        get("/api/games/crush/state") {
            call.respond(ApiResponse(data = gameManager.crushEngine.getState()))
        }

        get("/api/games/dragon-tiger/state") {
            call.respond(ApiResponse(data = gameManager.dragonTigerEngine.getState()))
        }

        // --- AUTHENTICATED USER ROUTES ---
        authenticate("auth-jwt") {
            route("/api/user") {
                get("/profile") {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.subject ?: return@get call.respond(HttpStatusCode.Unauthorized)
                    val profile = UserRepository.getUserById(userId)
                    if (profile != null) {
                        call.respond(ApiResponse(data = profile))
                    } else {
                        call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(status = "error", message = "User not found"))
                    }
                }

                put("/avatar") {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.subject ?: return@put call.respond(HttpStatusCode.Unauthorized)
                    val req = call.receive<UpdateAvatarRequest>()
                    UserRepository.updateAvatar(userId, req.avatar)
                    call.respond(ApiResponse(data = mapOf("avatar" to req.avatar)))
                }

                get("/stats") {
                    call.respond(
                        ApiResponse(
                            data = UserStats(
                                totalGamesPlayed = 48,
                                totalWonAmountPaise = 345000L,
                                referralCount = 5,
                                referralEarningsPaise = 25000L
                            )
                        )
                    )
                }
            }

            // Wallet
            route("/api/wallet") {
                get("/balance") {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.subject ?: return@get call.respond(HttpStatusCode.Unauthorized)
                    val bal = FinancialService.getWalletBalance(userId)
                    call.respond(ApiResponse(data = bal))
                }

                get("/transactions") {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.subject ?: return@get call.respond(HttpStatusCode.Unauthorized)
                    val txs = FinancialService.getTransactions(userId)
                    call.respond(ApiResponse(data = txs))
                }
            }

            // Deposits
            route("/api/deposits") {
                post("/initiate") {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.subject ?: return@post call.respond(HttpStatusCode.Unauthorized)
                    val req = call.receive<InitiateDepositRequest>()
                    val res = FinancialService.initiateDeposit(userId, req.amountRupees, req.paymentMethod)
                    call.respond(ApiResponse(data = res))
                }

                post("/submit-utr") {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.subject ?: return@post call.respond(HttpStatusCode.Unauthorized)
                    val req = call.receive<SubmitUtrRequest>()
                    val ok = FinancialService.submitUtr(userId, req.depositId, req.utr)
                    if (ok) {
                        call.respond(ApiResponse(data = mapOf("status" to "VERIFYING")))
                    } else {
                        call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(status = "error", message = "Deposit order not found"))
                    }
                }
            }

            // Withdrawals
            route("/api/withdrawals") {
                post("/request") {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.subject ?: return@post call.respond(HttpStatusCode.Unauthorized)
                    val req = call.receive<WithdrawalRequestPayload>()
                    val res = FinancialService.requestWithdrawal(userId, req)
                    if (res.isSuccess) {
                        call.respond(ApiResponse(data = res.getOrNull()))
                    } else {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ApiResponse<Unit>(status = "error", message = res.exceptionOrNull()?.message)
                        )
                    }
                }
            }

            // Game Betting Actions
            route("/api/games") {
                post("/seven-up-down/bet") {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.subject ?: return@post call.respond(HttpStatusCode.Unauthorized)
                    val req = call.receive<SevenUpDownBetRequest>()
                    val res = gameManager.sevenUpDownEngine.placeBet(
                        userId = userId,
                        area = req.area,
                        amountPaise = req.amountPaise,
                        idempotencyKey = req.idempotencyKey
                    )
                    if (res.isSuccess) {
                        call.respond(ApiResponse(data = res.getOrNull()))
                    } else {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ApiResponse<Unit>(status = "error", message = res.exceptionOrNull()?.message)
                        )
                    }
                }

                post("/crush/bet") {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.subject ?: return@post call.respond(HttpStatusCode.Unauthorized)
                    val req = call.receive<CrushBetRequest>()
                    val res = gameManager.crushEngine.placeBet(
                        userId = userId,
                        amountPaise = req.amountPaise,
                        autoCashout = req.autoCashoutMultiplier,
                        idempotencyKey = req.idempotencyKey
                    )
                    if (res.isSuccess) {
                        call.respond(ApiResponse(data = res.getOrNull()))
                    } else {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ApiResponse<Unit>(status = "error", message = res.exceptionOrNull()?.message)
                        )
                    }
                }

                post("/crush/cashout") {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.subject ?: return@post call.respond(HttpStatusCode.Unauthorized)
                    val res = gameManager.crushEngine.cashout(userId)
                    if (res.isSuccess) {
                        call.respond(ApiResponse(data = mapOf("multiplier" to res.getOrNull())))
                    } else {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ApiResponse<Unit>(status = "error", message = res.exceptionOrNull()?.message)
                        )
                    }
                }

                post("/dragon-tiger/bet") {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.subject ?: return@post call.respond(HttpStatusCode.Unauthorized)
                    val req = call.receive<DragonTigerBetRequest>()
                    val res = gameManager.dragonTigerEngine.placeBet(
                        userId = userId,
                        area = req.area,
                        amountPaise = req.amountPaise,
                        idempotencyKey = req.idempotencyKey
                    )
                    if (res.isSuccess) {
                        call.respond(ApiResponse(data = res.getOrNull()))
                    } else {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ApiResponse<Unit>(status = "error", message = res.exceptionOrNull()?.message)
                        )
                    }
                }
            }
        }

        // --- REAL-TIME WEBSOCKET HUB ---
        webSocket("/ws") {
            val token = call.request.queryParameters["token"]
            var userId: String? = null
            if (token != null) {
                val decoded = JwtService.verifyUserToken(token)
                userId = decoded?.subject
            }

            wsHub.registerSession(this, userId)
            try {
                for (frame in incoming) {
                    if (frame is Frame.Text) {
                        val text = frame.readText()
                        try {
                            val msg = Json.decodeFromString<WsMessage>(text)
                            when (msg.action) {
                                "subscribe" -> {
                                    msg.room?.let { wsHub.joinRoom(this, it) }
                                }
                                "unsubscribe" -> {
                                    msg.room?.let { wsHub.leaveRoom(this, it) }
                                }
                                "ping" -> {
                                    send(Frame.Text(Json.encodeToString(WsMessage(event = "pong"))))
                                }
                            }
                        } catch (e: Exception) {
                            // Non-json ping/frame
                        }
                    }
                }
            } finally {
                wsHub.unregisterSession(this, userId)
            }
        }
    }
}
