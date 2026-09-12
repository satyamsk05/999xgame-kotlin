package com.ingames.auth

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.authRoutes() {
    route("/api/auth") {
        post("/register") {
            try {
                val req = call.receive<RegisterRequest>()
                val res = AuthController.register(req)
                if (res.success) {
                    call.respond(HttpStatusCode.OK, res)
                } else {
                    call.respond(HttpStatusCode.BadRequest, res)
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, AuthResponse(success = false, message = e.message))
            }
        }

        post("/login") {
            try {
                val req = call.receive<LoginRequest>()
                val res = AuthController.login(req)
                if (res.success) {
                    call.respond(HttpStatusCode.OK, res)
                } else {
                    call.respond(HttpStatusCode.Unauthorized, res)
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, AuthResponse(success = false, message = e.message))
            }
        }
    }
}
