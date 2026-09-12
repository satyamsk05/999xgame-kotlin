# Phase 3: Technical Research (Base Game Engines Implementation)

**Analysis Date: 2026-09-12**

## Technical Context & Game Engine Architectures
The 8 base games in `:games` require server-authoritative, provably fair engine algorithms:

### 1. Provably Fair Seed Engine (`SeedProvider`, `ResultVerifier`)
- **Server Seed**: 64-character cryptographic random hex string generated via `SecureRandom`.
- **Client Seed**: User-provided or random seed string.
- **Nonce**: Incremental round / wager counter.
- **Combined HMAC-SHA256**: `HMAC_SHA256(serverSeed, "$clientSeed:$nonce")`.
- Outcome derived deterministically from HMAC hash bytes.

### 2. Game Engine Rules (8 Base Games)
1. **Coin Flip** (`coinflip`):
   - Options: HEADS / TAILS (payout multiplier 1.96x).
   - HMAC byte % 2 -> 0: HEADS, 1: TAILS.
2. **Classic Dice** (`classicdice`):
   - Roll range 0.00 to 99.99. Target condition (Over / Under).
   - Multiplier formula = `(100 - houseEdge) / winningProbability`.
3. **Keno** (`keno`):
   - Draw 10 numbers from 1 to 40 using HMAC SHA-256 seed shuffle.
   - Payout matrix based on number of hits (0 to 10 matches).
4. **Mines** (`mines`):
   - 5x5 grid (25 tiles), 1 to 24 mines.
   - Fisher-Yates shuffle seeded with HMAC SHA-256 for mine placement.
5. **Perya Color Game** (`peryacolor`):
   - 3 color dice rolled simultaneously (RED, BLUE, YELLOW, GREEN, PINK, WHITE).
   - Single match = 2x, double match = 3x, triple match = 4x.
6. **Ring of Fortune** (`ringoffortune`):
   - 54-segment wheel (1x, 2x, 5x, 10x, 20x, 45x multipliers).
7. **Double** (`double`):
   - 15 slots: 1 to 7 (RED 2x), 8 to 14 (BLACK 2x), 0 (WHITE 14x).
8. **Limbo** (`limbo`):
   - Target multiplier (1.01x to 1,000,000x).
   - Result multiplier derived from SHA-256: `(100 - houseEdge) / floatOutcome`.

<!-- refreshed: 2026-09-12 -->
