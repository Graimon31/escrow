# Next Step: Step 1 — Project Scaffolding and Build Infrastructure

## Goal
Gradle multi-module project builds successfully. Each service has a minimal Spring Boot app. Frontend scaffolded with Next.js.

## Tasks
1. Create root `build.gradle.kts` with Java 21 toolchain, Spring Boot 3.x BOM, shared config
2. Create `settings.gradle.kts` with all module includes
3. Create `gradle/libs.versions.toml` with centralized dependency versions
4. Create module directories: `api-gateway/`, `auth-service/`, `deal-service/`, `payment-service/`, `notification-service/`
5. Each Java module: `build.gradle.kts` + minimal `Application.java` + `application.yml` with unique port
6. Scaffold frontend: `npx create-next-app@14` with TypeScript, Tailwind, App Router
7. Add `.gitignore` for Java/Gradle/Node

## Verification
```bash
./gradlew build                     # all modules compile
cd frontend && npm run build        # frontend builds
# Each service starts and responds on /actuator/health
```

## Expected Outcome
- 5 Java modules compile and pass (no tests yet, just skeleton)
- Frontend builds successfully
- Project structure matches docs/architecture.md
