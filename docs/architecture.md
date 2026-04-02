# Architecture

## Microservice Decomposition

| Service | Port | Responsibility | DB Schema |
|---------|------|----------------|-----------|
| api-gateway | 8080 | Routing, JWT validation, CORS, rate limiting | — |
| auth-service | 8081 | Registration, login, JWT tokens, role management | `auth` |
| deal-service | 8082 | Escrow deal lifecycle (core domain) | `deals` |
| payment-service | 8083 | Mock accounts, escrow hold/release, ledger | `payments` |
| notification-service | 8084 | Kafka consumer, in-app notifications | `notifications` |
| frontend | 3000 | Next.js 14, SSR, Tailwind UI | — |

## Infrastructure

| Component | Port | Purpose |
|-----------|------|---------|
| PostgreSQL 16 | 5432 | Single instance, schema-per-service |
| Kafka + Zookeeper | 9092 | Event bus between services |
| Prometheus | 9090 | Metrics scraping |
| Grafana | 3001 | Dashboards |
| Elasticsearch | 9200 | Centralized logs |
| Kibana | 5601 | Log viewer |

## Deal State Machine

```
CREATED → FUNDED → DELIVERED → RELEASING → COMPLETED
                   DELIVERED → DISPUTED → RESOLVED
Any (except terminal) → CANCELLED
Terminal states: COMPLETED, CANCELLED, RESOLVED
```

### State Transitions
| From | To | Actor | Trigger |
|------|----|-------|---------|
| — | CREATED | depositor | Create deal |
| CREATED | FUNDED | depositor | Fund deal → payment hold |
| FUNDED | DELIVERED | beneficiary | Confirm delivery |
| DELIVERED | RELEASING | depositor | Confirm release |
| RELEASING | COMPLETED | system | Payment released |
| DELIVERED | DISPUTED | depositor | Open dispute |
| DISPUTED | RESOLVED | operator | Resolve (release or refund) |
| !terminal | CANCELLED | depositor/operator | Cancel deal |

## Core Entities (First Slice)

### auth-service
- **User** — id, email, password_hash, full_name, role, enabled, timestamps
- **RefreshToken** — id, user_id, token, expires_at

### deal-service
- **Deal** — id, title, description, amount, currency, depositor_id, beneficiary_id, status, timestamps
- **DealEvent** — id, deal_id, event_type, actor_id, actor_role, previous_status, new_status, payload (JSONB), created_at

### payment-service
- **Account** — id, user_id, balance (default 10000 RUB), currency
- **Transaction** — id, deal_id, from/to account, amount, type, status, timestamps
- **EscrowHold** — id, deal_id, amount, status (HELD/RELEASED/REFUNDED)

## Kafka Topics
| Topic | Producer | Consumer | Purpose |
|-------|----------|----------|---------|
| `deal-events` | deal-service | payment-service, notification-service | Deal state changes |
| `payment-events` | payment-service | deal-service | Payment confirmations |

## API Endpoints (through gateway :8080)

### Auth
```
POST /api/auth/register
POST /api/auth/login
POST /api/auth/refresh
GET  /api/auth/me
```

### Deals
```
POST /api/deals
GET  /api/deals
GET  /api/deals/{id}
POST /api/deals/{id}/fund
POST /api/deals/{id}/deliver
POST /api/deals/{id}/confirm
POST /api/deals/{id}/cancel
POST /api/deals/{id}/dispute       (Step 12)
POST /api/deals/{id}/resolve       (Step 12)
```

### Payments
```
GET /api/payments/account
GET /api/payments/transactions
```

### Notifications
```
GET  /api/notifications
POST /api/notifications/{id}/read
```

### Admin/Operator
```
GET   /api/admin/users
PATCH /api/admin/users/{id}
GET   /api/operator/deals
POST  /api/operator/deals/{id}/override
```

## Design Decisions

### Why Gradle (not Maven)
1. **Kotlin DSL** — type-safe build scripts with IDE autocompletion
2. **Build performance** — incremental builds + build cache; only changed modules recompile
3. **Multi-module ergonomics** — `subprojects {}` defines shared config once vs Maven's verbose parent POM
4. **Version catalog** — `libs.versions.toml` centralizes all dependency versions in one file
5. **Task flexibility** — custom tasks (Docker, Kafka topics, integration tests) are trivial

### Why Tailwind CSS
1. **Rapid prototyping** — utility classes build professional UI without custom CSS
2. **Design consistency** — built-in design tokens (spacing, colors, typography) without a designer
3. **Bundle size** — JIT compiler produces tiny CSS (5-15KB), only used classes ship
4. **First-class Next.js support** — zero-config with create-next-app
5. **Headless UI compatibility** — pairs with Radix UI for accessible components

### Why Schema-per-Service (single PostgreSQL)
- Full isolation per service at schema level
- Simple for MVP — one Postgres instance to manage
- Easy migration to separate instances later if needed

## Directory Structure
```
escrow/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle/libs.versions.toml
├── docker-compose.yml
├── init-db/init.sql
├── api-gateway/
├── auth-service/
├── deal-service/
├── payment-service/
├── notification-service/
├── frontend/
├── .github/workflows/ci.yml
└── docs/
```
