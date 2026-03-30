# Next Step: Step 10 — Notification Service

## Goal
Kafka consumer that listens to deal-events topic and stores in-app notifications for users.

## Tasks
1. Add Spring Kafka + Spring Data JPA dependencies to notification-service
2. Create Notification entity (id, userId, type, title, message, dealId, read, createdAt)
3. Flyway V1 migration for notifications table
4. Kafka consumer: listen to "deal-events" topic, create notifications for relevant users
5. REST endpoints: GET /api/notifications (list), PATCH /api/notifications/{id}/read (mark read)
6. Wire up frontend /notifications page to display real notifications
7. Update notification bell in top bar to show unread count

## Verification
```bash
./gradlew :notification-service:compileJava
./gradlew build -x test
cd frontend && npm run build
# Manual: create deal → see notification appear for beneficiary
```
