# Codebase Concerns & Technical Debt

**Analysis Date: 2026-09-12**

## Technical Debt & Areas to Watch

### 1. Game Engine Logic Implementation
- **Status**: Skeleton / Contract Interface phase complete.
- **Details**: Core data structures (`GameRound`, `GameBet`, `GameResult`) and engine generic interfaces (`GameEngine`) are fully specified in `:games`. The actual algorithmic outcomes (e.g. Provably Fair RNG calculations, bomb placement algorithms for Mines, multiplier curves for Limbo) are currently stubs returning placeholder results.
- **Mitigation**: Implement game logic phase-by-phase with full unit test coverage verifying provable fairness.

### 2. Android UI & Client Route Integration
- **Status**: Navigation structure defined, screens ready for connection.
- **Details**: Native Compose UI screens in `android/app/src/main/kotlin/com/ingames/app/ui/` need to bind to `:games` client state managers and WebSocket events.
- **Mitigation**: Connect ViewModel instances to Ktor WebSocket client channels as backend game endpoints become live.

### 3. Database Schema Migrations & Production Readiness
- **Status**: SQL migration scripts organized in `database/migrations/`.
- **Details**: Production PostgreSQL DDL scripts for users, wallets, ledger, deposits, withdrawals, and game rounds must be executed prior to staging deployment.
- **Mitigation**: Utilize Flyway or automated Liquibase runner in production startup routines.

<!-- refreshed: 2026-09-12 -->
