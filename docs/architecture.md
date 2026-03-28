# Architecture (MVP, escrow-first)

## 1) Почему выбран Gradle
- Нативная скорость и кэширование сборки для многомодульных Java-проектов.
- Удобная конфигурация Spring Boot экосистемы и единый способ запуска тестов/линтеров в CI.
- Хорошо подходит для постепенного роста монорепозитория с несколькими сервисами.

## 2) Почему выбран Tailwind CSS
- Быстрая инкрементная верстка без тяжелой кастомной дизайн-системы на старте MVP.
- Предсказуемый utility-first подход, удобный для Next.js/React компонент.
- Простая стандартизация UI и снижение объема кастомного CSS.

## 3) Разбиение MVP по микросервисам
1. **identity-service**
   - Пользователи, роли (депонент, бенефициар, оператор, администратор), аутентификация/авторизация.
2. **escrow-service**
   - Договоры эскроу, жизненный цикл сделки, бизнес-правила переходов состояний.
3. **payment-service** (MVP-эмуляция)
   - Резервирование, подтверждение и освобождение средств (через stub/adapters).
4. **notification-service**
   - Отправка доменных уведомлений (email/webhook-заглушка) на события из Kafka.
5. **audit-service**
   - Неизменяемый журнал действий и бизнес-событий.
6. **api-gateway**
   - Единая точка входа для frontend, маршрутизация и базовые политики безопасности.
7. **frontend (Next.js)**
   - Кабинеты ролей, сценарии создания/принятия/завершения сделки.

## 4) Первый рабочий вертикальный срез (Vertical Slice #1)
**Цель:** провести простую escrow-сделку от создания до освобождения средств в локальном окружении.

В срез входят:
- api-gateway
- identity-service
- escrow-service
- payment-service (эмуляция)
- frontend
- PostgreSQL (как минимум для identity + escrow)
- Kafka (события escrow)

Наблюдаемость (Prometheus/Grafana/ELK) подключается сначала базово на уровне health/метрик и логирования контейнеров, затем расширяется.

## 5) Первые сущности и state machines

### 5.1 Сущности
- `User` (id, email, password_hash, role, status)
- `EscrowContract` (id, depositor_id, beneficiary_id, amount, currency, status, created_at)
- `EscrowEvent` (id, contract_id, type, payload, occurred_at, actor_id)
- `PaymentHold` (id, contract_id, amount, currency, status, provider_ref)

### 5.2 State machine: EscrowContract
`DRAFT -> PENDING_BENEFICIARY -> ACTIVE -> RELEASE_REQUESTED -> RELEASED`

Альтернативные ветки:
- `ACTIVE -> DISPUTED -> RESOLVED`
- `PENDING_BENEFICIARY -> CANCELED`
- `ACTIVE -> CANCELED` (по правилам ролей)

### 5.3 State machine: PaymentHold (MVP)
`INITIATED -> HELD -> RELEASED`
или
`INITIATED -> HELD -> REFUNDED`

## 6) Ожидаемые URL и health endpoints после локального запуска (этапно)
- Frontend: `http://localhost:3000`
- API Gateway: `http://localhost:8080`
- Identity health: `http://localhost:8081/actuator/health`
- Escrow health: `http://localhost:8082/actuator/health`
- Payment health: `http://localhost:8083/actuator/health`
- Notification health: `http://localhost:8084/actuator/health` (на следующих шагах)
- Audit health: `http://localhost:8085/actuator/health` (на следующих шагах)

Infra/observability:
- Prometheus: `http://localhost:9090`
- Grafana: `http://localhost:3001`
- Elasticsearch: `http://localhost:9200`
- Kibana: `http://localhost:5601`

## 7) Инкрементный план реализации (1..N)
1. **Foundation docs & project contract** (текущий шаг).
2. **Monorepo scaffold**: директории сервисов, Gradle settings, Next.js app skeleton, базовый docker-compose.
3. **Identity service MVP**: регистрация/логин, роли, PostgreSQL, миграции, health.
4. **Escrow service MVP**: CRUD контрактов + базовые переходы состояний, PostgreSQL, health.
5. **Gateway + frontend baseline**: проксирование API, базовые страницы входа и создания сделки.
6. **Payment stub + escrow integration**: hold/release/refund в эмуляции.
7. **Kafka events + notification stub**: публикация/потребление ключевых событий.
8. **Audit trail service**: прием и хранение событий аудита.
9. **Observability baseline**: метрики, дашборды, централизованные логи.
10. **CI pipelines (GitHub Actions)**: lint/test/build для frontend/backend, smoke compose-up checks.
11. **Hardening & E2E smoke**: минимальные интеграционные сценарии, фиксы, документация запуска MVP.
