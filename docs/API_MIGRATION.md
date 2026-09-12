# 999xgame API Migration Specification

## 1. Response Contract & Error Codes

All HTTP APIs adhere to the unified standard envelope:

### Success Response
```json
{
  "status": "success",
  "data": { ... }
}
```

### Error Response
```json
{
  "status": "error",
  "code": "INSUFFICIENT_FUNDS",
  "message": "Your wallet balance is insufficient for this wager.",
  "requestId": "req_123456789"
}
```

---

## 2. Comprehensive Endpoint Mapping

| Old Node.js Route | New Kotlin Ktor Route | Method | Auth | Purpose |
|---|---|---|---|---|
| `/health` | `/health` | GET | None | Health check probe |
| `/ready` | `/ready` | GET | None | Database connection readiness probe |
| `/api/config` | `/api/config` | GET | None | App configuration & online user count |
| `/api/online-ticker` | `/api/online-ticker` | GET | None | Live online counter & ticker avatars |
| `/api/banners` | `/api/banners` | GET | None | Active promotional banners |
| `/api/auth/otp/send` | `/api/auth/otp/send` | POST | None | Send OTP to mobile phone |
| `/api/auth/otp/verify` | `/api/auth/otp/verify` | POST | None | Verify OTP, return JWT & profile |
| `/api/auth/token/refresh` | `/api/auth/token/refresh` | POST | Refresh Token | Refresh expired access JWT |
| `/api/auth/logout` | `/api/auth/logout` | POST | Bearer JWT | Revoke session |
| `/api/user/profile` | `/api/user/profile` | GET | Bearer JWT | Fetch user profile & balances |
| `/api/user/avatar` | `/api/user/avatar` | PUT | Bearer JWT | Update user avatar |
| `/api/user/stats` | `/api/user/stats` | GET | Bearer JWT | User game stats & referral stats |
| `/api/wallet/balance` | `/api/wallet/balance` | GET | Bearer JWT | Detailed wallet balances |
| `/api/wallet/transactions`| `/api/wallet/transactions`| GET | Bearer JWT | Paginated ledger transactions |
| `/api/deposits/initiate` | `/api/deposits/initiate` | POST | Bearer JWT | Create deposit order / UPI intent |
| `/api/deposits/submit-utr`| `/api/deposits/submit-utr`| POST | Bearer JWT | Submit 12-digit UTR for review |
| `/api/deposits/history` | `/api/deposits/history` | GET | Bearer JWT | User deposit history |
| `/api/withdrawals/request`| `/api/withdrawals/request`| POST | Bearer JWT | Request payout via UPI / Bank |
| `/api/withdrawals/history`| `/api/withdrawals/history`| GET | Bearer JWT | User withdrawal history |
| `/api/games` | `/api/games` | GET | Optional | List games & player counts |
| `/api/games/seven-up-down/state` | `/api/games/seven-up-down/state` | GET | Optional | Seven Up Down round state |
| `/api/games/seven-up-down/bet` | `/api/games/seven-up-down/bet` | POST | Bearer JWT | Place Seven Up Down wager |
| `/api/games/crush/state` | `/api/games/crush/state` | GET | Optional | Crush round state & seed |
| `/api/games/crush/bet` | `/api/games/crush/bet` | POST | Bearer JWT | Place Crush wager |
| `/api/games/crush/cashout` | `/api/games/crush/cashout` | POST | Bearer JWT | Cashout Crush wager |
| `/api/games/dragon-tiger/state` | `/api/games/dragon-tiger/state` | GET | Optional | Dragon Tiger round state |
| `/api/games/dragon-tiger/bet` | `/api/games/dragon-tiger/bet` | POST | Bearer JWT | Place Dragon Tiger wager |
| `/api/admin/auth/login` | `/api/admin/auth/login` | POST | None | Admin username/password login |
| `/api/admin/stats/overview`| `/api/admin/stats/overview`| GET | Admin JWT | Admin platform metrics |
| `/api/admin/deposits` | `/api/admin/deposits` | GET | Admin JWT | List pending/all deposits |
| `/api/admin/deposits/:id/approve` | `/api/admin/deposits/{id}/approve` | POST | Admin JWT | Credit deposit to user |
| `/api/admin/deposits/:id/reject` | `/api/admin/deposits/{id}/reject` | POST | Admin JWT | Reject invalid deposit |
| `/api/admin/withdrawals` | `/api/admin/withdrawals` | GET | Admin JWT | List pending withdrawals |
| `/api/admin/withdrawals/:id/approve` | `/api/admin/withdrawals/{id}/approve` | POST | Admin JWT | Approve & finalize payout |
| `/api/admin/withdrawals/:id/reject` | `/api/admin/withdrawals/{id}/reject` | POST | Admin JWT | Reject & refund reserved |

---

## 3. Real-Time WebSocket Protocol

- Endpoint: `ws://<host>:<port>/ws`
- Handshake Query / Auth Header: `?token=<JWT>`

### Client -> Server Actions
- `{"action": "subscribe", "room": "game:seven_up_down"}`
- `{"action": "subscribe", "room": "game:crush"}`
- `{"action": "subscribe", "room": "game:dragon_tiger"}`
- `{"action": "ping"}`

### Server -> Client Broadcasts
- `{"event": "round_start", "gameId": "seven_up_down", "roundId": "...", "serverSeedHash": "..."}`
- `{"event": "betting_status", "gameId": "seven_up_down", "status": "BETTING_OPEN", "remainingMs": 14500}`
- `{"event": "dice_result", "gameId": "seven_up_down", "dice": [3, 4], "total": 7, "outcome": "SEVEN"}`
- `{"event": "multiplier_tick", "gameId": "crush", "multiplier": 1.45, "elapsedMs": 2300}`
- `{"event": "crash", "gameId": "crush", "crashPoint": 3.82}`
- `{"event": "wallet_update", "userId": "...", "balances": { "deposit": 500, "winnings": 750, "total": 1250 }}`
