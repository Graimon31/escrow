# Next step (Step 2)

## Цель
Подготовить стартовый монорепозиторий и минимальный локальный запуск каркаса без бизнес-логики.

## Объём следующего шага
1. Создать структуру каталогов:
   - `backend/identity-service`
   - `backend/escrow-service`
   - `backend/payment-service`
   - `backend/notification-service`
   - `backend/audit-service`
   - `backend/api-gateway`
   - `frontend/web`
2. Инициализировать Gradle multi-module skeleton для backend сервисов.
3. Инициализировать Next.js + TypeScript + Tailwind приложение в `frontend/web`.
4. Добавить `docker-compose.yml` с минимумом:
   - PostgreSQL
   - Kafka (+zookeeper или KRaft-конфигурация)
   - заглушки сервисов/или один-две стартовые app-контейнера (по готовности)
5. Добавить базовые health endpoints в стартовые Spring Boot приложения (минимум identity + escrow).
6. Добавить `README.md` с командами локального запуска.

## Критерии готовности шага 2
- `docker compose up` поднимает минимум инфраструктуру (PostgreSQL, Kafka) и хотя бы 1-2 app-контейнера.
- `http://localhost:8081/actuator/health` и `http://localhost:8082/actuator/health` отвечают UP (или документирована временная заглушка).
- Frontend skeleton доступен на `http://localhost:3000`.
- Обновлены `docs/progress.md` и `docs/next-step.md`.
