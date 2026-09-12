
Tum abhi **UI discuss nahi kar rahe**. Pehle ek **complete production project structure** banana hai jisme:
@game logo
* Android app = Kotlin
* Games = Kotlin game module/library
* Backend = Kotlin/Ktor
* PostgreSQL DB
* Redis
* WebSocket/realtime
* Admin Panel = full control
* Deposit
* Withdrawal
* Wallet
* Transactions
* Users
* KYC/payout details
* Offers
* Referral
* Notifications
* Game management
* Game rounds/results
* Bets
* Ledger
* Audit logs
* Security
* Aur **har game ka complete folder structure**, lekin **abhi game ka code nahi likhna**

Aur tumhare diye hue posters ke hisaab se abhi visual game catalog mein ye **8 games** hain:

1. **Coin Flip**
2. **Classic Dice**
3. **Keno**
4. **Mines**
5. **Perya Color Game**
6. **Ring of Fortune**
7. **Double**
8. **Limbo**

Baad mein tum base games ki final quantity/name doge, to structure scalable rahega.

---

# Sabse pehle current repositories ka important point

Maine tumhara Kotlin repo check kiya. Current `999xgame-kotlin` already **3 modules** use kar raha hai:

```text
999xgame-kotlin
├── shared
├── backend
└── android
    └── app
```

`settings.gradle.kts` mein exactly `:shared`, `:backend` aur `:android:app` included hain.

Backend already Kotlin/JVM + Ktor + PostgreSQL + HikariCP + JWT/Bcrypt + WebSockets architecture use kar raha hai.

Aur Flutter repo ka existing backend Node.js + Express + PostgreSQL + Redis + Socket.IO based hai.

Us Flutter backend mein game manager/worker architecture bhi hai, jahan game engine aur scheduler separately registered hain.

**Lekin new Kotlin architecture mein hum current Flutter ke 7-Up/Down, Dragon Tiger, Crush ko base games nahi rakhenge.** Tumhare diye hue games ko game catalog mein rakhenge.

---

# FINAL PROJECT STRUCTURE

Main recommended structure ye rakhunga:

```text
999xgame-kotlin/
│
├── android/
│   └── app/
│
├── games/
│
├── shared/
│
├── backend/
│
├── admin/
│
├── database/
│
├── docs/
│
├── scripts/
│
├── deployment/
│
├── tests/
│
├── gradle/
│
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── gradlew
├── gradlew.bat
└── README.md
```

Yahan **`games` aur `admin` ko first-class modules** rakhenge.

---

# 1. ANDROID APP

```text
android/
└── app/
    └── src/
        └── main/
            ├── kotlin/com/ingames/
            │
            │── MainActivity.kt
            │
            ├── core/
            │   ├── App.kt
            │   ├── AppConfig.kt
            │   ├── Constants.kt
            │   ├── Environment.kt
            │   └── BuildConfig.kt
            │
            ├── navigation/
            │   ├── AppNavigator.kt
            │   ├── Routes.kt
            │   ├── NavGraph.kt
            │   └── NavigationState.kt
            │
            ├── ui/
            │   ├── theme/
            │   ├── components/
            │   ├── dialogs/
            │   ├── sheets/
            │   ├── animations/
            │   └── loading/
            │
            ├── home/
            │   ├── HomeScreen.kt
            │   ├── HomeViewModel.kt
            │   ├── HomeState.kt
            │   ├── HomeRepository.kt
            │   └── components/
            │
            ├── share/
            │   ├── ShareScreen.kt
            │   ├── ShareViewModel.kt
            │   ├── ReferralScreen.kt
            │   └── components/
            │
            ├── wallet/
            │   ├── WalletScreen.kt
            │   ├── WalletViewModel.kt
            │   ├── DepositScreen.kt
            │   ├── WithdrawalScreen.kt
            │   ├── TransactionsScreen.kt
            │   └── components/
            │
            ├── profile/
            │   ├── ProfileScreen.kt
            │   ├── ProfileViewModel.kt
            │   ├── AvatarScreen.kt
            │   ├── BankAccountScreen.kt
            │   ├── UpiScreen.kt
            │   ├── SettingsScreen.kt
            │   └── components/
            │
            ├── games/
            │   ├── GameLauncher.kt
            │   ├── GameRoute.kt
            │   ├── GameSession.kt
            │   ├── GameRegistry.kt
            │   └── GameWebSocket.kt
            │
            ├── auth/
            │   ├── LoginScreen.kt
            │   ├── OtpScreen.kt
            │   ├── RegisterScreen.kt
            │   ├── AuthViewModel.kt
            │   └── TokenManager.kt
            │
            ├── notifications/
            ├── support/
            ├── settings/
            └── data/
                ├── api/
                ├── repository/
                ├── local/
                └── models/
```

