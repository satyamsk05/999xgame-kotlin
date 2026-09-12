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
    val jwtIssuer: String = System.getenv("JWT_ISSUER") ?: "ingames-backend",
    val jwtAudience: String = System.getenv("JWT_AUDIENCE") ?: "ingames-client",
    val jwtExpirationMs: Long = (System.getenv("JWT_EXPIRATION_SECONDS")?.toLongOrNull() ?: (30L * 24 * 60 * 60)) * 1000L,
    val adminJwtExpirationMs: Long = (System.getenv("ADMIN_JWT_EXPIRATION_SECONDS")?.toLongOrNull() ?: (24L * 60 * 60)) * 1000L,

    // Redis
    val redisHost: String = System.getenv("REDIS_HOST") ?: "localhost",
    val redisPort: Int = System.getenv("REDIS_PORT")?.toIntOrNull() ?: 6379,
    val redisPassword: String? = System.getenv("REDIS_PASSWORD"),

    // Loggin.dev Integration
    val logginAppKey: String = System.getenv("LOGGIN_APP_KEY") ?: "",
    val logginApiUrl: String = System.getenv("LOGGIN_API_URL") ?: "https://api.loggin.dev/v1"
)

object ConfigManager {
    val config = AppConfig()
}
