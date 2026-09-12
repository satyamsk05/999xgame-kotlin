# 999xgame Kotlin Backend

Complete production-ready backend service for `999xgame-kotlin` built with Kotlin, Ktor Server, PostgreSQL, Redis, and Loggin.dev OTP-less WhatsApp Authentication.

---

## 🚀 Features

- **OTP-less WhatsApp Authentication**: Powered by Loggin.dev. Zero SMS OTP, zero 6-digit codes, zero password entry for users.
- **Wallet & Ledger Integrity**: Financial operations calculated strictly in integer-paise (no floating point money calculations). All balance updates are atomic, transactional, and idempotent.
- **Admin Management Portal**: Complete dashboard APIs with role-based JWT authentication, risk analytics, deposit/withdrawal approval workflows, and audit logging.
- **Real-Time WebSockets**: Live multi-room WebSocket hub for Seven Up Down, Crush, and Dragon Tiger multiplayer games.
- **Provably Fair Engine**: Cryptographically verifiable dice rolls, card draws, and crash multipliers.

---

## 📋 Requirements

- **JDK**: Java 17+
- **Gradle**: 8.x+ (bundled wrapper `./gradlew`)
- **PostgreSQL**: 14+ (optional for local dev; falls back to fast in-memory store if offline)
- **Redis**: 6.x+ (optional for local dev; falls back to session store if offline)

---

## ⚙️ Environment Variables & `.env`

Copy `.env.example` to `.env` in project root or backend directory:

```bash
cp .env.example .env
```

| Variable | Required | Purpose | Source / Where to get |
| -------- | -------- | ------- | --------------------- |
| `LOGGIN_APP_KEY` | Optional in dev / Required in prod | OTP-less WhatsApp auth key | [Loggin.dev Dashboard](https://loggin.dev) |
| `LOGGIN_API_URL` | Optional | Loggin.dev API base URL | Default `https://api.loggin.dev/v1` |
| `DB_HOST` | Required for PostgreSQL | Database host connection | PostgreSQL instance |
| `DB_NAME`, `DB_USER`, `DB_PASSWORD` | Required for PostgreSQL | Database credentials | PostgreSQL instance |
| `REDIS_HOST`, `REDIS_PORT` | Optional | Session & Cache store | Redis instance |
| `JWT_SECRET` | Required in prod | Application JWT signing | Cryptographic random 32-byte secret |
| `ADMIN_JWT_SECRET` | Required in prod | Admin JWT signing | Cryptographic random 32-byte secret |

---

## 🗄️ Database & Migrations

Database migrations are located in `src/main/resources/db/migrations/`:
- `001_initial.sql` — Users, wallets, games, bets
- `002_wallet_integrity.sql` — Wallet ledger, deposits, withdrawals, UTR tracking
- `003_admin_auth.sql` — Admin accounts, roles, permissions, audit logs
- `004_game_integrity.sql` — Game seeds, provably fair history
- `005_indexes.sql` — Performance indexes

Migrations run automatically on server initialization.

---

## 🛠️ Development & Startup

### Start Server
```bash
./gradlew :backend:run
```
The server will start on `http://0.0.0.0:8080`.

### Health & Ready Endpoints
- **Alive Check**: `GET /health`
- **Readiness Check**: `GET /ready` (verifies PostgreSQL connection state)

---

## 🧪 Testing

Run full test suite (Unit tests, Integration tests, Financial Idempotency tests):
```bash
./gradlew :backend:test
```

---

## 🔒 Security Best Practices

1. No OTP codes or hardcoded secrets exist in source files.
2. All financial debits/credits enforce `idempotencyKey` checks to prevent double-spending.
3. User JWT tokens and Admin JWT tokens use separate HMAC signing keys.
