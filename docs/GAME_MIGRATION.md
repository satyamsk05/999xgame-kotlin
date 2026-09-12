# 999xgame Game Migration Specification

## 1. Game Engines Overview

| Game ID | Name | Type | Rendering / Platform | Provably Fair Seed | Payout Model |
|---|---|---|---|---|---|
| `seven_up_down` | 7 Up Down | 2-Dice Dice Table | Native Compose / HTML5 Webview | SHA-256 Commit-Reveal | 2-6 (2x), 7 (5x), 8-12 (2x) |
| `crush` | Crush / Crash | Real-time Multiplier Curve | Native Compose Canvas | SHA-256 HMAC Seed | $1.00\times \to \text{Crash Point}$ |
| `dragon_tiger` | Dragon Tiger | 2-Card Compare | Native Compose Canvas/UI | SHA-256 Commit-Reveal | Dragon (1.95x/2x), Tiger (1.95x/2x), Tie (8x) |
| `fruit_slice` | Fruit Slice | HTML5 Arcade | WebKit / WebView + JS Bridge | Skill / Score Payout | Entry Fee vs Prize Pool |

---

## 2. Server-Authoritative Logic & State Machines

### Seven Up Down (`seven_up_down`)
```
+----------------+      15s timer       +------------------+
|  BETTING_OPEN  | -------------------> |  BETTING_CLOSED  |
+----------------+                      +------------------+
                                                 | 2s buffer
                                                 v
+----------------+      3s pause        +------------------+
|    SETTLED     | <------------------- |      RESULT      |
+----------------+                      +------------------+
```

1. **Seed Generation**: `server_seed = SecureRandomHex(32)`
2. **Commit Hash**: `hash = SHA256(server_seed + ":" + round_number)` broadcast to clients during `BETTING_OPEN`.
3. **Outcome Generation**:
   - `val hashInt = BigInteger(sha256(server_seed), 16)`
   - `die1 = (hashInt % 6) + 1`
   - `die2 = ((hashInt / 6) % 6) + 1`
   - `sum = die1 + die2`
4. **Settlement**: Atomic query credits all matching wagers and creates ledger credits before transitioning to `SETTLED`.

---

### Crush (`crush`)
1. **Crash Point Generation**:
   - Uses industry standard cryptographic crash formula:
     $$\text{Multiplier} = \max\left(1.00, \left\lfloor \frac{0.99}{\text{random\_fraction}} \times 100 \right\rfloor / 100\right)$$
   - House edge of 1%. Approximately 1 in 100 rounds crash instantly at 1.00x.
2. **Tick Stream**:
   - Server streams multiplier $M(t) = 1.00 \times e^{0.06 \times t_{\text{sec}}}$ at 50ms intervals.
3. **Cashout Protocol**:
   - User initiates `/api/games/crush/cashout` or WebSocket `cashout` message.
   - Server validates receipt timestamp $< t_{\text{crash}}$.
   - Credits $amount \times M_{\text{cashout}}$ to user winnings balance immediately within a transaction.

---

### Dragon Tiger (`dragon_tiger`)
1. **Card Deck**: Standard 52 card deck without jokers.
2. **Outcome Generation**:
   - Two cards drawn from provably fair seeded shuffle:
     `Dragon Card` and `Tiger Card`.
   - Card Values: Ace = 1, 2 = 2, ..., Jack = 11, Queen = 12, King = 13.
3. **Winner**:
   - If Dragon > Tiger: `DRAGON` wins.
   - If Tiger > Dragon: `TIGER` wins.
   - If Dragon == Tiger: `TIE` wins.
