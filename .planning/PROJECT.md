# Project Specification: 999xgame-kotlin

## What This Is
A multi-module Kotlin casino and gaming backend with real-time WebSockets, integer-paise financial ledger, 8 provably fair game engines, role-based admin panel, Jetpack Compose Android app integration with live streaming enhancements, and automated risk scoring / analytics governance.

## Core Value
Provably fair, low-latency gaming architecture built on double-entry financial ledger principles, resilient live streaming WebSockets, automated risk management, and robust Kotlin multi-module standards.

## Requirements

### Validated
- ✓ Database & Persistence Layer (PostgreSQL & HikariCP migration DDLs) — v1.0
- ✓ Financial Ledger & Wallet Subsystem (Integer paise balance calculation) — v1.0
- ✓ Provably Fair Base Game Engines (Coin Flip, Dice, Keno, Mines, Perya, Ring, Double, Limbo) — v1.0
- ✓ Realtime Gateway & WebSocket Communication (Session manager & event routing) — v1.0
- ✓ Admin Governance & Audit Logging (BCrypt auth, withdrawal processing, audit trail) — v1.0
- ✓ Android App Integration & End-to-End Verification (`RealtimeClient.kt` & E2E suite) — v1.0
- ✓ Shimmer Loading Skeletons & Compose UI Polish — v1.1
- ✓ Realtime Client Exponential Backoff Reconnection Resilience — v1.1
- ✓ Tactile Haptic Feedback & Win Visual Overlays — v1.1
- ✓ User Risk Scoring Engine (`LOW`, `MEDIUM`, `HIGH`, `CRITICAL`) — v1.2
- ✓ Automated Risk-Flagged Withdrawal Locking (`RISK_LOCKED`) — v1.2
- ✓ Platform Analytics Summary Endpoints (GGR, NGR, DAU, Net Deposit Volume) — v1.2
- ✓ Admin Risk Management Controller & Audit Logging — v1.2

### Active
- [ ] Next Milestone Features (Multi-currency support, VIP Loyalty Tier Engine, Custom Private Tables)

### Out of Scope
- Microservices refactoring — monorepo Gradle multi-module approach is sufficient for current scale.

## Key Decisions
| Decision | Outcome | Status |
|---|---|:---:|
| Integer Paise Monetary Values | Eliminates floating-point precision errors | ✓ Good |
| Provably Fair SHA-256 Engine | Enables client-side result verification | ✓ Good |
| Double-Entry Wallet Ledger | Ensures immutable audit trail for all balance mutations | ✓ Good |
| Exponential Backoff WebSockets | Guarantees automatic reconnection on network drops | ✓ Good |
| Automated Risk Scoring & Locking | Prevents instant fraudulent withdrawal payouts | ✓ Good |

---
*Last updated: 2026-09-12 after v1.2 milestone completion*
