package com.ingames.database

import com.ingames.config.ConfigManager
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.slf4j.LoggerFactory
import java.io.InputStreamReader
import java.sql.Connection
import java.sql.DriverManager
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

object DatabaseFactory {
    private val logger = LoggerFactory.getLogger(DatabaseFactory::class.java)
    private var dataSource: HikariDataSource? = null
    var isConnectedToPostgres: Boolean = false
        private set

    fun init() {
        val config = ConfigManager.config
        try {
            val hikariConfig = HikariConfig().apply {
                jdbcUrl = "jdbc:postgresql://${config.dbHost}:${config.dbPort}/${config.dbName}"
                username = config.dbUser
                password = config.dbPassword
                maximumPoolSize = config.dbPoolSize
                isAutoCommit = true
                connectionTimeout = 3000
                initializationFailTimeout = 1000
            }
            dataSource = HikariDataSource(hikariConfig)
            dataSource?.connection?.use { conn ->
                val stmt = conn.createStatement()
                stmt.executeQuery("SELECT 1")
                logger.info("Connected successfully to PostgreSQL at {}:{}", config.dbHost, config.dbPort)
                isConnectedToPostgres = true
            }
            runMigrations()
        } catch (e: Exception) {
            logger.warn("PostgreSQL connection unavailable ({}). Initializing in-memory fallback store for standalone development/testing.", e.message)
            isConnectedToPostgres = false
        }
    }

    private fun runMigrations() {
        if (!isConnectedToPostgres) return
        val ds = dataSource ?: return
        val migrationFiles = listOf(
            "001_initial.sql",
            "002_wallet_integrity.sql",
            "003_admin_auth.sql",
            "004_game_integrity.sql",
            "005_indexes.sql"
        )
        ds.connection.use { conn ->
            for (fileName in migrationFiles) {
                try {
                    val stream = javaClass.classLoader.getResourceAsStream("db/migrations/$fileName")
                    if (stream != null) {
                        val sql = InputStreamReader(stream).readText()
                        conn.createStatement().use { stmt ->
                            stmt.execute(sql)
                        }
                        logger.info("Applied migration: {}", fileName)
                    }
                } catch (e: Exception) {
                    logger.warn("Migration notice on {}: {}", fileName, e.message)
                }
            }
        }
    }

    fun <T> withConnection(block: (Connection) -> T): T {
        val ds = dataSource
        if (isConnectedToPostgres && ds != null) {
            return ds.connection.use(block)
        }
        throw IllegalStateException("PostgreSQL is not connected")
    }
}

/**
 * In-Memory fallback store holding state during testing or when PostgreSQL is offline.
 */
object MemoryDataStore {
    val users = ConcurrentHashMap<String, MutableMap<String, Any?>>()
    val wallets = ConcurrentHashMap<String, MutableMap<String, Long>>() // userId -> map of balances
    val ledger = ConcurrentHashMap<String, MutableMap<String, Any?>>() // id -> record
    val deposits = ConcurrentHashMap<String, MutableMap<String, Any?>>()
    val withdrawals = ConcurrentHashMap<String, MutableMap<String, Any?>>()
    val gameRounds = ConcurrentHashMap<String, MutableMap<String, Any?>>()
    val bets = ConcurrentHashMap<String, MutableMap<String, Any?>>()
    val settlements = ConcurrentHashMap<String, MutableMap<String, Any?>>()
    val promotions = ConcurrentHashMap<String, MutableMap<String, Any?>>()

    private val roundSeq = AtomicLong(1000)

    init {
        // Seed initial mock promotions
        promotions["promo_1"] = mutableMapOf(
            "id" to "promo_1",
            "tag" to "DEPOSIT",
            "title" to "100% Welcome Bonus",
            "subtitle" to "DEPOSIT -> GET BONUS",
            "button_text" to "DEPOSIT NOW",
            "image_url" to "banners/deposit_banner.png",
            "target_screen" to "/add-cash",
            "status" to "ACTIVE"
        )
    }

    fun nextRoundNumber(): Long = roundSeq.incrementAndGet()
}
