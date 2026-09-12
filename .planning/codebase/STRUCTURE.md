# Directory Layout & Structure

**Analysis Date:** 2026-09-12

```text
999xgame-kotlin/
├── android/app/               # Native Android Compose App
│   └── src/main/kotlin/com/ingames/app/
│       ├── core/
│       ├── navigation/
│       ├── ui/
│       ├── home/
│       ├── share/
│       ├── wallet/
│       ├── profile/
│       ├── games/
│       └── auth/
│
├── backend/                   # Ktor JVM Backend Server
│   └── src/main/kotlin/com/ingames/
│       ├── Application.kt
│       ├── auth/
│       ├── users/
│       ├── wallet/
│       ├── deposits/
│       ├── withdrawals/
│       ├── transactions/
│       ├── realtime/
│       └── redis/
│
├── games/                     # Multi-Game Kotlin Library
│   ├── assets/                # Posters & visual assets for 8 games
│   └── src/main/kotlin/com/ingames/games/
│       ├── core/              # Game framework contracts
│       ├── common/            # Provably fair seeds, Money, Calculators
│       └── games/             # 8 game submodules (coinflip, dice, keno, etc.)
│
├── admin/                     # Admin Management Module
│   └── src/main/kotlin/com/ingames/admin/
│
├── database/                  # PostgreSQL DDL, Seeds, Migrations, Views
├── docs/                      # Technical Documentation
└── deployment/                # Docker & Environment configurations
```

<!-- refreshed: 2026-09-12 -->
