# Next Step: Step 4 — API Gateway: Routing and JWT Validation

## Goal
Single entry point at :8080 that validates JWT tokens and routes to downstream services.

## Tasks
1. Configure Spring Cloud Gateway routes:
   - `/api/auth/**` → auth-service:8081
   - `/api/deals/**` → deal-service:8082
   - `/api/payments/**` → payment-service:8083
   - `/api/notifications/**` → notification-service:8084
2. Add JWT validation filter (extract claims, forward `X-User-Id` and `X-User-Role` headers)
3. CORS configuration for `localhost:3000`
4. Add shared JWT secret config
5. Public routes bypass JWT filter (auth endpoints, actuator)

## Verification
```bash
./gradlew :api-gateway:build
# With auth-service + gateway running:
curl localhost:8080/api/auth/login ...     # passes through to auth-service
curl localhost:8080/api/deals             # 401 without token
curl -H "Authorization: Bearer <token>" localhost:8080/api/deals  # routes with X-User-Id header
```

## Expected Outcome
- Gateway routes all `/api/**` requests to correct downstream services
- JWT validated at gateway level; X-User-Id and X-User-Role forwarded
- Unauthenticated requests to protected routes get 401
- CORS allows frontend origin
