# Next Step: Step 8 — Frontend: Authentication Pages

## Goal
Users can register and login via the web UI. JWT stored, protected routes, dashboard skeleton.

## Tasks
1. Create API client module (`lib/api.ts`) — talks to gateway at localhost:8080
2. Auth context/provider (`contexts/auth-context.tsx`) — JWT storage, user state
3. Pages:
   - `/login` — email/password form, login call, redirect to dashboard
   - `/register` — email/password/fullName/role form, register call
   - `/dashboard` — skeleton with user info, empty deal list placeholder
4. Protected route middleware (redirect to /login if no token)
5. Layout with navigation bar (logo, user menu, logout)
6. Tailwind styling for all components

## Verification
```bash
cd frontend && npm run build     # builds successfully
cd frontend && npm run dev       # starts at :3000
# Manual: register → login → see dashboard → logout → redirect to /login
```
