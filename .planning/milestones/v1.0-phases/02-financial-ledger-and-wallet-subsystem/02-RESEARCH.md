# Phase 2: Technical Research (Financial Ledger & Wallet Subsystem)

**Analysis Date: 2026-09-12**

## Technical Context & Architectural Rules
1. **Multi-Balance Wallet Architecture**:
   - `deposit_balance`: Money deposited via payment gateways (Paise / Double precision).
   - `winnings_balance`: Funds won from game rounds; eligible for withdrawal.
   - `bonus_balance`: Promotional / referral rewards; locked until wagering requirement met.
   - `reserved_balance`: Funds held during active withdrawal requests.
   - `total_balance` = `deposit_balance` + `winnings_balance` + `bonus_balance`.
2. **Strict Financial Ledger Rule**:
   - Wallet balances are NEVER updated in isolation.
   - Every balance modification must generate an immutable record in `wallet_ledger` / `transactions`.
   - Transaction Types: `DEPOSIT`, `WITHDRAWAL`, `BET`, `WIN`, `REFUND`, `BONUS`, `REFERRAL`, `REVERSAL`, `ADJUSTMENT`.
   - Every operation requires an `idempotency_key` to prevent double-spending or duplicate credit.
3. **Withdrawal Queue & Admin Approval Flow**:
   - Request created → `PENDING` status → Funds moved from `winnings_balance` to `reserved_balance`.
   - Admin approves → Status `APPROVED` → Process payout → Status `PAID` → Debit `reserved_balance`.
   - Admin rejects → Status `REJECTED` → Revert funds from `reserved_balance` back to `winnings_balance`.

<!-- refreshed: 2026-09-12 -->