---

# 2. GAMES MODULE

Ye bahut important hai.

**Har game ko independent module/feature banana hai.**

```text
games/
└── src/
    └── main/
        └── kotlin/com/ingames/games/
```

Common game framework:

```text
games/
├── core/
│   ├── Game.kt
│   ├── GameEngine.kt
│   ├── GameSession.kt
│   ├── GameState.kt
│   ├── GameConfig.kt
│   ├── GameResult.kt
│   ├── GameRound.kt
│   ├── GameBet.kt
│   ├── GamePlayer.kt
│   ├── GameEvent.kt
│   ├── GameError.kt
│   ├── GameRegistry.kt
│   ├── GameScheduler.kt
│   ├── GameValidator.kt
│   └── GameSettlement.kt
│
├── common/
│   ├── Money.kt
│   ├── Currency.kt
│   ├── RandomProvider.kt
│   ├── SeedProvider.kt
│   ├── Timer.kt
│   ├── GameClock.kt
│   ├── ResultVerifier.kt
│   ├── BetValidator.kt
│   └── PayoutCalculator.kt
│
└── games/
```

---

# 3. HAR GAME KA COMPLETE STRUCTURE

Tumne kaha:

> "1 1 choti files bhi miss nhi honi jo ek game mein hoti hain"

Isliye har game ke liye same standard structure rakhenge.

Example:

```text
coin-flip/
├── CoinFlipGame.kt
├── CoinFlipEngine.kt
├── CoinFlipConfig.kt
├── CoinFlipState.kt
├── CoinFlipRound.kt
├── CoinFlipBet.kt
├── CoinFlipResult.kt
├── CoinFlipPlayer.kt
├── CoinFlipEvent.kt
├── CoinFlipValidator.kt
├── CoinFlipPayout.kt
├── CoinFlipSettlement.kt
├── CoinFlipRepository.kt
├── CoinFlipService.kt
├── CoinFlipScheduler.kt
├── CoinFlipWebSocket.kt
├── CoinFlipMapper.kt
├── CoinFlipSerializer.kt
├── CoinFlipError.kt
├── CoinFlipConstants.kt
├── CoinFlipMetrics.kt
├── CoinFlipAudit.kt
└── test/
    ├── CoinFlipEngineTest.kt
    ├── CoinFlipBetTest.kt
    ├── CoinFlipPayoutTest.kt
    ├── CoinFlipSettlementTest.kt
    └── CoinFlipIntegrationTest.kt
```

**Abhi in files mein game code nahi hoga.**

Sirf structure/contracts/placeholders rahenge.

---

# 4. CURRENT 8 GAMES

Isi standard structure ko:

```text
games/
└── games/
    │
    ├── coin-flip/
    │
    ├── classic-dice/
    │
    ├── keno/
    │
    ├── mines/
    │
    ├── perya-color/
    │
    ├── ring-of-fortune/
    │
    ├── double/
    │
    └── limbo/
```

par apply karenge.

### Posters

