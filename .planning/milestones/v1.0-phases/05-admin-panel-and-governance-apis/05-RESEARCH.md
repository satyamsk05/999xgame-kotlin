# Research Phase 05: Admin Panel & Governance APIs

## Overview
Phase 05 establishes role-based governance endpoints, withdrawal/deposit approval workflows, user account moderation, game stake configuration, and platform analytics audit logs.

## Core Component Mapping
1. **Admin Models & Role Access**:
   - `AdminUser`: `id`, `username`, `passwordHash`, `role` (`SUPER_ADMIN`, `FINANCE_ADMIN`, `GAME_ADMIN`, `SUPPORT_AGENT`).
   - `AdminJwtService`: Issue and verify admin JWT claims separately from player tokens.
2. **Financial Approvals Workflow**:
   - Withdrawal request review, ledger credit/debit adjustments, and audit log generation.
   - UTR deposit verification and deposit confirmation.
3. **User Governance**:
   - Block/unblock accounts with `blocked_reason`, inspect transaction audit history.
4. **Game Management & Platform Analytics**:
   - Stake limit adjustment (`minStake`, `maxStake`) per game and platform revenue summaries.

## Database Tables Utilized
- `admins`: Admin user accounts and hashed passwords.
- `audit_logs`: Admin action tracking (`admin_id`, `action`, `target`, `details`).
- `withdrawals`, `deposits`, `wallets`, `wallet_ledger`, `users`, `games`.
