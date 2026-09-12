You are a senior Kotlin, Android, Jetpack Compose, Ktor backend, multiplayer-game architecture, DevOps, and migration engineer.

PROJECT:
999xgame /Users/satyamkumar/999xgame

GOAL:
Completely migrate the existing 999xgame repository from the current Flutter/Dart frontend and existing backend implementation to a NEW Kotlin-based architecture.

IMPORTANT:
Do NOT perform a superficial Flutter-to-Kotlin conversion.
Do NOT simply wrap the existing Flutter application.
Do NOT preserve Flutter as the production frontend.
Build a new native Kotlin implementation using the existing project as the functional and visual reference.

The final production stack should be:

ANDROID FRONTEND:
- Kotlin
- Jetpack Compose
- Android SDK
- Kotlin Coroutines
- StateFlow / SharedFlow
- ViewModel
- Navigation Compose
- Retrofit/OkHttp where appropriate
- Kotlin serialization
- Room/DataStore where appropriate

BACKEND:
- Kotlin/JVM
- Ktor
- Kotlin Coroutines
- PostgreSQL
- Redis where required
- WebSockets for real-time game functionality
- REST APIs where appropriate
- JWT/authentication
- Proper service/repository architecture
- Database migrations
- Structured logging
- Configuration through environment variables

GAME LAYER:
The existing games and game logic must be carefully audited.
Do NOT blindly rewrite game logic before understanding it.

If the current games are implemented using Flutter Flame or another game framework, determine whether:
1. they should remain as an isolated game engine module,
2. be migrated to a suitable Kotlin/native rendering architecture,
3. or use another appropriate Android-compatible game technology.

The final decision must be based on the actual repository code, not assumptions.

==================================================
PHASE 1 — COMPLETE REPOSITORY AUDIT
==================================================

Before changing ANY production code:

1. Inspect the entire repository.
2. Identify:
   - Flutter frontend
   - Dart source
   - backend
   - APIs
   - database
   - authentication
   - WebSocket/real-time code
   - game logic
   - game state management
   - assets
   - animations
   - sounds
   - configuration
   - environment variables
   - deployment scripts
   - Docker files
   - AWS/infrastructure configuration
   - tests
   - CI/CD
3. Identify every Flutter/Dart dependency.
4. Identify every backend dependency.
5. Identify all API endpoints.
6. Identify all database tables/models.
7. Identify all authentication flows.
8. Identify all game state machines and rules.
9. Identify all screens and navigation flows.
10. Identify all reusable UI components.
11. Identify all networking code.
12. Identify all external services.

DO NOT modify code during this audit.

Create:

docs/MIGRATION_AUDIT.md

Include:

- Current architecture
- Frontend architecture
- Backend architecture
- Game architecture
- Dependency mapping
- API mapping
- Database mapping
- Authentication mapping
- WebSocket mapping
- Screen inventory
- Game inventory
- Risks
- Missing information
- Recommended target architecture

==================================================
PHASE 2 — TARGET ARCHITECTURE
==================================================

Create:

docs/KOTLIN_TARGET_ARCHITECTURE.md

Design a clean architecture.

Target repository structure should be approximately:

999xgame/
│
├── android/
│   ├── app/
│   ├── core/
│   ├── data/
│   ├── domain/
│   ├── feature/
│   ├── game/
│   └── ui/
│
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   └── kotlin/
│   │   └── test/
│   ├── resources/
│   └── build.gradle.kts
│
├── shared/
│
├── database/
│
├── infrastructure/
│
├── docs/
│
└── scripts/

You may improve this structure if the actual repository requires a better architecture.

Android architecture:

UI
↓
ViewModel
↓
UseCase
↓
Repository
↓
Data Source
↓
REST/WebSocket/Local DB

Backend architecture:

Routes
↓
Controller/Handler
↓
Service
↓
Repository
↓
PostgreSQL / Redis

Keep domain logic independent from frameworks wherever practical.

==================================================
PHASE 3 — FRONTEND MIGRATION
==================================================

Create a NEW native Android application.

Use:

Kotlin
Jetpack Compose
Material 3 only where appropriate

The UI must reproduce the existing Flutter application's design as accurately as possible.