| Game             | Poster                               |
| ---------------- | ------------------------------------ |
| Coin Flip        | Tumhara uploaded Coin Flip poster    |
| Classic Dice     | Tumhara uploaded Classic Dice poster |
| Keno             | Tumhara uploaded Keno poster         |
| Mines            | Tumhara uploaded Mines poster        |
| Perya Color Game | Tumhara uploaded Perya poster        |
| Ring of Fortune  | Tumhara uploaded Ring poster         |
| Double           | Tumhara uploaded Double poster       |
| Limbo            | Tumhara uploaded Limbo poster        |

**Poster asset aur game implementation ko separate rakhenge.**

---

# 5. GAME ASSETS

Har game:

```text
games/assets/
├── coin_flip/
│   ├── poster.webp
│   ├── logo.webp
│   ├── icon.webp
│   ├── background.webp
│   ├── sounds/
│   ├── images/
│   ├── animations/
│   └── particles/
│
├── classic_dice/
├── keno/
├── mines/
├── perya_color/
├── ring_of_fortune/
├── double/
└── limbo/
```

Baad mein tum assets doge to **game-specific asset folder mein jayenge**.

---

# 6. BACKEND

Tumhare Flutter backend ke existing concepts ko preserve karte hue Kotlin/Ktor backend ko:

```text
backend/
└── src/main/kotlin/com/ingames/
```

mein divide karunga:

```text
backend/
├── Application.kt
│
├── config/
│   ├── AppConfig.kt
│   ├── DatabaseConfig.kt
│   ├── RedisConfig.kt
│   ├── JwtConfig.kt
│   ├── PaymentConfig.kt
│   └── CorsConfig.kt
│
├── server/
│   ├── Routing.kt
│   ├── WebSockets.kt
│   ├── Serialization.kt
│   ├── Authentication.kt
│   ├── StatusPages.kt
│   ├── Monitoring.kt
│   └── RateLimit.kt
│
├── auth/
├── users/
├── wallet/
├── deposits/
├── withdrawals/
├── transactions/
├── payments/
├── referrals/
├── notifications/
├── support/
├── kyc/
├── profile/
├── games/
├── bets/
├── rounds/
├── settlements/
├── leaderboard/
├── offers/
├── promotions/
├── cms/
├── admin/
├── audit/
├── security/
├── realtime/
└── database/
```

---

# 7. USER SYSTEM

```text
users/
├── User.kt
├── UserRepository.kt
├── UserService.kt
├── UserController.kt
├── UserValidator.kt
├── UserMapper.kt
├── UserStatus.kt
├── UserBlockService.kt
└── UserSessionService.kt
```

Admin control:

```text
Users
├── Search
├── View
├── Block
├── Unblock
├── Suspend
├── Wallet view
├── Transactions
├── Bets
├── Games played
├── Referral
├── KYC
├── Bank/UPI
├── Login history
└── Audit history
```

---

# 8. WALLET SYSTEM

Wallet ko sirf ek `balance` field nahi rakhenge.

```text
wallet/
├── Wallet.kt
├── WalletRepository.kt
├── WalletService.kt
├── WalletLedger.kt
├── WalletTransaction.kt
├── WalletBalance.kt
├── WalletLock.kt
├── WalletValidator.kt
├── WalletMapper.kt
└── WalletAudit.kt
```

Balances:

```text
wallet
├── deposit balance
├── winnings balance
├── bonus/reward balance
├── withdrawable balance
├── locked balance
└── total balance
```

---

# 9. DEPOSIT

```text
deposits/
├── Deposit.kt
├── DepositOrder.kt
├── DepositRepository.kt
├── DepositService.kt
├── DepositController.kt
├── DepositValidator.kt
├── DepositStatus.kt
├── PaymentCallback.kt
├── PaymentWebhook.kt
├── DepositReconciliation.kt
└── DepositAudit.kt
```

Admin:

```text
Admin → Deposits

├── All
├── Pending
├── Success
├── Failed
├── Cancelled
├── Search
├── Filter
├── Date
├── User
├── Amount
├── Payment ID
└── Reconciliation
```

---

# 10. WITHDRAWAL

Ye separate complete subsystem hoga:

