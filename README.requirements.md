# Требования (Requirements)

## Роли
- депонент (depositor)
- бенефициар (beneficiary)
- администратор (administrator)
- оператор (operator)

## MVP сценарии
1. Создание сделки эскроу (escrow deal).
2. Согласование условий (agreement).
3. Открытие счёта (account opened).
4. Внесение средств (funding/deposit).
5. Исполнение бенефициаром (fulfillment).
6. Проверка депонентом (review).
7. Разрешение: раскрытие/возврат/спор (release/refund/dispute).

## Машины состояний
### Сделка
DRAFT → AWAITING_AGREEMENT → AGREED → ACCOUNT_OPENED → AWAITING_FUNDING → FUNDING_PROCESSING → FUNDS_SECURED → AWAITING_BENEFICIARY_FULFILLMENT → AWAITING_DEPOSITOR_REVIEW → RELEASE_PENDING/REFUND_PENDING/DISPUTED → RELEASED/REFUNDED → CLOSED.

### Счёт эскроу
NOT_CREATED → OPENED → AWAITING_DEPOSIT → DEPOSIT_IN_PROCESS → HELD_IN_ESCROW → RELEASE_AUTHORIZED/REFUND_AUTHORIZED/ON_DISPUTE_HOLD → RELEASED_TO_BENEFICIARY/REFUNDED_TO_DEPOSITOR → CLOSED.
