# Phase 4: Technical Research (Realtime Gateway & WebSockets Integration)

**Analysis Date: 2026-09-12**

## Technical Context & Architectural Guidelines
1. **Ktor WebSockets Plugin (`ktor-server-websockets`)**:
   - High-performance non-blocking WebSocket engine.
   - Pipelining & ping/pong heartbeat interval: 15 seconds.
2. **WebSocket Events Structure**:
   - `SUBSCRIBE_GAME`: Client joins a game channel (`coin_flip`, `classic_dice`, `mines`, etc.).
   - `ROUND_TIMER`: Server broadcasts round countdown seconds (`15s` -> `0s`).
   - `ROUND_RESULT`: Server broadcasts provably fair round outcome payload.
   - `BALANCE_UPDATE`: Realtime user balance notification upon win/loss settlement.
   - `ONLINE_COUNT`: Active online users count broadcast.
3. **Session & Connection Management**:
   - Thread-safe `ConcurrentHashMap` tracking connected `DefaultWebSocketServerSession` instances by `userId` and channel `gameId`.

<!-- refreshed: 2026-09-12 -->
