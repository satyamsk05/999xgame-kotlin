# Requirements Specifications (`999xgame-kotlin` — Milestone v1.3)

## User Stories & Acceptance Criteria

### 1. VIP Tier Progression & Reward Engine
- **VIP-01**: As a player, I want my VIP level to automatically advance from Bronze to Diamond based on total wager volume so that I unlock higher cashback percentages.
  - *Acceptance Criteria*: `VipService.getVipLevel(totalWagerPaise)` evaluates levels (`BRONZE`, `SILVER`, `GOLD`, `PLATINUM`, `DIAMOND`).
- **VIP-02**: As a player, I want weekly cashback automatically calculated on net losses and credited to my bonus balance.
  - *Acceptance Criteria*: `VipService.calculateWeeklyCashback(userId)` credits calculated bonus paise to the user's wallet.

### 2. User & Admin VIP REST Endpoints
- **VIP-03**: As a player, I want to view my VIP tier, progress percentage, and claimable cashback rewards in the app.
  - *Acceptance Criteria*: `GET /api/user/vip-status` and `POST /api/user/claim-cashback` return updated VIP status and balance.
- **VIP-04**: As an admin, I want to override user VIP tiers or adjust cashback rate multipliers for promotions.
  - *Acceptance Criteria*: `POST /api/admin/users/vip-override` sets user VIP tier and logs the action in `audit_logs`.

---

## Requirement Traceability

| Requirement ID | Assigned Phase | Status |
|---|:---:|:---:|
| **VIP-01** | Phase 9 | `[ ]` |
| **VIP-02** | Phase 9 | `[ ]` |
| **VIP-03** | Phase 9 | `[ ]` |
| **VIP-04** | Phase 9 | `[ ]` |

---

## Definition of Done
- VIP Unit & Integration tests pass (`./gradlew :backend:test`).
- `./gradlew check` succeeds cleanly across all 5 workspace modules.
