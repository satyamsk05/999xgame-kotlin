# Technology Stack

**Analysis Date:** 2026-09-12

## Core Framework & Runtimes
- **JVM Target:** Java 17 (Java Toolchain)
- **Languages:** Kotlin 1.9+, Kotlin JVM, Kotlin Android
- **Build System:** Gradle (Kotlin DSL `build.gradle.kts`, `settings.gradle.kts`)
- **Android UI:** Jetpack Compose, Material3, Kotlin Coroutines, StateFlow

## Backend Framework & Core Libraries
- **Backend Framework:** Ktor Server (Kotlin/JVM)
- **Serialization:** `kotlinx.serialization.json`
- **Asynchronous Execution:** `kotlinx.coroutines.core`
- **Database Access:** HikariCP Connection Pooling, PostgreSQL JDBC driver
- **Authentication:** JWT (JSON Web Tokens), Bcrypt password hashing
- **Realtime Infrastructure:** Ktor WebSockets, Socket.IO / Custom WebSocket Protocol

## Multi-Module Architecture
- `:shared` - Shared DTOs, domain models, serialization specs
- `:backend` - Ktor REST API and WebSockets server
- `:admin` - Admin panel service controllers and management API
- `:games` - Multi-game modular engine library (8 base games)
- `:android:app` - Native Android Jetpack Compose client app

<!-- refreshed: 2026-09-12 -->
