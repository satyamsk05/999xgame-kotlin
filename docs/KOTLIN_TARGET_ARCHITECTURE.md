# 999xgame Kotlin Target Architecture

## 1. Directory & Module Organization

```
999xgame-kotlin/
│
├── android/                        # Native Android Application (Jetpack Compose)
│   ├── app/                        # Application entry point, NavHost, Main Activity
│   ├── core/
│   │   ├── ui/                     # Design System (Theme, Colors, Typography, Common Widgets)
│   │   ├── network/                # Ktor HTTP Client, WebSockets, OkHttp interceptors
│   │   ├── model/                  # Android domain/presentation data classes
│   │   └── datastore/              # Preferences DataStore (tokens, cached settings)
│   ├── feature/
│   │   ├── auth/                   # Phone OTP Login & Verification UI/ViewModel
│   │   ├── home/                   # Lobby, Online Ticker, Banners, Game Cards
│   │   ├── wallet/                 # Wallet breakdown, Add Cash, Withdraw, Transactions
│   │   ├── games/                  # Native Compose Games & Webview Bridge
│   │   └── profile/                # Profile, Avatar Picker, Share/Refer, Help, Settings
│   └── build.gradle.kts
│
├── backend/                        # High Performance Ktor JVM Backend
│   ├── src/
│   │   ├── main/
│   │   │   ├── kotlin/com/ingames/
│   │   │   │   ├── Application.kt  # Ktor Server Startup & Plugins configuration
│   │   │   │   ├── config/         # AppConfig, EnvLoader
│   │   │   │   ├── auth/           # JWT, OTP Provider, PasswordHasher
│   │   │   │   ├── database/       # HikariCP, TransactionManager, Schema Migrator
│   │   │   │   ├── wallet/         # WalletService, Ledger, Deposits, Withdrawals
│   │   │   │   ├── games/          # GameManager, SevenUpDown, Crush, DragonTiger
│   │   │   │   ├── websocket/      # Real-time WebSocket session hub & room routing
│   │   │   │   ├── admin/          # Admin backoffice API endpoints
│   │   │   │   └── utils/          # Logger, Validation, Response Contracts
│   │   │   └── resources/
│   │   │       ├── application.conf
│   │   │       ├── logback.xml
│   │   │       ├── public/         # Static assets (Avatars, Game Icons, HTML5 games)
│   │   │       └── db/migrations/  # SQL schema migrations (001 to 005)
│   │   └── test/                   # JUnit 5 & Ktor Server Test Host suites
│   └── build.gradle.kts
│
├── shared/                         # Multiplatform / Shared Data Models & Serialization
│   ├── src/commonMain/kotlin/
│   └── build.gradle.kts
│
├── database/                       # Raw SQL scripts and Docker postgres setup
│   ├── migrations/
│   └── docker-compose.yml
│
└── docs/                           # Architecture, Migration, and Run Guides
```

---

## 2. Android Layer Architecture (Clean MVI / MVVM)

```
+-------------------------------------------------------------+
|                      UI Layer (Compose)                     |
|  - Screen Composables (HomeScreen, WalletScreen, etc.)      |
|  - Stateless Components (TopHeader, GameCard, Ticker)       |
+-------------------------------------------------------------+
                              | StateFlow<UiState> / Events
                              v
+-------------------------------------------------------------+
|                     ViewModel Layer                         |
|  - HomeViewModel, WalletViewModel, GameViewModel            |
|  - CoroutineScope (viewModelScope)                          |
+-------------------------------------------------------------+
                              | UseCases / Repositories
                              v
+-------------------------------------------------------------+
|                    Data & Network Layer                     |
|  - GameRepository, WalletRepository, AuthRepository         |
|  - Ktor HTTP Client / WebSocket Client                      |
|  - DataStore Token Manager                                  |
+-------------------------------------------------------------+
```

---

## 3. Backend Layer Architecture (Ktor JVM)

```
+-------------------------------------------------------------+
|                    Routing & Controllers                    |
|  - AuthRoutes, WalletRoutes, GameRoutes, AdminRoutes        |
|  - WebSocket Routing (game:seven_up_down, game:crush, etc.) |
+-------------------------------------------------------------+
                              |
                              v
+-------------------------------------------------------------+
|                       Service Layer                         |
|  - AuthService, FinancialService (ACID Ledger), GameManager |
|  - ProvablyFairEngine, ReconciliationService                |
+-------------------------------------------------------------+
                              |
                              v
+-------------------------------------------------------------+
|                     Repository Layer                        |
|  - WalletRepository, UserRepository, GameRepository         |
|  - withTransaction { client -> ... }                        |
+-------------------------------------------------------------+
                              |
                              v
+-------------------------------------------------------------+
|               Data Storage (PostgreSQL & Redis)             |
|  - HikariCP Connection Pool                                 |
|  - In-Memory / Redis Hot Cache                              |
+-------------------------------------------------------------+
```

---

## 4. Key Design Patterns & Guarantees

1. **Server-Authoritative Real-Time Gaming**:
   - The game round status, multiplier ticks, and dice rolls are calculated strictly on the backend with millisecond-accurate timestamps.
   - Clients only render the interpolated state and send idempotent bet/action commands.
2. **Double-Entry Financial Ledger**:
   - Balance changes always require atomic row locking on `wallets` (`SELECT ... FOR UPDATE`) accompanied by an immutable ledger entry on `wallet_ledger`.
   - Balances are partitioned into `deposit_balance`, `winnings_balance`, and `bonus_balance`.
3. **Provably Fair Commit-Reveal**:
   - Prior to accepting bets, the server commits `server_seed_hash = SHA256(server_seed + round_number)`.
   - Upon round completion, `server_seed` is broadcast to allow independent client verification.
