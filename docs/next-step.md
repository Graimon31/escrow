# Next Step: Step 11 — Operator + Admin Roles

## Goal
Add operator and administrator functionality: operator dashboard for managing deals/disputes, admin panel for user management.

## Tasks
1. Operator dashboard: list all deals, filter by status, view any deal detail
2. Operator actions: assign operator to deal, resolve disputes
3. Admin user management: list users, change roles, deactivate accounts
4. Role-based route guards in frontend (operator/admin pages)
5. Backend authorization checks (operator/admin-only endpoints)
6. Navigation updates: show operator/admin menu items based on role

## Verification
```bash
./gradlew build -x test
cd frontend && npm run build
# Manual: login as operator → see operator dashboard, manage disputes
# Manual: login as admin → see admin panel, manage users
```
