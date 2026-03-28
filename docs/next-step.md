# Next Step: Step 9 — Frontend: Deal Flow UI

## Goal
Users can create, view, fund, deliver, and confirm escrow deals through the web UI.

## Tasks
1. Deal API client functions (`lib/api.ts`) — CRUD + state transition calls
2. Deal list page (`/deals`) — table/cards showing user's deals with status badges
3. Create deal page (`/deals/new`) — form: title, description, amount, beneficiary email
4. Deal detail page (`/deals/[id]`) — full deal info, action buttons based on role + status
5. State-aware action buttons:
   - Depositor: Fund (CREATED), Confirm (DELIVERED), Cancel
   - Beneficiary: Deliver (FUNDED)
6. Navigation update — add "Deals" link to navbar
7. Tailwind styling for all deal components

## Verification
```bash
cd frontend && npm run build     # builds successfully
cd frontend && npm run lint      # no errors
# Manual: create deal → fund → deliver → confirm → see COMPLETED status
```
