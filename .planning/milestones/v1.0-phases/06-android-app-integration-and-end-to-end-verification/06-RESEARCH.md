# Research Phase 06: Android App Integration & End-to-End Verification

## Overview
Phase 06 completes the client-side WebSocket live stream binding in `android/app` and performs an end-to-end integration test suite verifying the complete player lifecycle (Auth -> Deposit -> Gameplay -> Realtime WS -> Admin Governance -> Withdrawal).

## Core Component Mapping
1. **Android App WebSocket Client (`RealtimeClient.kt`)**:
   - Connects Ktor WebSocket client to `/ws/game/{gameId}` and `/ws/user/{userId}`.
   - Exposes `StateFlow<WsMessage>` for Jetpack Compose UI screens (Seven Up Down, Crush, Dragon Tiger, Home Screen Header balance).
2. **End-to-End Verification Test (`E2EIntegrationTest.kt`)**:
   - Simulates complete money and gameplay flows across `backend`, `games`, `admin`, and `shared`.
   - Validates ledger invariants (`totalPaise = depositPaise + winningsPaise + bonusPaise`).

## Verification Commands
- `./gradlew :android:app:test`
- `./gradlew :backend:test --tests "com.ingames.e2e.E2EIntegrationTest"`
- `./gradlew test`
