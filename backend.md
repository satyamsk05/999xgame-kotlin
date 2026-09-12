Tumhe mere `999xgame-kotlin` project ka BACKEND end-to-end complete aur production-ready banana hai.

## 🔴 MOST IMPORTANT — AUTHENTICATION

Mere app ka user authentication **Loggin.dev ka OTP-less WhatsApp Login** hai.

Official documentation:
https://loggin.dev/docs

Original/reference implementation:
`999xgame`

### ❌ YE SYSTEM BILKUL MAT BANANA

* SMS OTP
* WhatsApp OTP
* 6-digit OTP
* OTP input screen
* Phone + OTP verification
* Hardcoded `123456`
* Fake/demo OTP
* Password-based user login

### ✅ ACTUAL USER LOGIN FLOW

Exactly ye flow implement karo:

`Continue with WhatsApp`
↓
Backend Loggin se temporary authentication token/link create kare
↓
Loggin WhatsApp verification link return kare
↓
App WhatsApp link externally open kare
↓
User WhatsApp mein pre-filled verification message send kare
↓
Loggin user ka WhatsApp number verify kare
↓
Backend verification result receive kare
↓
Backend verified phone number obtain kare
↓
Temporary verification session atomically consume kare
↓
Phone number se user FIND/CREATE kare
↓
Blocked user ko reject kare
↓
Application JWT generate kare
↓
App session save kare
↓
New user → Name/Onboarding
Existing user → Home

**User ko OTP type nahi karna hai.**

---

# 1. ORIGINAL `999xgame` KO REFERENCE BANAO

Original repository:

`999xgame`

ko inspect karo, especially authentication implementation.

Relevant original files:

* `lib/screens/login_screen.dart`
* `backend/src/auth/auth.controller.js`
* `backend/src/auth/loggin.service.js`

Original behavior ko Kotlin backend/frontend architecture mein properly migrate karo.

Business behavior change mat karo.

---

# 2. LOGGIN.DEV INTEGRATION

Loggin official service use karo.

Documentation:

https://loggin.dev/docs

Existing/reference implementation mein use hone wale concepts ko follow karo:

* Loggin App Key
* create authentication token
* verification link
* verification waiting/listener
* verified phone
* temporary verification session

Agar official SDK/version current project ke dependency setup se different hai, documentation ke according compatible implementation choose karo.

### Backend

Loggin credentials **ONLY environment variables** se load honge.

Example:

`LOGGIN_APP_KEY=`

Agar API URL/configuration required ho:

`LOGGIN_API_URL=`

Lekin exact variables actual Loggin SDK/docs aur code inspect karke determine karo.

Hardcode:

* App Key
* API key
* secrets
* tokens

kabhi mat karo.

---

# 3. TEMPORARY LOGIN SESSION

Loggin verification ke liye temporary server-side session maintain karo.

Redis available hai to Redis use karo.

Session mein appropriate information ho sakti hai:

* temporary Loggin token
* status
* createdAt
* expiresAt
* verification link
* verifiedPhone

Temporary session approximately **5 minutes** ke baad expire ho.

Verified temporary session:

* one-time use
* atomically consumed
* concurrent requests mein reuse impossible

hona chahiye.

Important:

**Loggin temporary token ≠ application JWT**

Dono ko completely separate rakho.

---

# 4. REQUIRED AUTH ROUTES

Proper backend routes implement karo.

At minimum architecture should support:

`POST /loggin/create-token`

`GET /loggin/status/:token`

`POST /loggin/verify`

`POST /logout`

Frontend ke actual API contract ke according naming adjust kar sakte ho, lekin behavior same rehna chahiye.

### `/loggin/create-token`

Backend:

1. Loggin SDK/provider se token create kare.
2. Verification link receive kare.
3. Temporary server-side session create kare.
4. Expiry set kare.
5. Frontend ko token/link/expiry return kare.

### `/loggin/status/:token`

Status:

* PENDING
* VERIFIED
* EXPIRED
* ALREADY_CONSUMED
* ACCOUNT_BLOCKED

appropriately return kare.

### VERIFIED

Verified phone milne ke baad:

1. Temporary session atomically consume.
2. User find/create.
3. Block status check.
4. Application JWT create.
5. User data return.

---

# 5. USER CREATION

Verified WhatsApp phone number ko primary authentication identity treat karo.

Existing user:

→ login

New user:

→ create account

New user ko onboarding state ke according:

→ Name screen

phir:

→ Home

Existing onboarded user:

→ directly Home.

User ko manually phone number enter karne ke liye force mat karo agar original flow mein required nahi hai.

---

