# External Integrations

**Analysis Date: 2026-09-12**

## Database & Storage Systems
- **PostgreSQL 15+**: Primary relational datastore handling user accounts, KYC records, wallet balances, immutable transaction ledgers, deposit orders, withdrawal approvals, game round outcomes, and audit logs.
- **Redis 7+**: Distributed memory store for user sessions, active WebSocket tokens, rate limiting counters, temporary round states, and worker leader election.

## Payment & Payout Providers
- **Deposit Webhooks**: Endpoint handlers located in `backend/src/main/kotlin/com/ingames/deposits/` supporting incoming payment gateway callbacks and order reconciliation.
- **Withdrawal Processing**: Approval workflow queues in `backend/src/main/kotlin/com/ingames/withdrawals/` supporting automated UPI/Bank payout requests and manual admin review triggers.

## Realtime Gateway
- **Ktor WebSockets**: High-throughput bidirectional event channel defined in `backend/src/main/kotlin/com/ingames/realtime/WebSocketManager.kt`.
- **Event Broadcasting**: Broadcasts live game round state changes, countdown timers, and wallet balance updates to connected Android app clients.

<!-- refreshed: 2026-09-12 -->
