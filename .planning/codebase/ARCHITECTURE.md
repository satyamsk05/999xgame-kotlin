# System Architecture

**Analysis Date:** 2026-09-12

## Architectural Overview
`999xgame-kotlin` is structured as a production multi-module Kotlin application adhering to Clean Architecture & Domain-Driven Design (DDD) principles:

```text
                        ┌───────────────────┐
                        │    ADMIN PANEL    │
                        └─────────┬─────────┘
                                  │
                                  ▼
┌───────────────┐         ┌───────────────────┐
│ ANDROID APP   │◄───────►│ KOTLIN BACKEND    │
│ Kotlin Compose│ REST/WS │ Ktor / JVM        │
└───────┬───────┘         └─────────┬─────────┘
        │                           │
        ▼                           ▼
┌───────────────┐         ┌───────────────────┐
│ GAMES MODULE  │         │  POSTGRES / REDIS │
│ 8 Base Games  │         │ Storage & Cache   │
└───────────────┘         └─────────┬─────────┘
```

## Layers & Core Component Contracts
1. **Core Framework (`games/src/main/kotlin/com/ingames/games/core/`)**:
   - `Game.kt`, `GameEngine.kt`, `GameState.kt`, `GameConfig.kt`, `GameRound.kt`, `GameBet.kt`, `GameResult.kt`, `GameValidator.kt`, `GameSettlement.kt`
2. **Game Domain Engines (`games/src/main/kotlin/com/ingames/games/games/`)**:
   - Coin Flip, Classic Dice, Keno, Mines, Perya Color Game, Ring of Fortune, Double, Limbo.
3. **Backend Domain Modules (`backend/src/main/kotlin/com/ingames/`)**:
   - Auth, Users, Wallet, Deposits, Withdrawals, Transactions, KYC, Realtime, Redis, Audit, Security.

<!-- refreshed: 2026-09-12 -->
