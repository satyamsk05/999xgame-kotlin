# Phase 2 Context: Financial Ledger & Wallet Subsystem

## Goal
Implement a thread-safe, double-entry financial ledger and wallet balance system in Ktor backend, complete with deposit order creation, withdrawal queues, and audit trail APIs.

## User Requirements & Design Decisions
- `FinancialService` must handle atomic deductions (deposit first, then winnings, then bonus).
- All wallet operations must enforce `idempotency_key` deduplication.
- Deposit initiation returns QR Code URL / UPI payment instructions (`initiateDeposit`).
- Withdrawal requests validate minimum withdrawal threshold (₹100) and reserve winnings balance.
- REST endpoints:
  - `GET /api/wallet/balance`
  - `GET /api/wallet/transactions`
  - `POST /api/wallet/deposit/initiate`
  - `POST /api/wallet/deposit/utr`
  - `POST /api/wallet/withdrawal/request`