```text
withdrawals/
├── Withdrawal.kt
├── WithdrawalRequest.kt
├── WithdrawalRepository.kt
├── WithdrawalService.kt
├── WithdrawalController.kt
├── WithdrawalValidator.kt
├── WithdrawalStatus.kt
├── WithdrawalQueue.kt
├── WithdrawalApproval.kt
├── WithdrawalRejection.kt
├── WithdrawalProcessor.kt
├── WithdrawalReconciliation.kt
└── WithdrawalAudit.kt
```

Admin panel:

```text
Withdrawals
├── Pending
├── Under Review
├── Approved
├── Processing
├── Paid
├── Rejected
├── Failed
├── Cancelled
└── Manual Review
```

Admin actions:

```text
View
Approve
Reject
Hold
Release
Mark Paid
View User
View KYC
View Bank/UPI
View Ledger
View History
```

---

# 11. TRANSACTION SYSTEM

Sabse important backend components mein se ek:

```text
transactions/
├── Transaction.kt
├── TransactionRepository.kt
├── TransactionService.kt
├── TransactionType.kt
├── TransactionStatus.kt
├── TransactionLedger.kt
├── TransactionReference.kt
├── TransactionMapper.kt
└── TransactionAudit.kt
```

Types:

```text
DEPOSIT
WITHDRAWAL
BET
WIN
REFUND
BONUS
REFERRAL
REVERSAL
ADJUSTMENT
FEE
```

**Balance ko directly random jagah update nahi karna.**

Har financial movement ledger/transaction ke through traceable hona chahiye.

---

# 12. GAME BACKEND

```text
games/
├── Game.kt
├── GameRepository.kt
├── GameService.kt
├── GameRegistry.kt
├── GameConfigService.kt
├── GameStatus.kt
├── GameCategory.kt
├── GameEngine.kt
├── GameWorker.kt
├── GameScheduler.kt
├── GameRoundService.kt
├── GameResultService.kt
└── games/
    ├── coin-flip/
    ├── classic-dice/
    ├── keno/
    ├── mines/
    ├── perya-color/
    ├── ring-of-fortune/
    ├── double/
    └── limbo/
```

---

# 13. GAME ROUND SYSTEM

Har game ka round independent hoga:

```text
rounds/
├── GameRound.kt
├── RoundRepository.kt
├── RoundService.kt
├── RoundScheduler.kt
├── RoundState.kt
├── RoundResult.kt
├── RoundSettlement.kt
├── RoundHistory.kt
└── RoundAudit.kt
```

Flow:

```text
ROUND CREATED
     ↓
BETTING OPEN
     ↓
BETTING CLOSED
     ↓
RESULT GENERATED
     ↓
RESULT VERIFIED
     ↓
BET SETTLEMENT
     ↓
WALLET UPDATE
     ↓
ROUND CLOSED
     ↓
HISTORY
```

---

# 14. BET SYSTEM

```text
bets/
├── Bet.kt
├── BetRepository.kt
├── BetService.kt
├── BetValidator.kt
├── BetStatus.kt
├── BetPlacement.kt
├── BetSettlement.kt
├── BetCancellation.kt
├── BetHistory.kt
└── BetAudit.kt
```

---

# 15. REALTIME

Flutter backend already Socket.IO based realtime architecture use karta hai, including online user count aur authenticated sockets.

Kotlin version mein:

```text
realtime/
├── WebSocketManager.kt
├── UserSocket.kt
├── GameSocket.kt
├── WalletSocket.kt
├── NotificationSocket.kt
├── OnlineUserService.kt
├── RealtimeEvent.kt
├── RealtimeMessage.kt
└── ConnectionManager.kt
```

---

# 16. REDIS

```text
redis/
├── RedisClient.kt
├── RedisKeys.kt
├── RedisCache.kt
├── SessionCache.kt
├── OnlineUsersCache.kt
├── GameStateCache.kt
├── RateLimitCache.kt
├── LockManager.kt
└── LeaderElection.kt
```

Use cases:

