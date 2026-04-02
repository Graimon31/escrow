# MVP Complete — Architecture Upgraded

Steps 0–14 implemented. The escrow platform MVP includes 7 microservices with double-entry ledger, escrow account state machine, transactional outbox, audit trail, and document management.

## Architecture (7 Services)
| Service | Port | Domain |
|---------|------|--------|
| api-gateway | 8080 | Routing, JWT validation, CORS |
| auth-service | 8081 | Registration, login, JWT, roles, admin API |
| deal-service | 8082 | Deal lifecycle (15-state SM), operator API |
| payment-service | 8083 | Double-entry ledger, escrow accounts (10-state SM), outbox |
| notification-service | 8084 | Kafka consumer, in-app notifications |
| audit-service | 8085 | Immutable event log from Kafka |
| document-service | 8086 | File upload/download per deal |

## How to Run
```bash
docker compose up --build
```

## Web Interfaces
| Service | URL | Credentials |
|---------|-----|-------------|
| Frontend | http://localhost:3000 | Register a new account |
| Grafana | http://localhost:3001 | admin / admin |
| Prometheus | http://localhost:9090 | — |
| Kibana | http://localhost:5601 | — |
| Elasticsearch | http://localhost:9200 | — |

## Testing the Full Flow
1. Register as DEPOSITOR at /register
2. Register as BENEFICIARY (different email) in another browser/incognito
3. As depositor: create deal → submit → fund → confirm
4. As beneficiary: agree → deliver
5. Register as OPERATOR to access /operator panel
6. Register as ADMINISTRATOR to access /admin panel
7. Open a dispute, resolve it from operator panel
8. Upload documents on /documents page
9. Check Grafana dashboards for metrics
10. Check Kibana for service logs (create index pattern: filebeat-*)

## Key Financial Features
- **Double-entry ledger**: Every money movement produces balanced DEBIT+CREDIT entries
- **Escrow account SM**: Tracks escrow lifecycle independently from deal SM
- **Idempotency**: Financial operations are idempotent via Idempotency-Key header
- **Transactional outbox**: Prevents dual-write problems between DB and Kafka

## Future Enhancements
- Saga orchestrator for distributed transaction coordination
- WebSocket real-time notifications
- Email/SMS notification channels
- Payment gateway integration (replacing mock accounts)
- Correlation IDs for cross-service tracing
- Custom business metrics in Prometheus/Grafana
- Advanced dispute chat/messaging
