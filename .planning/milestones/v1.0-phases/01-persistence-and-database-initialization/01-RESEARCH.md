# Phase 1: Technical Research (Persistence & Database Initialization)

**Analysis Date: 2026-09-12**

## Technical Context & Ecosystem
- **Database Engine**: PostgreSQL 15+
- **Connection Pool**: HikariCP (Java/Kotlin high-performance JDBC connection pool)
- **Framework**: Ktor Server (JVM) with JWT authentication feature plugin (`ktor-server-auth-jwt`)
- **Password Hashing**: Bcrypt (`org.mindrot:jbcrypt` or `BCrypt`)

## Architecture & Design Choices
1. **HikariCP Configuration**:
   - Max pool size: 10 connections
   - Connection timeout: 30,000ms
   - Idle timeout: 600,000ms
   - Configured via environment variables / `AppConfig.kt`
2. **Schema Initialization**:
   - Execute DDL migration scripts in order from `database/migrations/`:
     - `001_initial.sql` (users, wallets, games, bets)
     - `002_wallet_integrity.sql`
     - `003_admin_auth.sql`
     - `004_game_integrity.sql`
     - `005_indexes.sql`
3. **JWT Authentication & Auth Routing**:
   - Token issuer: `com.ingames`
   - Token secret: Loaded from environment configuration
   - Expiration: 24 hours
   - Endpoints: `POST /api/auth/register`, `POST /api/auth/login`

<!-- refreshed: 2026-09-12 -->
