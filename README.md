# Платформа эскроу-first (Escrow-first Platform)

Локально запускаемый MVP-каркас микросервисной платформы с центральной сущностью счёта эскроу (escrow account).

## Быстрый старт (Quick Start)
1. Скопировать переменные окружения:
   ```bash
   cp .env.example .env
   ```
2. Запустить инфраструктуру и каркас:
   ```bash
   ./scripts/start-local.sh
   ```
3. Проверить базовую доступность:
   ```bash
   ./scripts/smoke-check.sh
   ```

## Остановка
```bash
./scripts/stop.sh
```

## Сброс окружения
```bash
./scripts/reset.sh
```

## Структура
- `backend/services` — каркасы backend-сервисов (Gradle + Spring Boot).
- `frontend` — каркас Next.js + TypeScript + Tailwind CSS.
- `docker-compose.yml` — инфраструктурные контейнеры и базовые сервисы.
- `docs` — архитектура, прогресс, следующий шаг.
