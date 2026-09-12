# Phase 1 Context: Persistence & Database Initialization

## Goal
Establish PostgreSQL database connection pool via HikariCP, execute SQL migration DDLs, and build JWT/Bcrypt authentication APIs in Ktor backend.

## User Requirements & Decisions
- Connection parameters configured via environment variables (`DB_URL`, `DB_USER`, `DB_PASSWORD`).
- Password hashing must use Bcrypt with a work factor of 12.
- JWT tokens must encode `userId`, `username`, and `roles`.
- Auth routes must return JSON payloads matching `:shared` DTO formats.
