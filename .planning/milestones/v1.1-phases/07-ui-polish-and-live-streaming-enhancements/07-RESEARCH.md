# Research Phase 07: UI Polish & Live Streaming Enhancements

## Overview
Phase 07 enhances the user experience of the Android Jetpack Compose app with loading shimmer animations, robust WebSocket reconnection state handling, tactile haptics, and win celebration visual overlays.

## Core Components
1. **Compose Shimmer Effect**:
   - Animated linear gradient brush applied to background Modifier while fetching games list.
2. **WebSocket Reconnection & Backoff**:
   - Exponential backoff retry loop in `RealtimeClient.kt` (`1s`, `2s`, `4s`, max `10s`).
   - `connectionState: StateFlow<ConnectionState>` exposed to Compose top app bar.
3. **Haptic Feedback**:
   - `LocalHapticFeedback.current.performHapticFeedback(HapticFeedbackType.LongPress)` triggered on bet buttons.
4. **Win Overlays & Tickers**:
   - Celebration dialog/overlay on game screens when receiving `event == "win"` message.

## Verification Strategy
- Android unit tests for `RealtimeClient` state machine.
- Compose preview & build verification via `./gradlew :android:app:test`.
