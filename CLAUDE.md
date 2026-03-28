# CLAUDE.md — рабочий контракт проекта

## Цель
Escrow-first веб-продукт на микросервисной архитектуре с локально запускаемым MVP, пригодным для ручного и автоматического тестирования.

## Зафиксированный стек (не менять без отдельного решения)
- Backend: Java, Spring Boot, Spring Web, Spring Security, Spring Data JPA, Hibernate, Gradle
- Frontend: TypeScript, Next.js, React, Tailwind CSS
- Database: PostgreSQL
- Messaging: Kafka
- Observability: Prometheus, Grafana, Elasticsearch, Kibana
- Infra: Docker Compose
- CI: GitHub Actions

## Режим работы
1. Работать только инкрементально: один шаг за сессию.
2. Не переписывать готовое без необходимости.
3. Любой шаг завершать в запускаемом/проверяемом состоянии.
4. При обнаружении дефекта прошлого шага — исправлять минимально необходимое.
5. Перед изменениями читать: `CLAUDE.md`, `docs/progress.md`, `docs/next-step.md`, `README.md` (если есть).
6. После каждого шага обязательно обновлять `docs/progress.md` и `docs/next-step.md`.

## Definition of Done для каждого шага
- Изменения ограничены текущим шагом плана.
- Выполнены релевантные проверки/тесты.
- Документация статуса обновлена (`progress` + `next-step`).
- Сформирован короткий отчёт: что изменено, чем проверено, что работает, ограничения.
