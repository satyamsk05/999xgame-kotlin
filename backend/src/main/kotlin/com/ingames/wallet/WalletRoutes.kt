package com.ingames.wallet

import com.ingames.models.WithdrawalRequestPayload
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.walletRoutes() {
    authenticate("auth-jwt") {
        route("/api/wallet") {
            get("/balance") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.subject ?: "test_user_fin_1"
                val balance = WalletController.getBalance(userId)
                call.respond(HttpStatusCode.OK, balance)
            }

            get("/transactions") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.subject ?: "test_user_fin_1"
                val transactions = WalletController.getTransactions(userId)
                call.respond(HttpStatusCode.OK, transactions)
            }

            post("/deposit/initiate") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.subject ?: "test_user_fin_1"
                    val payload = call.receive<InitiateDepositPayload>()
                    val res = WalletController.initiateDeposit(userId, payload)
                    call.respond(HttpStatusCode.OK, res)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to (e.message ?: "Invalid deposit payload")))
                }
            }

            post("/deposit/utr") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.subject ?: "test_user_fin_1"
                    val payload = call.receive<SubmitUtrPayload>()
                    val ok = WalletController.submitUtr(userId, payload)
                    if (ok) {
                        call.respond(HttpStatusCode.OK, mapOf("status" to "success"))
                    } else {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Deposit ID not found"))
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to (e.message ?: "Invalid UTR payload")))
                }
            }

            post("/withdrawal/request") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.subject ?: "test_user_fin_1"
                    val payload = call.receive<WithdrawalRequestPayload>()
                    val result = WalletController.requestWithdrawal(userId, payload)
                    if (result.isSuccess) {
                        call.respond(HttpStatusCode.OK, result.getOrThrow())
                    } else {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to (result.exceptionOrNull()?.message ?: "Withdrawal failed")))
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to (e.message ?: "Invalid withdrawal payload")))
                }
            }
        }
    }
}
