# 999xgame Flutter Removal Plan

## 1. Deprecation Strategy & Milestones

To prevent regression or accidental service disruption, removal of the legacy Flutter and Node.js artifacts follows a three-stage gating model:

```
[Phase A: Side-by-Side Coexistence]
  ├── Verify Kotlin Backend against legacy integration test suites
  ├── Validate Android Jetpack Compose UI visual fidelity
  └── Check WebSocket real-time event parity

[Phase B: Production Cutover]
  ├── Route API traffic to Ktor Backend
  ├── Publish Native Android APK build
  └── Confirm zero ledger drift via ReconciliationService

[Phase C: Safe Removal & Cleanup]
  ├── Archive legacy Flutter / Dart code
  ├── Decommission Node.js scripts
  └── Clean repository root
```

---

## 2. File Inventory & Action Classification

### SAFE TO REMOVE (Post-Cutover)
- `lib/` (Entire Flutter Dart codebase)
- `pubspec.yaml`, `pubspec.lock`, `analysis_options.yaml`
- `ios/`, `linux/`, `macos/`, `windows/`, `web/` (Flutter desktop/web runner scaffolding)
- `backend/server.js`, `backend/src/**/*.js` (Node.js runtime scripts once Ktor backend is validated)

### KEEP PERMANENTLY (Shared Assets & Configurations)
- `Assets/` (Brand logos, game banners, chip SVGs, avatar icons)
- `backend/public/` (HTML5 games: `seven_up_down`, `fruit_slice` assets)
- `database/migrations/*.sql` (Canonical SQL migration history)
- `docs/` (All architecture specifications and runbooks)
