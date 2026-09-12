# Testing Strategy & Structure

**Analysis Date: 2026-09-12**

## Test Frameworks & Configurations
- **Framework**: JUnit 5 (Jupiter) with `kotlinx.coroutines.test` for asynchronous coroutine validation.
- **Execution**: Configured in Gradle DSL across `:shared`, `:games`, `:backend`, and `:admin` (`tasks.test { useJUnitPlatform() }`).

## Test Suite Layout (`tests/`)
- `tests/unit/`:
  - `wallet/` - Unit tests for balance math and transaction ledger generation.
  - `payments/` - Unit tests for deposit callback verification.
  - `withdrawals/` - Unit tests for payout approval validation.
  - `games/` - Unit tests for 8 base game engine placeholders.
  - `users/` - Unit tests for user profile validation.
- `tests/integration/`:
  - Integration tests for authentication flows, wallet transactions, and REST APIs.
- `tests/e2e/`:
  - End-to-end scenarios covering complete deposit-to-gameplay-to-withdrawal user journeys.

<!-- refreshed: 2026-09-12 -->
