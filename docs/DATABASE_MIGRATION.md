# 999xgame Database Migration Specification

## 1. Schema Architecture & Integrity Principles

1. **All Financial Balances Stored as Integer Paise (₹1.00 = 100 paise)**:
   - Eliminates floating-point rounding errors across all calculations.
2. **ACID Transactions**:
   - Every balance modification performs `SELECT ... FOR UPDATE` on `wallets` within a strict transaction.
3. **Immutable Double-Entry Ledger (`wallet_ledger`)**:
   - Every credit and debit creates an immutable record tied to `reference_type` and `reference_id`.
4. **Idempotency**:
   - Unique constraints on `(idempotency_key)` prevent double-charging or double-crediting on retries.

---

## 2. SQL Migrations

### `001_initial.sql`
Defines core tables:
- `users`: ID, phone, display name, avatar, role, is_blocked, created_at.
- `wallets`: ID, user_id (UNIQUE), deposit_balance, winnings_balance, bonus_balance, reserved_balance, updated_at.
- `wallet_ledger`: ID, user_id, type, amount, balance_after, reference_type, reference_id, idempotency_key (UNIQUE), description, created_at.
- `deposits`: ID, deposit_id (UNIQUE), user_id, amount, status, payment_method, utr (UNIQUE where not null), created_at.
- `withdrawals`: ID, withdrawal_id (UNIQUE), user_id, gross_amount, tds_deducted, net_amount, payout_method, payout_details, status, created_at.
- `game_rounds`: ID, game_id, round_number, status, server_seed, server_seed_hash, result_data, started_at, settled_at.
- `bets`: ID, round_id, user_id, game_id, bet_type, amount, status, idempotency_key (UNIQUE), created_at.
- `settlements`: ID, round_id, bet_id (UNIQUE), user_id, payout_amount, multiplier, created_at.
- `promotions`: ID, tag, title, subtitle, button_text, image_url, target_screen, status, display_order.
- `admins`: ID, username (UNIQUE), password_hash, role, is_active, created_at.
- `audit_logs`: ID, user_id, admin_id, action, details, ip_address, created_at.

### `002_wallet_integrity.sql`
- Ensures `deposit_balance >= 0`, `winnings_balance >= 0`, `reserved_balance >= 0`.
- Adds check constraints to prevent negative balances at the database engine level.

### `003_admin_auth.sql`
- Admin roles (`SUPER_ADMIN`, `SUPPORT`, `FINANCE`), permissions, and session tracking.

### `004_game_integrity.sql`
- Provably fair columns: `server_seed_hash VARCHAR(128)`, `crash_point NUMERIC(6,2)`.
- Unique constraint `uq_game_round_number` per `(game_id, round_number)`.

### `005_indexes.sql`
- Performance indexes on frequently queried foreign keys, time-series filters, and status flags.
