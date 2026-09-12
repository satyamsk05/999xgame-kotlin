# 999xgame Migration Audit

## Executive Summary
This document provides a comprehensive audit of the legacy **999xgame** codebase (located at `/Users/satyamkumar/999xgame`) to guide the complete migration to a native **Kotlin** ecosystem (Jetpack Compose on Android + Ktor JVM Backend + PostgreSQL + Redis + WebSockets).

---

## 1. Current Architecture Overview

```
+-------------------------------------------------------------+
|                      Client Layer                           |
|  - Flutter (Mobile/Web/Desktop)                             |
|  - HTML5 Canvas/DOM Games (Seven Up Down, Fruit Slice)      |
+-------------------------------------------------------------+
                              | REST / WebSocket (Socket.IO)
                              v
+-------------------------------------------------------------+
|                      Backend Layer                          |
|  - Node.js / Express 4.x                                    |
|  - Socket.IO Realtime Server                                |
|  - PostgreSQL 15+ (pg driver, raw SQL queries)              |
|  - Redis 7.x (Optional / In-Memory Fallback)                |
|  - JWT Authentication (Bearer Tokens)                       |
+-------------------------------------------------------------+
```

---

## 2. Legacy Component Inventory

### 2.1 Frontend (Flutter / Dart)
- **Framework**: Flutter 3.x (Dart 3.x)
- **Key Modules**:
  - `lib/main.dart`: Root widget, navigation shell, responsive device frame (1080x2400 aspect container), deep dark theme.
  - `lib/theme/app_colors.dart`: Dark luxury casino palette (`#1F0130` -> `#0F0016`, Gold `#E1B219`, Green `#00B57F`).
  - `lib/widgets/`: `TopHeader`, `OnlineTicker`, `PromoBanner`, `GameCard`, `CustomBottomNavBar`, `MobileDeviceFrame`, `ShimmerBox`, `NetworkErrorWidget`, `InAppUpdateDialog`.
  - `lib/screens/`:
    - `login_screen.dart`: Phone number input, OTP request & verify.
    - `wallet_screen.dart`: Balance breakdown (Total, Deposit, Winnings, Rewards), actions, transactions button.
    - `add_cash_screen.dart`: Preset amounts (₹100, ₹500, ₹1000, etc.), cashback offers, UPI payment gateway initiation, UTR submit dialog.
    - `withdraw_screen.dart`: Amount input, TDS/fee calculator, UPI ID / Bank account validation, withdrawal request.
    - `transactions_screen.dart`: Transaction history (Deposits, Withdrawals, Game Wins/Losses, Bonuses) with status pills and filter tabs.
    - `share_screen.dart`: Refer & Earn dashboard, invite code, WhatsApp/social share, referral earnings list.
    - `profile_screen.dart`: User profile, avatar changer, KYC status, statistics, quick settings links.
    - `settings_screen.dart`: Sound, vibration, notification toggles, language, app version.
    - `help_centre_screen.dart`: Searchable FAQ accordion, quick support contact.
    - `contact_us_screen.dart`: Telegram, WhatsApp, Email, Live chat links.
    - `fair_play_screen.dart`: Provably fair explanation, SHA-256 hash verifier.
    - `reported_issues_screen.dart`: Support tickets, query status.
    - `html5_game_screen.dart`: WebView integration for HTML5 games with JS bridge.

### 2.2 Backend (Node.js / Express)
- **Runtime**: Node.js with Express 4.x & Socket.IO
- **Key Directories**:
  - `src/server/app.js`: Express app, middleware (CORS, RequestId, ResponseContract, RateLimiting, ErrorHandling), route registration.
  - `src/server/http.js`: HTTP server with Socket.IO server, token auth handshake, room subscription management.
  - `src/database/`: `db.js` (pg pool), `schema.sql`, migrations (`001_initial.sql` - `005_indexes.sql`), `redis.js`.
  - `src/auth/`: JWT issuance/verification, OTP login service, session validation.
  - `src/users/`: Profile retrieval, avatar update, user status.
  - `src/wallet/`: `wallet.repository.js`, `financial.service.js`, `deposit.controller.js`, `withdrawal.controller.js`, `admin_deposit.controller.js`, `admin_withdrawal.controller.js`.
  - `src/games/`:
    - `game.manager.js`: Orchestrator for all game engines.
    - `seven-up-down/`: `engine.js`, `game.repository.js`, `seven_up_down.scheduler.js` (Provably fair 2-dice game).
    - `crush/`: `crush.engine.js`, `crush.repository.js`, `crush.scheduler.js` (Crash multiplier game).
    - `dragon-tiger/`: `dragon_tiger.engine.js`, `dragon_tiger.repository.js`, `dragon_tiger.scheduler.js` (2-card high-card game).
  - `src/services/`: `financial.service.js` (Atomic ACID transactions & dual-entry ledger), `reconciliation.service.js` (Balance drift checker), `audit.service.js`, `telegram.service.js`, `redis.service.js`.
  - `src/admin/`: Admin controllers for stats, users, game controls, promotions, financial reviews, reports.

---

## 3. Database Schema & Entities