Do NOT redesign the product.

Preserve:
- screen structure
- spacing
- typography
- colors
- gradients
- cards
- icons
- images
- animations
- navigation
- interactions
- loading states
- empty states
- error states
- dialogs
- bottom navigation
- game lobby
- wallet
- profile
- tournament UI
- game selection
- authentication

Do not invent new UX unless required to fix a real technical problem.

Create reusable Compose components instead of duplicating UI code.

Use:
- ViewModel
- StateFlow
- immutable UI state
- lifecycle-aware collection
- proper coroutine scopes

Avoid:
- global mutable state
- unnecessary singleton objects
- business logic inside composables
- hardcoded API URLs
- hardcoded secrets
- duplicated networking code

==================================================
PHASE 4 — BACKEND MIGRATION
==================================================

Create a NEW Kotlin Ktor backend.

Do NOT simply rename existing files.

Reimplement the backend using clean Kotlin architecture.

Requirements:

- Ktor
- Kotlin Coroutines
- PostgreSQL
- Redis where useful
- JWT authentication
- WebSocket support
- REST APIs
- validation
- centralized error handling
- structured logging
- configuration via environment variables
- database migrations
- transaction safety
- connection pooling
- rate limiting where appropriate
- proper security practices

Every existing API must be mapped.

Create:

docs/API_MIGRATION.md

with:

Old API
→ New Kotlin API
→ Request
→ Response
→ Authentication
→ Database interaction

Do not silently remove an existing API.

If an API is obsolete, document why.

==================================================
PHASE 5 — DATABASE
==================================================

Inspect the existing database schema.

Do NOT destroy existing production data.

Create a database migration strategy.

Document:

- tables
- columns
- primary keys
- foreign keys
- indexes
- constraints
- relationships
- migrations

Use PostgreSQL-compatible migrations.

If the existing database is already PostgreSQL, preserve compatibility wherever possible.

==================================================
PHASE 6 — AUTHENTICATION
==================================================

Audit the current authentication system.

Preserve existing user identity and account behavior.

Implement securely using Kotlin/Ktor.

Never hardcode:
- JWT secrets
- database passwords
- API keys
- AWS credentials
- private keys

Use environment variables/secrets.

==================================================
PHASE 7 — REAL-TIME GAME SYSTEM
==================================================

This is critical.

Inspect the current real-time architecture before implementing anything.

Determine:

- How players connect
- How rooms are created
- How matchmaking works
- How game state is synchronized
- How turns are validated
- How disconnect/reconnect works
- How timers work
- How results are generated
- How server authority is enforced
- How cheating is prevented

The server must remain authoritative for multiplayer game state.

Never trust the client for:
- dice results
- player balance
- winnings
- game results
- turn validity
- wallet transactions
- tournament results

Use WebSockets for real-time gameplay where appropriate.

Implement:
- reconnect
- heartbeat/ping
- disconnect handling
- room lifecycle
- authoritative state
- idempotent actions
- race-condition protection

==================================================
PHASE 8 — WALLET / MONEY / TRANSACTIONS
==================================================

If the repository contains wallet, balance, deposits, withdrawals, winnings, coins, or financial transactions:

Treat these as high-integrity systems.

Use:
- database transactions
- idempotency
- immutable transaction records
- balance consistency checks
- server-side validation
- audit logs

Never trust client-side balance values.

Do NOT expose secrets or sensitive financial data in logs.

Do NOT change financial behavior without documenting the change.

==================================================
PHASE 9 — GAME IMPLEMENTATION
==================================================

Inventory every game in the repository.

For each game document:

- rendering technology
- game rules
- state model
- animations
- networking
- assets
- sound
- input
- timers
- scoring
- multiplayer behavior

Then implement/migrate each game according to the actual existing architecture.

Preserve gameplay behavior exactly unless a bug is intentionally fixed.

Create:

docs/GAME_MIGRATION.md

with a migration status for every game.

==================================================
PHASE 10 — ASSETS
==================================================

Reuse existing assets where legally/project-appropriate.

Do not unnecessarily recreate assets.

Audit:
- PNG
- WebP
- SVG
- fonts
- sounds
- animations
- sprites
- JSON assets

Optimize Android assets appropriately.

