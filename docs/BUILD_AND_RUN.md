# 999xgame Build & Run Guide

## 1. Prerequisites
- **Java Development Kit (JDK)**: 17 or 21 (Temurin or OpenJDK recommended)
- **Android SDK**: API 34 (Android 14) / Build Tools 34.0.0
- **PostgreSQL**: 15+ (Local or Docker)
- **Redis**: 7+ (Optional; in-memory cache fallback is active by default)

---

## 2. Setting Up the Database

### Using Docker Compose
```bash
cd database
docker-compose up -d postgres
```

### Manual PostgreSQL Setup
```bash
createdb ingames_db
# The Ktor backend automatically executes all SQL migrations located in `resources/db/migrations/` on startup.
```

---

## 3. Building and Running the Ktor Backend

### Environment Variables
Configure `.env` or set shell variables:
```bash
export PORT=8080
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=ingames_db
export DB_USER=postgres
export DB_PASSWORD=postgres
export JWT_SECRET=super_secret_jwt_key_for_development_minimum_32_chars
export ADMIN_JWT_SECRET=super_secret_admin_jwt_key_minimum_32_chars
```

### Run the Backend Server
```bash
./gradlew :backend:run
```
The server will start listening at `http://0.0.0.0:8080`.
- Health Check: `curl http://localhost:8080/health`
- Database Readiness: `curl http://localhost:8080/ready`
- WebSocket: `ws://localhost:8080/ws`

### Run Backend Unit & Integration Tests
```bash
./gradlew :backend:test
```

---

## 4. Building and Running the Android App

### Compile Debug APK
```bash
./gradlew :android:app:assembleDebug
```
Output APK location: `android/app/build/outputs/apk/debug/app-debug.apk`

### Run on Connected Device / Emulator
```bash
./gradlew :android:app:installDebug
adb shell am start -n com.ingames.app/.MainActivity
```

### Run Android Unit Tests
```bash
./gradlew :android:app:testDebugUnitTest
```