| Table Name | Primary Purpose | Key Constraints / Indexes |
|---|---|---|
| `users` | User accounts, phone, name, avatar, KYC, status | `phone` UNIQUE, `is_blocked`, `role` |
| `wallets` | User balance (deposit, winnings, bonus, reserved) | `user_id` UNIQUE FK, non-negative checks |
| `wallet_ledger` | Immutable dual-entry financial journal | `idempotency_key` UNIQUE, `reference_type`, `reference_id` |
| `deposits` | Deposit orders & payment receipts | `deposit_id` UNIQUE, `utr` UNIQUE (when not null), status |
| `withdrawals` | Payout requests & status | `withdrawal_id` UNIQUE, status, user FK |
| `game_rounds` | Game round lifecycle & provably fair seeds | `(game_id, round_number)` UNIQUE, `server_seed_hash`, status |
| `bets` | Individual user wagers | `idempotency_key` UNIQUE, `(round_id, user_id)` indexes |
| `settlements` | Round payouts & win calculations | `bet_id` UNIQUE FK, `payout_amount` |
| `promotions` | Banners & active bonuses | `status` ('ACTIVE'/'INACTIVE'), ordering |
| `admins` | Backoffice administrative accounts | `username` UNIQUE, password hash |
| `audit_logs` | Security & compliance audit trail | `(user_id, created_at)`, `(admin_id, created_at)` |

---

## 4. API Endpoints Inventory

### Public / Configuration
- `GET /health` -> `{ status: "ok", service: "ingames-backend", uptime }`
- `GET /ready` -> `{ status: "ready", db: "connected" }`
- `GET /api/config` -> `{ onlineUsers, maintenanceMode, minimumAppVersion }`
- `GET /api/online-ticker` -> `{ totalOnline, label, ringColors, avatars, isLive }`
- `GET /api/banners` -> List of active promo banners

### Authentication (`/api/auth`)
- `POST /api/auth/otp/send` -> Send 6-digit OTP to phone
- `POST /api/auth/otp/verify` -> Verify OTP, return JWT token & user profile
- `POST /api/auth/token/refresh` -> Refresh expired session token
- `POST /api/auth/logout` -> Revoke active session

### User Profile (`/api/user` & `/api/app`)
- `GET /api/user/profile` -> Current user details & wallet summary
- `PUT /api/user/avatar` -> Update selected avatar ID
- `GET /api/user/stats` -> Win/loss and referral statistics

### Wallet & Payments (`/api/wallet`, `/api/deposits`, `/api/withdrawals`)
- `GET /api/wallet/balance` -> Real-time balance (`deposit_balance`, `winnings_balance`, `bonus_balance`, `total`)
- `GET /api/wallet/transactions` -> Paginated ledger transaction history
- `POST /api/deposits/initiate` -> Create deposit intent with UPI QR / details
- `POST /api/deposits/submit-utr` -> Submit 12-digit UPI UTR reference for manual verification
- `GET /api/deposits/history` -> User deposit history
- `POST /api/withdrawals/request` -> Request withdrawal via UPI ID or Bank account
- `GET /api/withdrawals/history` -> User withdrawal history

### Real-Time Games (`/api/games`)
- `GET /api/games` -> List of available games & active player counts
- `GET /api/games/:gameId/state` -> Current round status, timer, seed hash, recent history
- `POST /api/games/:gameId/bet` -> Place authoritative wager (Atomic debit)
- `POST /api/games/crush/cashout` -> Cashout Crash wager before crash point

---

## 5. Game Engines & State Machines

### 5.1 Seven Up Down (`seven_up_down`)
- **Cycle**: `BETTING_OPEN` (15s) -> `BETTING_CLOSED` (2s) -> `RESULT` / Roll Dice (3s) -> `SETTLED` (3s pause)
- **Rules**: 2 Standard 6-sided dice rolled ($D_1 + D_2 \in [2, 12]$).
  - `DOWN` (2 to 6): Pays 2.0x
  - `SEVEN` (Exactly 7): Pays 5.0x
  - `UP` (8 to 12): Pays 2.0x
- **Provably Fair**: `SHA256(server_seed + round_number)` committed prior to round start.

### 5.2 Crush (`crush`)
- **Cycle**: `BETTING_OPEN` (10s) -> `FLYING` / Multiplier curve $M(t) = e^{k \cdot t}$ -> `CRASHED` -> `SETTLED`
- **Cashout**: Client posts `/cashout` while flying; payout = $amount \times M_{current}$. Server validates timestamp against authoritative crash point.

### 5.3 Dragon Tiger (`dragon_tiger`)
- **Cycle**: `BETTING_OPEN` (15s) -> `BETTING_CLOSED` (2s) -> `RESULT` (Deal 2 cards) -> `SETTLED`
- **Rules**: Dragon Card Value vs Tiger Card Value (Ace=1, King=13).
  - Dragon > Tiger -> Dragon wins (Pays 1.95x / 2.0x)
  - Tiger > Dragon -> Tiger wins (Pays 1.95x / 2.0x)
  - Dragon == Tiger -> Tie wins (Pays 8.0x or 9.0x)

---

## 6. Migration Risks & Mitigations

1. **Financial Integrity & Double Spending**:
   - *Mitigation*: Maintain identical database transaction boundaries (`SELECT ... FOR UPDATE` on wallets table and unique `idempotency_key` on `wallet_ledger`).
2. **Real-time Game Timing & Desync**:
   - *Mitigation*: Kotlin Coroutines channels + WebSocket server-authoritative timestamps (`startedAt`, `durationMs`, `serverTime`).
3. **Provably Fair Cryptographic Verification**:
   - *Mitigation*: Ensure SHA-256 HMAC and seed generation algorithms produce identical deterministic outcomes across Kotlin backend and client verifiers.
4. **UI Fidelity with Jetpack Compose**:
   - *Mitigation*: Direct translation of custom color tokens, linear gradient brushes, animations, and composable layout hierarchies to match the Flutter design 1:1.
