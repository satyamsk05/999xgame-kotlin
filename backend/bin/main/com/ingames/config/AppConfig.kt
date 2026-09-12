package com.ingames.config

data class AppConfig(
    val port: Int = System.getenv("PORT")?.toIntOrNull() ?: 8080,
    val host: String = System.getenv("HOST") ?: "0.0.0.0",
    val environment: String = System.getenv("KTOR_ENV") ?: "development",
    val corsOrigin: String = System.getenv("CORS_ORIGIN") ?: "*",

    // Database
    val dbHost: String = System.getenv("DB_HOST") ?: "localhost",
    val dbPort: Int = System.getenv("DB_PORT")?.toIntOrNull() ?: 5432,
    val dbName: String = System.getenv("DB_NAME") ?: "ingames_db",
    val dbUser: String = System.getenv("DB_USER") ?: "postgres",
    val dbPassword: String = System.getenv("DB_PASSWORD") ?: "postgrespassword",
    val dbPoolSize: Int = System.getenv("DB_POOL_SIZE")?.toIntOrNull() ?: 10,

    // JWT Security
    val jwtSecret: String = System.getenv("JWT_SECRET") ?: "999xgame_super_secret_jwt_signing_key_32bytes",
    val adminJwtSecret: String = System.getenv("ADMIN_JWT_SECRET") ?: "999xgame_admin_jwt_secret_signing_key_32bytes",
    val jwtIssuer: String = "ingames-backend",
    val jwtAudience: String = "ingames-client",
    val jwtExpirationMs: Long = 30L * 24 * 60 * 60 * 1000 // 30 days
)

object ConfigManager {
    val config = AppConfig()
}