# 6. CURRENT DEMO OTP COMPLETELY REMOVE

Current Kotlin backend mein agar aisa kuch hai:

`123456`

ya demo OTP generation/validation:

**REMOVE IT.**

Production auth mein demo OTP nahi hona chahiye.

Search entire project for:

* `123456`
* `otp`
* `sendOtp`
* `verifyOtp`
* fake OTP responses
* demo authentication

Aur determine karo kya obsolete hai.

Jo obsolete user OTP architecture hai usko Loggin OTP-less architecture se replace karo.

---

# 7. `.ENV` COMPLETE SETUP

Backend ke liye actual required environment variables identify karo.

Create/update:

`.env.example`

and ensure `.env` git mein commit na ho.

`.gitignore` verify karo.

Example only:

```text
APP_ENV=development
PORT=8080
HOST=0.0.0.0

DATABASE_URL=
DATABASE_HOST=
DATABASE_PORT=
DATABASE_NAME=
DATABASE_USER=
DATABASE_PASSWORD=

JWT_SECRET=
JWT_ISSUER=
JWT_AUDIENCE=
JWT_EXPIRATION_SECONDS=

REDIS_URL=
REDIS_HOST=
REDIS_PORT=
REDIS_PASSWORD=

LOGGIN_APP_KEY=
LOGGIN_API_URL=

CORS_ALLOWED_ORIGINS=

ADMIN_JWT_EXPIRATION_SECONDS=

PAYMENT_PROVIDER=
PAYMENT_API_KEY=
PAYMENT_SECRET=

WITHDRAWAL_PROVIDER=
WITHDRAWAL_API_KEY=
WITHDRAWAL_SECRET=

NOTIFICATION_PROVIDER=
NOTIFICATION_API_KEY=
```

**IMPORTANT:**

Ye complete final list nahi hai.

Actual source code + dependencies + database + Loggin documentation inspect karke **exact variables determine karo**.

Jo variable actually required nahi hai usko add mat karo.

---

# 8. MUJHE MISSING ENV VALUES MANGO

Agar kisi external service ki credential required hai aur mere paas abhi nahi hai:

Implementation mat roko.

Instead:

1. Environment variable define karo.
2. `.env.example` mein placeholder rakho.
3. Configuration class mein load karo.
4. Validation implement karo.
5. Mujhe exact value/credential ki list do.

Final report mein:

### REQUIRED ENV VALUES

| Variable | Required | Purpose | Where to get |
| -------- | -------- | ------- | ------------ |

Example:

`LOGGIN_APP_KEY`
→ Required for WhatsApp authentication
→ Loggin dashboard

`DATABASE_URL`
→ PostgreSQL

`JWT_SECRET`
→ Application JWT signing

etc.

Main real values baad mein `.env` mein add karunga.

---

# 9. DATABASE

Existing PostgreSQL migrations inspect karo.

Ensure:

* all migrations valid
* DB connection production-ready
* connection pool
* transactions
* foreign keys
* indexes
* constraints
* repository implementation

properly work kare.

Application startup par DB unavailable ho to production mein silently `ready` return mat karo.

---

# 10. JWT

Existing `JwtService` inspect karo.

Implement/verify:

* secret from ENV
* issuer
* audience
* expiration
* claims
* token validation
* expired token handling
* invalid token handling

Secrets hardcode nahi hone chahiye.

User JWT aur Admin authentication ko logically separate rakho.

---

# 11. ADMIN AUTH

IMPORTANT DISTINCTION:

### APP USER

`Continue with WhatsApp`
→ Loggin OTP-less authentication

### ADMIN

Existing `admins` table ke according:

`username + password`
→ JWT
→ role/authorization

Admin login ko WhatsApp login ke saath mix mat karo unless explicitly required.

Admin password plaintext mein store nahi hona chahiye.

Existing admin table/schema reuse karo.

---

# 12. ADMIN API

Current Admin frontend inspect karo:

`backend/src/main/resources/public/admin/`

Frontend jin `/api/admin/*` APIs ko call karta hai unko backend mein implement karo.

Including:

* `/api/admin/auth/login`
* `/api/admin/auth/me`
* `/api/admin/auth/logout`
* dashboard
* users
* user details
* block/unblock
* balance adjustment
* KYC
* deposits
* withdrawals
* games
* game rounds
* bets
* promotions
* ledger
* reports
* notifications
* security
* settings
* audit logs

Existing DB schema/services reuse karo.

---

# 13. ADMIN SECURITY

Financial operations must be:

* authenticated
* authorized
* transactional
* idempotent
* audited

Example:

Deposit confirmation double click:

