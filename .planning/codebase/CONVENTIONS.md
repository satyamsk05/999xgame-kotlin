# Coding Conventions & Patterns

**Analysis Date: 2026-09-12**

## Kotlin & Android Style Guide
- **Naming Conventions**:
  - Packages: `com.ingames.[module].[feature]` (all lowercase, no underscores).
  - Classes & Interfaces: `PascalCase` (e.g. `CoinFlipEngine`, `WithdrawalService`).
  - Functions & Properties: `camelCase` (e.g. `validateBet`, `processRound`).
  - Constants & Enums: `SCREAMING_SNAKE_CASE` (e.g. `GAME_ID`, `ROUND_PHASE`).
- **Null Safety & Immutability**:
  - Prefer immutable `val` properties for data classes.
  - Avoid nullable primitives where defaults can be safely assigned.

## Financial Ledger Enforcement Rule
- **Strict Rule**: No balance field in the system may be modified directly without generating a corresponding `Transaction` entry.
- **Transaction Types**: `DEPOSIT`, `WITHDRAWAL`, `BET`, `WIN`, `REFUND`, `BONUS`, `REFERRAL`, `REVERSAL`.
- Every wallet movement must update `balanceAfter` deterministically based on previous ledger state.

## Error Handling Pattern
- Use domain-specific sealed exception hierarchies inheriting from `GameException`:
  - `InvalidBetException`
  - `RoundClosedException`
  - `InsufficientBalanceException`
  - `GameMaintenanceException`

<!-- refreshed: 2026-09-12 -->
