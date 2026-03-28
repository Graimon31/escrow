# Next Step: Step 6 — Payment Service: Mock Accounts and Escrow Hold

## Goal
Mock payment system with user accounts and escrow hold/release operations.

## Tasks
1. Add dependencies: Spring Data JPA, Flyway, PostgreSQL, Validation
2. Flyway V1: `accounts`, `transactions`, `escrow_holds` tables in `payments` schema
3. Entities: Account (auto-created, 10000 RUB balance), Transaction, EscrowHold
4. PaymentService: getOrCreateAccount, holdFunds, releaseFunds, refundFunds
5. PaymentController:
   - `GET /api/payments/account` — my account balance
   - `GET /api/payments/transactions` — my transaction history
6. Internal endpoints for deal-service integration (hold/release)
7. Pessimistic locking on account balance updates
8. Integration tests with Testcontainers

## Verification
```bash
./gradlew :payment-service:compileJava
./gradlew :payment-service:compileTestJava
./gradlew build -x test
```
