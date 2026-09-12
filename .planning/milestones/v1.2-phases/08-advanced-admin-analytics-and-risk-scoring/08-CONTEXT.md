# Phase 08 Context: Advanced Admin Analytics & Risk Scoring

## User Stories & Acceptance Criteria Addressed
- **RISK-01**: User Risk Scoring Service evaluating deposit velocity, withdrawal frequency, and multi-account signals (`LOW`, `MEDIUM`, `HIGH`, `CRITICAL`).
- **RISK-02**: Automated risk-flagged withdrawal locking (`RISK_LOCKED`) preventing instant payouts for high-risk users.
- **ANALYTICS-01**: `/api/admin/analytics/summary` REST endpoints returning GGR, NGR, daily active users, and net deposit volume.
- **ANALYTICS-02**: Risk Management Admin Controller for user risk score overrides, manual user bans, and audit trail logging.

## Dependencies
- Phase 01: Database & Persistence (`users`, `wallets`, `deposits`, `withdrawals`).
- Phase 02: Financial Ledger & Wallet Subsystem.
- Phase 05: Admin Panel & Governance APIs (`AuditLogService`, `AdminAuthService`).
