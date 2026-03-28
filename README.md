# Платформа эскроу-first (Escrow-first Platform)

Локально запускаемый MVP-каркас микросервисной платформы с центральной сущностью счёта эскроу (escrow account).

## Быстрый старт (Quick Start)
1. Скопировать переменные окружения:
   ```bash
   cp .env.example .env
   ```
2. Поднять весь каркас одной командой:
   ```bash
   ./scripts/start-local.sh
   ```
3. Выполнить smoke-check:
   ```bash
   ./scripts/smoke-check.sh
   ```

## Вертикальный срез сделки (Deal Vertical Slice)
Реализованный flow состояния сделки:
- `DRAFT` → `AGREED` → `ACCOUNT_OPENED` → `AWAITING_FUNDING`

### Deal API
- `POST /api/v1/deals` — создать сделку (роль: DEPOSITOR)
- `GET /api/v1/deals` — список сделок
- `GET /api/v1/deals/{id}` — карточка сделки
- `POST /api/v1/deals/{id}/agree` — согласовать сделку (DEPOSITOR/BENEFICIARY)
- `POST /api/v1/deals/{id}/open-escrow-account` — открыть счёт эскроу (DEPOSITOR/OPERATOR/ADMIN)

### Escrow Account API
- `POST /api/v1/escrow-accounts/open` — открыть счёт эскроу
- `GET /api/v1/escrow-accounts/by-deal/{dealId}` — получить счёт по id сделки

### Auth contour
Тестовые пользователи:
- `depositor / depositor123` → `DEPOSITOR`
- `beneficiary / beneficiary123` → `BENEFICIARY`
- `operator / operator123` → `OPERATOR`
- `admin / admin123` → `ADMIN`

## Доступные endpoint после запуска
- Frontend: http://localhost:3000
- Auth service: http://localhost:8081 (Swagger: `/swagger-ui.html`)
- Deal service: http://localhost:8080 (Swagger: `/swagger-ui.html`)
- Escrow-account service: http://localhost:8082 (Swagger: `/swagger-ui.html`)
- PostgreSQL: localhost:5432
- Kafka: localhost:9092
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3001
- Elasticsearch: http://localhost:9200
- Kibana: http://localhost:5601

## Остановка
```bash
./scripts/stop.sh
```

## Сброс окружения
```bash
./scripts/reset.sh
```
