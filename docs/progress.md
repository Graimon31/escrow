# Прогресс реализации (Progress)

## Шаги 1-6
Статус: выполнены (каркас, infra, auth, deal/escrow, funding event-driven, fulfillment/review).

## Шаг 7 — Resolution/Release/Refund/Dispute
Статус: выполнен.

Сделано:
- реализован `resolution-service`:
  - принятие финального исхода `RELEASE | REFUND`;
  - хранение истории решений;
  - публикация Kafka-событий `FUNDS_RELEASED | FUNDS_REFUNDED`;
- реализован `dispute-service`:
  - открытие спора;
  - хранение истории споров;
  - автоматическое закрытие OPEN-спора по resolution-событию;
- `deal-service` расширен финальными переходами: `RELEASED` / `REFUNDED`;
- `escrow-account-service` расширен финальными переходами: `RELEASED_TO_BENEFICIARY` / `REFUNDED_TO_DEPOSITOR`;
- добавлены UI-экраны:
  - `/dispute/{dealId}`;
  - `/outcome/{dealId}`;
- обновлены infra/docs/tests.

Проверены сценарии:
- release;
- refund;
- dispute.