* online users
* sessions
* temporary game state
* distributed locks
* worker leadership
* rate limiting
* realtime state

---

# 17. DATABASE

Separate:

```text
database/
├── migrations/
├── seeds/
├── functions/
├── indexes/
├── views/
├── triggers/
└── schema/
```

---

# 18. DATABASE TABLES

Production structure roughly:

```text
users
user_profiles
user_sessions
user_devices
user_login_history

wallets
wallet_ledger
wallet_transactions

deposits
deposit_orders
payment_transactions
payment_webhooks

withdrawals
withdrawal_requests
withdrawal_attempts

bank_accounts
upi_accounts
payout_methods

games
game_configs
game_assets
game_rounds
game_results

bets
bet_items
bet_settlements

referrals
referral_rewards
referral_events

offers
promotions
bonuses
reward_rules

notifications
notification_templates

support_tickets
support_messages

kyc_records
kyc_documents

admin_users
admin_roles
admin_permissions
admin_sessions

audit_logs
security_events

app_settings
game_settings
payment_settings
withdrawal_settings
```

---

# 19. ADMIN PANEL

Ye **sirf ek dashboard screen nahi** hoga.

```text
admin/
├── dashboard/
├── authentication/
├── users/
├── games/
├── rounds/
├── bets/
├── wallets/
├── deposits/
├── withdrawals/
├── transactions/
├── payments/
├── referrals/
├── offers/
├── promotions/
├── bonuses/
├── KYC/
├── support/
├── notifications/
├── reports/
├── analytics/
├── CMS/
├── settings/
├── admins/
├── roles/
├── permissions/
├── audit/
├── security/
└── system/
```

---

# 20. ADMIN DASHBOARD

Dashboard mein:

```text
TODAY

Users
New Users
Active Users
Online Users

Money
Total Deposits
Total Withdrawals
Pending Withdrawals
Pending Deposits

Games
Total Bets
Total Bet Amount
Total Wins
Total Payout

System
Active Games
Live Rounds
Failed Payments
Failed Withdrawals
Server Health
Redis Health
Database Health
```

---

# 21. ADMIN GAME CONTROL

Sabse important:

```text
Admin
 ↓
Games
```

Har game:

```text
Coin Flip
├── Enabled / Disabled
├── LIVE / OFFLINE
├── Maintenance
├── Minimum Bet
├── Maximum Bet
├── Available Bet Options
├── Payout Rules
├── Game Timer
├── Round Timer
├── Display Order
├── Poster
├── Icon
├── Name
└── Configuration
```

**Lekin result manually manipulate karne wala unsafe/backdoor system nahi banayenge.** Game outcome generation auditable aur server-authoritative hona chahiye.

---

# 22. ADMIN WITHDRAWAL CONTROL

```text
Admin Panel
    ↓
Withdrawals
    ↓
Pending
    ↓
Request Details

User
Amount
Requested At
Payout Method
KYC Status
Previous Withdrawals
Wallet Ledger
Risk Flags
```

Then:

```text
Approve
Reject
Hold
Release
Process
Mark Paid
```

Har action:

```text
ADMIN
↓
ACTION
↓
TIMESTAMP
↓
OLD STATUS
↓
NEW STATUS
↓
REASON
↓
AUDIT LOG
```

---

# 23. ADMIN DEPOSIT CONTROL

Same:

```text
Deposits
├── Pending
├── Success
├── Failed
├── Reversed
├── Manual Review
├── Payment Provider
├── Amount
├── User
└── Transaction ID
```

---

# 24. ADMIN ROLES

```text
admin_users
    ↓
admin_roles
    ↓
admin_permissions
```

Example:

```text
SUPER_ADMIN
ADMIN
FINANCE_ADMIN
GAME_ADMIN
SUPPORT_ADMIN
KYC_ADMIN
REPORT_ADMIN
```

Finance admin ko game configuration access dene ki zarurat nahi.

---

# 25. AUDIT SYSTEM

Har sensitive action:

```text
audit_logs/
```