Do not reduce visual quality without justification.

==================================================
PHASE 11 — TESTING
==================================================

Create comprehensive tests.

Frontend:
- ViewModel tests
- repository tests
- navigation tests
- Compose UI tests
- critical interaction tests

Backend:
- unit tests
- API integration tests
- database tests
- authentication tests
- WebSocket tests
- game-rule tests
- concurrency tests
- transaction tests

Game:
- rule validation
- turn validation
- scoring
- win/loss
- reconnect
- timeout
- multiplayer synchronization

==================================================
PHASE 12 — BUILD & VALIDATION
==================================================

The project must build successfully.

Android:

./gradlew assembleDebug

Backend:

./gradlew test

and appropriate backend build/run commands.

Fix compilation errors instead of documenting them as "known issues".

Do not leave TODO placeholders for core functionality.

==================================================
PHASE 13 — OLD FLUTTER CODE
==================================================

Do NOT delete the Flutter implementation immediately.

First create the Kotlin implementation and verify feature parity.

Then identify:

SAFE TO REMOVE
KEEP TEMPORARILY
REQUIRED FOR MIGRATION

Only remove Flutter code after confirming that nothing production-critical still depends on it.

Create:

docs/FLUTTER_REMOVAL_PLAN.md

==================================================
PHASE 14 — DOCUMENTATION
==================================================

Create/update:

docs/MIGRATION_AUDIT.md
docs/KOTLIN_TARGET_ARCHITECTURE.md
docs/API_MIGRATION.md
docs/DATABASE_MIGRATION.md
docs/GAME_MIGRATION.md
docs/FLUTTER_REMOVAL_PLAN.md
docs/BUILD_AND_RUN.md

BUILD_AND_RUN.md must explain exactly:

1. How to build Android
2. How to run backend
3. How to configure environment variables
4. How to run database
5. How to run Redis
6. How to run tests
7. How to run emulator/device
8. How frontend connects to backend

==================================================
CRITICAL RULES
==================================================

1. NEVER delete existing functionality just to make migration easier.

2. NEVER overwrite production configuration without first documenting it.

3. NEVER expose secrets.

4. NEVER invent APIs that don't exist without documenting them.

5. NEVER assume the game engine. Inspect the actual code.

6. NEVER assume the backend technology. Inspect it.

7. NEVER assume Flutter is the only frontend layer.

8. Preserve existing behavior.

9. Prefer clean new Kotlin code over mechanical Dart-to-Kotlin translation.

10. Avoid unnecessary dependencies.

11. Keep modules loosely coupled.

12. Use Kotlin idioms properly.

13. Use structured logging.

14. Make backend server authoritative for multiplayer games.

15. Preserve API compatibility where possible during migration.

16. Do not create unnecessary Git branches.

17. Work on the existing repository/current branch unless explicitly instructed otherwise.

18. Do not make unrelated changes.

19. Do not stop after creating documentation. Actually implement the migration.

20. At every major phase, verify compilation/tests before continuing.

==================================================
EXECUTION ORDER
==================================================

Follow this exact order:

PHASE 1
Audit repository

PHASE 2
Design Kotlin architecture

PHASE 3
Create Kotlin Android shell

PHASE 4
Implement shared networking/domain models

PHASE 5
Implement Ktor backend

PHASE 6
Implement authentication

PHASE 7
Implement database layer

PHASE 8
Implement real-time/WebSocket layer

PHASE 9
Migrate Home/Lobby UI

PHASE 10
Migrate Profile/Wallet/Tournament/etc.

PHASE 11
Migrate games

PHASE 12
Integrate frontend + backend

PHASE 13
Run complete tests

PHASE 14
Build APK

PHASE 15
Document final migration

==================================================
FINAL REPORT
==================================================

At the end provide:

1. What was migrated
2. What remains
3. Frontend architecture
4. Backend architecture
5. Database architecture
6. Game architecture
7. API mapping
8. Test results
9. Build result
10. Any blockers
11. Any behavior differences from the Flutter version
12. Files created
13. Files modified
14. Files removed
15. Exact commands to run the final project

IMPORTANT:
Do not claim the migration is complete unless the project actually compiles and the critical functionality has been verified.