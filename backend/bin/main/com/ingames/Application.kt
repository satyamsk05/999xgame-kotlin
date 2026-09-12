package com.ingames

import com.ingames.auth.JwtService
import com.ingames.config.ConfigManager
import com.ingames.database.DatabaseFactory
import com.ingames.games.GameManager
import com.ingames.routes.configureRoutes
import com.ingames.websocket.WebSocketHub
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import java.io.File
import java.time.Duration

fun main() {
    val config = ConfigManager.config
    val logger = LoggerFactory.getLogger("com.ingames.Main")
    logger.info("Starting InGames Ktor Backend Server on {}:{}", config.host, config.port)

    embeddedServer(Netty, port = config.port, host = config.host, module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    DatabaseFactory.init()

    val wsHub = WebSocketHub()
    val gameManager = GameManager(wsHub)
    gameManager.startAll()

    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
                encodeDefaults = true
            }
        )
    }

    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Get)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        anyHost()
    }

    install(CallLogging) {
        level = Level.INFO
    }

    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(30)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    install(Authentication) {
        jwt("auth-jwt") {
            realm = "ingames-backend"
            verifier(JwtService.userVerifier)
            validate { credential ->
                if (credential.payload.getClaim("type").asString() == "USER" && !credential.payload.subject.isNullOrBlank()) {
                    JWTPrincipal(credential.payload)
                } else null
            }
            challenge { _, _ ->
                call.respond(
                    HttpStatusCode.Unauthorized,
                    mapOf("status" to "error", "code" to "UNAUTHORIZED", "message" to "Token is invalid or expired")
                )
            }
        }
    }

    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respond(
                HttpStatusCode.InternalServerError,
                mapOf(
                    "status" to "error",
                    "code" to "INTERNAL_ERROR",
                    "message" to (cause.message ?: "An unexpected error occurred")
                )
            )
        }
    }

    // Static assets & HTML5 games
    routing {
        staticResources("/", "public")
    }

    configureRoutes(gameManager, wsHub)
}
