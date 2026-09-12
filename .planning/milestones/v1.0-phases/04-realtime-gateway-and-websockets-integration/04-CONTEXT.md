# Phase 4 Context: Realtime Gateway & WebSockets Integration

## Goal
Implement a scalable, non-blocking WebSocket gateway in Ktor backend (`/ws/game/{gameId}` and `/ws/user/{userId}`) for live round broadcasts, timer synchronization, and balance updates.

## User Requirements & Design Decisions
- `WebSocketManager` handles channel subscriptions and thread-safe session tracking.
- Broadcast messages must be formatted as structured JSON (`RealtimeMessage`).
- WebSocket routes must authenticate client connections via JWT query params or headers.
