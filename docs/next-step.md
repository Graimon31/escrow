# Next Step: Step 3 — Auth Service: Registration and JWT Login

## Goal
Users can register, login, and receive JWT tokens. Auth service connects to PostgreSQL.

## Tasks
1. Add dependencies to auth-service: Spring Security, Spring Data JPA, Flyway, PostgreSQL, jjwt
2. Create Flyway migration `V1__create_users_table.sql` in `auth` schema
3. Implement `User` entity, `UserRepository`
4. Implement `JwtProvider` (access token 15min, refresh token 7d)
5. Implement `AuthService` (register, login, refresh, me)
6. Implement `AuthController` with endpoints:
   - `POST /api/auth/register`
   - `POST /api/auth/login`
   - `POST /api/auth/refresh`
   - `GET /api/auth/me`
7. Configure Spring Security (permit auth endpoints, secure others)
8. Add `application-docker.yml` with Postgres datasource
9. Write integration tests with Testcontainers

## Verification
```bash
./gradlew :auth-service:test          # tests pass
./gradlew :auth-service:bootRun       # starts with local Postgres
curl -X POST localhost:8081/api/auth/register -H 'Content-Type: application/json' \
  -d '{"email":"test@test.com","password":"password","fullName":"Test User","role":"DEPOSITOR"}'
curl -X POST localhost:8081/api/auth/login -H 'Content-Type: application/json' \
  -d '{"email":"test@test.com","password":"password"}'
```

## Expected Outcome
- User registration with BCrypt password hashing
- JWT access + refresh tokens returned on login
- `/api/auth/me` returns user info with valid Bearer token
- Flyway creates `auth.users` and `auth.refresh_tokens` tables automatically
