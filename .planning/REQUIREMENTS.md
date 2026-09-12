# Requirements Specifications (`999xgame-kotlin` — Milestone v1.1)

## User Stories & Acceptance Criteria

### 1. UI Loading & Skeleton States
- **UIPOLISH-01**: As a user, I want smooth animated shimmer loading placeholders on the Home screen while game data is loading from backend REST APIs.
  - *Acceptance Criteria*: `HomeScreen` displays shimmer skeleton cards until `ApiClient.fetchGames()` completes.

### 2. Live WebSocket Resilience & User Notifications
- **UIPOLISH-02**: As a user, I want an automatic reconnection banner and toast when the WebSocket connection drops during backgrounding or network changes.
  - *Acceptance Criteria*: `RealtimeClient` detects disconnect events, triggers exponential backoff retry, and displays an unobtrusive reconnection bar.

### 3. Tactile Haptics & Feedback
- **UIPOLISH-03**: As a player, I want tactile haptic feedback when placing bets or executing cashouts.
  - *Acceptance Criteria*: Jetpack Compose bet buttons emit `HapticFeedbackType.LongPress` or `TextHandleMove` feedback on click.

### 4. Live Round & Payout Overlays
- **UIPOLISH-04**: As a player, I want animated win celebration overlays and live countdown tickers in active game screens.
  - *Acceptance Criteria*: Winning round WebSocket messages trigger overlay celebration animations on Seven Up Down, Crush, and Dragon Tiger screens.

---

## Requirement Traceability

| Requirement ID | Assigned Phase | Status |
|---|:---:|:---:|
| **UIPOLISH-01** | Phase 7 | `[ ]` |
| **UIPOLISH-02** | Phase 7 | `[ ]` |
| **UIPOLISH-03** | Phase 7 | `[ ]` |
| **UIPOLISH-04** | Phase 7 | `[ ]` |

---

## Definition of Done
- `./gradlew check` passes cleanly across `:android:app` and backend modules.
- Android unit tests verify `RealtimeClient` reconnection flow and `HomeScreen` UI composables.
