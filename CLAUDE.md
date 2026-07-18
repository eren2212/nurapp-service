# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this repo is

Backend for **nurapp** — a Muslim spiritual-companion app (product brief: `urun-brief.md`). Pragmatic Spring Boot **4.1** (Java 17 target, Maven) microservices: an API **gateway** plus three backing services (**user**, **content**, **subscription**). Spring Cloud **2025.1.2**.

The React Native frontend lives in `nurapp-mobile/` — a **separate git repo** (gitignored here) with its own `CLAUDE.md`/`AGENTS.md`. Don't edit it as part of backend work.

## Architecture (the parts that span files)

**Local-first by design.** Spiritual data (prayer/dhikr/journal/streak/companion) stays on the device. The server only holds: account identity, user preferences, subscription entitlement, and multilingual content. Don't add server-side storage of spiritual data.

**Gateway is the only public entry.** `gateway` (Spring Cloud Gateway, WebFlux) listens on container `8080`, published on the host at `127.0.0.1:8090`, and exposed publicly via a cloudflared tunnel at `nurapp.ereniridere.xyz`. Route predicates map path → service:
- `/auth/**`, `/users/**` → user-service (`8081`)
- `/content/**` → content-service (`8082`)
- `/subscriptions/**` → subscription-service (`8083`)

**Centralized auth at the gateway.** `AuthGlobalFilter` (gateway) validates the JWT, always strips any client-supplied `X-User-Id`, and injects a trusted `X-User-Id` for backing services. Public paths: `/auth/**` (except `/auth/upgrade`), `/content/**`, `/actuator/**`, `/users/ping`, `/subscriptions/webhook`; everything else requires `Authorization: Bearer`. Consequently, backing services do **not** re-validate the JWT:
- user-service has Spring Security whose principal is the `UUID` derived from `X-User-Id` (via a `HeaderAuthFilter`); controllers read `@AuthenticationPrincipal UUID`.
- content-service and subscription-service have **no** Spring Security; they read `@RequestHeader("X-User-Id")` directly.

**Auth model.** Anonymous device identity first: `POST /auth/device` with an optional `deviceId` returns the same `userId` on repeat calls (identity keyed on `provider="device"` + `deviceId`). Optional upgrade to email/password (`POST /auth/upgrade`, protected, BCrypt) and `POST /auth/login`, both resolving to the same user. JWT via jjwt 0.12.6 (HS512, `JWT_SECRET`). Refresh tokens rotate: `POST /auth/token/refresh` consumes the old and issues a new pair.

**Database-per-service on a shared Postgres.** `users_db` / `content_db` / `subscriptions_db` live in one shared `postgres` (PostGIS) container on the external `backend` Docker network. Each service connects with its own role (`users_svc` / `content_svc` / `subscriptions_svc`). Schema is owned by **Flyway** (`src/main/resources/db/migration/V*.sql`) with JPA `ddl-auto: validate` — entities must match migrations, add columns via a new `V*` file. **Databases and roles are provisioned externally on the server; nothing in this repo creates them** (no `CREATE DATABASE`/`CREATE ROLE`).

**Errors: RFC 7807 Problem Details** everywhere (`spring.mvc.problemdetails.enabled` + `@RestControllerAdvice`).

**Subscriptions come from RevenueCat.** `POST /subscriptions/webhook` is public but authenticated by a shared secret (`REVENUECAT_WEBHOOK_SECRET`) and is idempotent; it upserts entitlements. `GET /subscriptions/me` returns premium status for the current user.

Package convention: `com.nurapp.<service>` with subpackages like `domain` / `auth` / `web` / `config` (user) or `service` / `web` / `webhook` (content, subscription).

## Version-specific gotchas (easy to get wrong)

- Gateway routes go under `spring.cloud.gateway.server.webflux.routes` (Spring Cloud 2025.x), **not** the old `spring.cloud.gateway.routes`. Starter is `spring-cloud-starter-gateway-server-webflux`.
- Boot 4.1's servlet web starter is `spring-boot-starter-webmvc` (not `-web`).
- In user-service Spring Security, `/error` must be `permitAll` — otherwise business-rule exceptions surface as `403` instead of their intended status (e.g. `401`/`409`).
- Containers build and run on Temurin **21** (see Dockerfiles) even though `java.version` targets 17.

## Commands

Run the whole stack (server, or locally against a reachable Postgres):
```bash
docker network create backend   # once, if the external network doesn't exist
cp .env.example .env             # then fill real values (not committed)
docker compose up --build -d
docker logs -f nurapp-gateway
```

Per-service Maven (run inside the service directory, e.g. `cd user-service`):
```bash
./mvnw clean package                       # build + tests
./mvnw test                                # tests only
./mvnw test -Dtest=ClassName#methodName    # a single test
./mvnw spring-boot:run                     # run one service locally (needs env vars + Postgres)
```
Note: `subscription-service` has no Maven wrapper — use a system `mvn` there.

Smoke-test the public API through the gateway:
```bash
curl -X POST http://127.0.0.1:8090/auth/device          # anonymous session (public)
curl http://127.0.0.1:8090/users/me -H "Authorization: Bearer <accessToken>"
```

## Deploy workflow

Write locally → `git push` → on the server (`deploy@evsunucu`, repo at `/srv/projects/nurapp`) `git pull` + `docker compose up --build -d`. `.env` is never committed; it is maintained by hand on the server. All services join the external `backend` network and reach shared infra (`postgres`, `redis`, `minio`, `cloudflared`) by container name.
