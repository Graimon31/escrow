# Платформа счёта эскроу (Escrow-first platform)

MVP микросервисной платформы для управления условными сделками эскроу (escrow deals).

## Быстрый запуск
```bash
./scripts/start-local.sh
```

## Остановка
```bash
./scripts/stop-local.sh
```

## Сброс
```bash
./scripts/reset-local.sh
```

## Seed
```bash
./scripts/seed.sh
```

## URL после запуска
- Frontend: http://localhost:3000
- Deal Service API: http://localhost:8081/api/v1/deals
- Swagger UI (deal-service): http://localhost:8081/swagger-ui.html
- Grafana: http://localhost:3001 (admin/admin)
- Kibana: http://localhost:5601
- Prometheus: http://localhost:9090

## Тестовые пользователи
- депонент (depositor): `depositor / depositor123`
- бенефициар (beneficiary): `beneficiary / beneficiary123`
- администратор (admin): `admin / admin123`

## Архитектура
- Frontend: Next.js + TypeScript + Tailwind CSS.
- Backend: 11 Spring Boot сервисов.
- БД: PostgreSQL.
- События: Kafka.
- Наблюдаемость: Prometheus + Grafana.
- Логи: Elasticsearch + Kibana.
