# Research Phase 08: Advanced Admin Analytics & Risk Scoring

## Overview
Phase 08 introduces automated fraud detection and administrative analytics for platform governance.

## Component Mapping
1. **Risk Scoring Engine (`RiskScoringService.kt`)**:
   - Calculates a risk score `0..100` based on:
     - Rapid deposit frequency (e.g. >3 deposits in 1 hour -> +30 pts)
     - High withdrawal/deposit ratio (>90% withdrawal without betting -> +40 pts)
     - Multiple accounts sharing phone prefix/IP -> +30 pts
   - Risk Tiers: `LOW` (0-29), `MEDIUM` (30-59), `HIGH` (60-79), `CRITICAL` (80-100).

2. **Automated Risk Lock (`FinancialService.kt` / `WithdrawalService.kt`)**:
   - When user submits withdrawal request, `FinancialService.requestWithdrawal` checks `RiskScoringService.getRiskScore(userId)`.
   - If risk score >= 80 (`CRITICAL`), sets withdrawal `status = "RISK_LOCKED"`.

3. **Analytics Engine (`AdminAnalyticsService.kt`)**:
   - Computes platform metrics: Gross Gaming Revenue (`GGR`), Net Gaming Revenue (`NGR`), Total Deposits (`totalDepositPaise`), Total Withdrawals (`totalWithdrawalPaise`), and Active Players.
   - Endpoint: `GET /api/admin/analytics/summary`.

4. **Risk Action Admin Controller (`AdminRiskController.kt`)**:
   - Endpoint: `POST /api/admin/users/risk-action` for manual risk score override, account suspension/unban, and logging to `AuditLogService`.

## Verification Commands
- `./gradlew :backend:test --tests "com.ingames.admin.AdminRiskAnalyticsTest"`
- `./gradlew test`