mein:

```text
id
admin_id
user_id
action
entity
entity_id
old_value
new_value
reason
ip_address
device
timestamp
```

---

# 26. APP SETTINGS

```text
settings/
├── AppSettings
├── WalletSettings
├── DepositSettings
├── WithdrawalSettings
├── ReferralSettings
├── GameSettings
├── NotificationSettings
├── MaintenanceSettings
└── SecuritySettings
```

Admin se configuration change ho sakegi, bina app update kiye, jahan appropriate ho.

---

# 27. REPORTING

```text
reports/
├── UserReport
├── DepositReport
├── WithdrawalReport
├── TransactionReport
├── GameReport
├── BetReport
├── RevenueReport
├── ReferralReport
└── FinancialReport
```

Export:

```text
CSV
Excel
PDF
```

---

# 28. TESTING STRUCTURE

```text
tests/
├── unit/
│   ├── wallet/
│   ├── payments/
│   ├── withdrawals/
│   ├── games/
│   └── users/
│
├── integration/
│   ├── auth/
│   ├── wallet/
│   ├── payments/
│   ├── games/
│   └── admin/
│
└── e2e/
    ├── deposit/
    ├── withdrawal/
    ├── game/
    └── user/
```

---

# 29. DOCUMENTATION

```text
docs/
├── architecture/
│   ├── system.md
│   ├── android.md
│   ├── backend.md
│   ├── games.md
│   └── admin.md
│
├── database/
│   ├── schema.md
│   ├── relationships.md
│   └── migrations.md
│
├── api/
│   ├── auth.md
│   ├── users.md
│   ├── wallet.md
│   ├── deposits.md
│   ├── withdrawals.md
│   ├── games.md
│   └── admin.md
│
└── games/
    ├── coin-flip.md
    ├── classic-dice.md
    ├── keno.md
    ├── mines.md
    ├── perya-color.md
    ├── ring-of-fortune.md
    ├── double.md
    └── limbo.md
```

---

# 30. Final architecture

Overall:

```text
                         ┌───────────────────┐
                         │    ADMIN PANEL     │
                         └─────────┬─────────┘
                                   │
                                   ▼
┌───────────────┐          ┌───────────────────┐
│ ANDROID APP   │◄────────►│ KOTLIN BACKEND    │
│ Kotlin        │ REST     │ Ktor              │
│               │ WS       │                   │
└───────┬───────┘          └─────────┬─────────┘
        │                            │
        ▼                            ▼
┌───────────────┐          ┌───────────────────┐
│ GAMES MODULE  │          │     REDIS         │
│               │          │ Cache/Lock/RT     │
│ Coin Flip     │          └───────────────────┘
│ Classic Dice  │
│ Keno          │          ┌───────────────────┐
│ Mines         │          │   POSTGRESQL      │
│ Perya Color   │          │ Users/Wallet/Game │
│ Ring Fortune  │          │ Bets/Payments     │
│ Double        │          │ Admin/Audit       │
│ Limbo         │          └───────────────────┘
└───────────────┘
```

### Aur sabse important rule

**Abhi game code nahi likhenge.**

Pehle:

```text
PROJECT STRUCTURE
        ↓
DATABASE STRUCTURE
        ↓
BACKEND MODULES
        ↓
ADMIN MODULES
        ↓
ANDROID MODULES
        ↓
GAME MODULES
        ↓
GAME ASSET STRUCTURE
        ↓
API CONTRACTS
        ↓
AUTH / WALLET / PAYMENT ARCHITECTURE
        ↓
PHIR ACTUAL GAME CODE
```

Aur **game count hardcoded nahi hoga**. Abhi tumhare diye hue 8 posters catalog mein rahenge; jab tum final base-game list doge, uske according `games/` ke modules add honge.

Ye approach tumhare current Kotlin repo ke existing `shared + backend + android:app` structure ko preserve karke uske upar **dedicated `games` + `admin` architecture** add karta hai, instead of existing project ko random files se bhar dena.