→ wallet sirf ek baar credit ho.

Withdrawal processing double request:

→ sirf ek baar process ho.

Balance adjustment:

→ ledger + audit log required.

---

# 14. WALLET + LEDGER

Existing integer-paise architecture preserve karo.

Never use floating-point money calculations.

Every balance-changing operation must be atomic:

* deposit
* withdrawal
* bet
* winnings
* refund
* admin adjustment

Wallet + ledger + business record same transaction mein update hon.

Race conditions prevent karo.

---

# 15. GAMES

Existing games inspect karo:

* Seven Up Down
* Crush
* Dragon Tiger
* future/COMING_SOON games

Ensure implemented games have:

* round lifecycle
* betting window
* bet validation
* wallet debit
* result
* settlement
* wallet credit
* ledger
* idempotency
* provably-fair logic where applicable

Fake/demo production game results mat rakho.

---

# 16. WEBSOCKET

Existing WebSocketHub inspect karo.

Ensure:

* authentication
* connection handling
* disconnect handling
* reconnect handling
* game updates
* round updates
* result updates

properly work karein.

---

# 17. ERROR HANDLING

Consistent API error responses implement karo.

Examples:

400 validation
401 authentication
403 authorization/blocked
404 not found
409 conflict
410 expired
429 rate limit
500 internal error

Production mein stack traces/secrets expose mat karo.

---

# 18. SECURITY

Verify:

* request validation
* CORS
* rate limiting
* authentication rate limiting
* admin password hashing
* JWT security
* secret protection
* SQL injection protection
* sensitive log protection

Never log:

* passwords
* JWT secrets
* Loggin App Key
* API keys
* authentication tokens unnecessarily

---

# 19. HEALTH + READY

Health endpoint:

→ application alive

Readiness:

→ DB available
→ required production dependencies available

Agar PostgreSQL down hai to backend ko falsely READY mat declare karo.

---

# 20. TESTS

Tests implement/fix karo.

At minimum:

### Authentication

* Loggin token creation
* pending verification
* successful verification
* expired token
* consumed token
* blocked user
* new user
* existing user
* concurrent session consumption

### Wallet

* deposit
* withdrawal
* bet
* win
* refund
* admin adjustment
* duplicate request

### Admin

* login
* invalid login
* JWT
* authorization
* audit

### Critical test

Same financial request simultaneously 2 baar aaye:

Expected:

**Only one successful financial operation.**

---

# 21. BUILD

After implementation run:

* Gradle build
* unit tests
* integration tests where available
* migrations verification
* API compilation
* frontend/backend API contract verification

Errors ko fix karo.

Sirf build pass hona sufficient nahi hai.

---

# 22. DOCUMENTATION

Create/update:

`backend/README.md`

Include:

* requirements
* PostgreSQL
* Redis
* `.env`
* migrations
* development setup
* production setup
* Loggin setup
* Admin setup
* API startup
* testing
* deployment

Also create:

`.env.example`

---

# 23. FINAL REPORT

Implementation ke end mein mujhe exactly ye report do:

## COMPLETED

Backend mein kya-kya complete hua.

## FIXED

Existing kya problems fix ki.

## LOGGIN AUTH

Explain karo ki actual OTP-less WhatsApp flow kaise implemented hai.

Confirm explicitly:

**NO OTP INPUT EXISTS.**

## ENV VARIABLES

Exact complete list.

## MUJHE KYA DENA HAI

Jo credentials/secrets mujhe manually provide karne hain.

Especially Loggin ke credentials.

## OPTIONAL ENV

Development/testing ke optional values.

## BLOCKERS

Sirf actual external dependencies/credentials ke blockers.

## TEST RESULTS

Actual build/test results.

## REMAINING WORK

Sirf genuinely remaining work.

---

# 🔴 FINAL RULES

1. **User login = Loggin.dev OTP-less WhatsApp.**
2. **No OTP.**
3. **No 6-digit code.**
4. **No fake OTP.**
5. **No hardcoded credentials.**
6. **Loggin credentials backend-only.**
7. **Application JWT separate from Loggin temporary token.**
8. **Temporary login session one-time consumable.**
9. **Financial operations transactional + idempotent.**
10. **Admin auth remains username/password + JWT.**
11. **Use existing project architecture wherever possible.**
12. **Do not create duplicate authentication/database/wallet architectures.**
13. **Do not stop implementation merely because `.env` credentials are missing.**
14. **Use placeholders and clearly tell me which real ENV values are required.**
15. **Backend ko maximum possible extent tak abhi fully implement/test karo.**
16. **Real credentials main baad mein `.env` mein add karunga.**
