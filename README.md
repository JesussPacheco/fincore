# FinCore — Digital Banking Platform

> A production-grade microservices backend for a digital banking core, built with Java 21, Spring Boot 3, Apache Kafka, and AWS. Demonstrates real-world fintech engineering patterns including Saga choreography, idempotency, distributed locking, and event-driven architecture.

---

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Microservices](#microservices)
- [Key Engineering Decisions](#key-engineering-decisions)
- [Getting Started](#getting-started)
- [API Reference](#api-reference)
- [Kafka Topics & Events](#kafka-topics--events)
- [Transfer Flow — Saga Choreography](#transfer-flow--saga-choreography)
- [Running Tests](#running-tests)
- [Deployment — AWS](#deployment--aws)
- [Roadmap](#roadmap)

---

## Overview

FinCore is a digital banking core that allows users to:

- Register and authenticate with JWT RS256
- Create savings and checking accounts in PEN or USD
- Transfer money between accounts with full consistency guarantees
- Query account balances with ownership validation


---

## Architecture

```
                          ┌─────────────────────────────────────────┐
                          │              AWS VPC                     │
                          │                                          │
Client ──HTTPS──► ALB ──► │  API Gateway :8080                      │
                          │  JWT · Rate Limiting · Routing           │
                          │         │                                │
                          │    ┌────┴──────────────────────┐         │
                          │    │                           │         │
                          │  Auth :8081        Account :8082         │
                          │  Register          Create account        │
                          │  Login JWT         Get balance           │
                          │  RS256             Debit / Credit        │
                          │                        ▲                 │
                          │                    HTTP/Feign            │
                          │                        │                 │
                          │             Transaction :8083            │
                          │             Saga · Idempotency           │
                          │             Distributed Lock             │
                          │                        │                 │
                          │            ┌───────────▼──────────┐      │
                          │            │    Apache Kafka       │      │
                          │            │    Event Bus          │      │
                          │            └───────────────────────┘      │
                          │                                          │
                          │  PostgreSQL · Redis · Secrets Manager    │
                          └─────────────────────────────────────────┘
```

**Patterns applied:**

| Pattern | Where | Why |
|---|---|---|
| Event-Driven Architecture | Kafka between services | Temporal decoupling, resilience |
| Saga Choreography | Transfer flow | Distributed transactions without 2PC |
| Hexagonal Architecture | Every service | Domain isolation from frameworks |
| Database per Service | 4 isolated PostgreSQL DBs | Service autonomy |
| Idempotency Keys | Transfer endpoint | Prevent double debit on retry |
| Distributed Lock | Before transfer initiation | Prevent race conditions |
| Circuit Breaker | OpenFeign calls | Fault tolerance |
| Optimistic Locking | Account balance | Concurrent update protection |

---

## Tech Stack

| Category | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.3 |
| Gateway | Spring Cloud Gateway (WebFlux) |
| Messaging | Apache Kafka 3.7 |
| Database | PostgreSQL 15 |
| Cache & Locks | Redis 7 + Redisson |
| HTTP Client | OpenFeign + Resilience4j |
| Auth | JWT RS256 (JJWT 0.12) |
| DB Migrations | Flyway |
| Build | Gradle 9 (monorepo) |
| Containers | Docker + Docker Compose |
| Observability | Prometheus + Grafana + Zipkin |
| Cloud | AWS ECS Fargate + RDS + ElastiCache + MSK |
| CI/CD | GitHub Actions → ECR → ECS |

---

## Microservices

### API Gateway — Port 8080

Single public entry point. Validates JWT RS256 using RSA public key. Extracts `userId` and `role` from token claims and propagates them as internal headers (`X-User-Id`, `X-User-Role`). Applies token bucket rate limiting per user via Redis.

Downstream services never validate JWT — they trust the headers from the gateway.

### Auth Service — Port 8081

Handles user identity. Registers users with BCrypt-hashed passwords. On login, generates a JWT signed with RSA private key. The private key never leaves this service.

### Account Service — Port 8082

Manages account lifecycle and balances. Uses `@Version` (optimistic locking) on the balance field. Exposes internal HTTP endpoints for Transaction Service to validate and modify balances. Also acts as a Kafka consumer — listens to `fincore.transfer.initiated` to execute debits as part of the Saga.

### Transaction Service — Port 8083

Orchestrates the transfer Saga. On each transfer request:

1. Checks Redis for duplicate idempotency key
2. Acquires a distributed lock on the source account via Redisson
3. Validates balance via synchronous HTTP call to Account Service
4. Publishes `TransferInitiated` to Kafka and responds `202 Accepted`
5. Consumes `AccountDebited` → credits target account → marks transfer `COMPLETED`
6. Consumes `AccountDebitFailed` → marks transfer `CANCELLED`

---

## Key Engineering Decisions

### Why Saga Choreography over Orchestration?

An orchestrator is a single point of failure and introduces tight coupling. With choreography, each service reacts to Kafka events independently. If Transaction Service is down, Account Service still processes events from its queue — nothing is lost, only delayed.

### Why two concurrency mechanisms?

**Distributed lock** (Redisson, before DB): prevents two instances of Transaction Service from both passing balance validation simultaneously for the same account. Works at the application layer across service instances.

**Optimistic locking** (`@Version`, at DB): prevents two JPA operations from overwriting each other at the database level. Last line of defense.

They solve different failure scenarios — both are needed in a multi-instance deployment.

### Why `@Modifying` JPQL UPDATE for debit/credit?

JPA's EntityManager caches entities per session. When an HTTP thread loads an account entity and a Kafka consumer thread simultaneously tries to save a different instance of the same entity (same UUID), JPA throws `NonUniqueObjectException`. A direct `UPDATE` statement bypasses the JPA cache entirely, which is the correct pattern for Kafka consumers that modify entities previously loaded in another session.

### Why JWT RS256 over HS256?

HS256 requires all verifying services to have the same secret key — if any service is compromised, an attacker could forge tokens. RS256: only Auth Service holds the RSA private key for signing. All other services have only the public key for verification. They can verify tokens but cannot create them.

### Why `REQUIRES_NEW` propagation in Kafka consumer handlers?

When a `@Transactional` method fails, Spring marks the entire transaction as `rollback-only`. If a parent transaction calls a failing child, the parent is also marked for rollback — even if you catch the exception. `REQUIRES_NEW` creates a completely independent transaction. Failure in the consumer doesn't contaminate any outer transaction context.

---

## Getting Started

### Prerequisites

- Java 21
- Docker Desktop
- Gradle 9

### 1. Clone the repository

```bash
git clone https://github.com/JesussPacheco/FinCore.git
cd fincore
```

### 2. Generate RSA keys for JWT

```bash
mkdir -p auth-service/src/main/resources/keys
cd auth-service/src/main/resources/keys

openssl genrsa -out private.pem 2048
openssl rsa -in private.pem -pubout -out public.pem

# Copy public key to services that verify tokens
cp public.pem ../../../../../../api-gateway/src/main/resources/keys/
cp public.pem ../../../../../../account-service/src/main/resources/keys/
cp public.pem ../../../../../../transaction-service/src/main/resources/keys/
```

### 3. Start infrastructure

```bash
# Stop local PostgreSQL if running (port conflict)
sudo systemctl stop postgresql

# Start all required infrastructure
docker-compose up -d postgres redis zookeeper kafka kafka-ui
```

Verify everything is healthy:

```bash
docker-compose ps
```

All containers should show `Up (healthy)`.

### 4. Start services

Open four terminals and run each service:

```bash
# Terminal 1
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
./gradlew auth-service:bootRun

# Terminal 2
./gradlew account-service:bootRun

# Terminal 3
./gradlew transaction-service:bootRun

# Terminal 4
./gradlew api-gateway:bootRun
```

### 5. Verify

```bash
curl http://localhost:8080/actuator/health
```

---

## API Reference

All requests go through the API Gateway at `http://localhost:8080`.

### Auth

#### Register

```http
POST /api/v1/auth/register
Content-Type: application/json

{
  "name": "Jesus Pacheco",
  "email": "jesus@fincore.com",
  "password": "SecurePass123!"
}
```

Response `201 Created`:
```json
{
  "userId": "97d2691d-d4f5-474b-b263-f4ed079680cf",
  "email": "jesus@fincore.com",
  "name": "Jesus Pacheco"
}
```

#### Login

```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "jesus@fincore.com",
  "password": "SecurePass123!"
}
```

Response `200 OK`:
```json
{
  "accessToken": "eyJhbGciOiJSUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 900
}
```

### Accounts

#### Create Account

```http
POST /api/v1/accounts
Authorization: Bearer {token}
Content-Type: application/json

{
  "type": "SAVINGS",
  "currency": "PEN"
}
```

Response `201 Created`:
```json
{
  "id": "c60b49f1-e33a-46dd-a61d-86ce45a4d9ed",
  "accountNumber": "ACC-00000000001",
  "type": "SAVINGS",
  "currency": "PEN",
  "balance": 0.00,
  "status": "ACTIVE",
  "createdAt": "2026-04-06T22:00:00"
}
```

#### Get Balance

```http
GET /api/v1/accounts/{accountId}/balance
Authorization: Bearer {token}
```

Response `200 OK`: same structure as above with current balance.

**Ownership check:** returns `403 Forbidden` if the authenticated user does not own the account.

### Transfers

#### Initiate Transfer

```http
POST /api/v1/transfers
Authorization: Bearer {token}
Content-Type: application/json
X-Idempotency-Key: {uuid-unique-per-operation}

{
  "sourceAccountId": "c60b49f1-e33a-46dd-a61d-86ce45a4d9ed",
  "targetAccountId": "d72e1908-6e89-4c77-b7c5-39a130ff85d6",
  "amount": 100.00,
  "currency": "PEN"
}
```

Response `202 Accepted`:
```json
{
  "transferId": "d8897b3a-1061-40aa-be38-d7a6a1950b70",
  "sourceAccountId": "c60b49f1-...",
  "targetAccountId": "d72e1908-...",
  "amount": 100.00,
  "currency": "PEN",
  "status": "PENDING",
  "createdAt": "2026-04-06T22:00:00"
}
```

`202` means the transfer was accepted and is being processed asynchronously. The final status (`COMPLETED` or `CANCELLED`) is set via Kafka after the Saga completes.

**Idempotency:** retrying the same request with the same `X-Idempotency-Key` returns the original response without processing the transfer again.

### Error Responses

All errors follow a consistent format:

```json
{
  "timestamp": "2026-04-06T22:00:00",
  "status": 422,
  "error": "INSUFFICIENT_FUNDS",
  "message": "Source account does not have sufficient funds"
}
```

| HTTP Status | Error Code | Cause |
|---|---|---|
| 400 | VALIDATION_ERROR | Invalid request body |
| 401 | UNAUTHORIZED | Missing or invalid JWT |
| 403 | UNAUTHORIZED_ACCOUNT_ACCESS | Account belongs to another user |
| 404 | ACCOUNT_NOT_FOUND | Account does not exist |
| 409 | DUPLICATE_TRANSFER | Same idempotency key already used |
| 409 | TRANSFER_LOCK_UNAVAILABLE | Account has a transfer in progress |
| 422 | INSUFFICIENT_FUNDS | Not enough balance |
| 500 | INTERNAL_ERROR | Unexpected server error |

---

## Kafka Topics & Events

| Topic | Published by | Consumed by | Trigger |
|---|---|---|---|
| `fincore.user.registered` | Auth Service | Notification, Audit | User registers |
| `fincore.account.created` | Account Service | Audit | Account created |
| `fincore.transfer.initiated` | Transaction Service | Account Service, Audit | Transfer starts |
| `fincore.account.debited` | Account Service | Transaction Service, Audit | Debit succeeds |
| `fincore.account.debit.failed` | Account Service | Transaction Service, Audit | Debit fails |
| `fincore.transfer.completed` | Transaction Service | Notification, Audit | Transfer succeeds |
| `fincore.transfer.cancelled` | Transaction Service | Notification, Audit | Transfer fails |

View events live at `http://localhost:8090` (Kafka UI).

---

## Transfer Flow — Saga Choreography

The transfer is the most complex flow. It mixes synchronous HTTP and asynchronous Kafka communication:

```
1. Client sends POST /transfers with X-Idempotency-Key
2. Transaction Service checks Redis → key not found → continue
3. Acquires distributed lock: lock:account:{sourceAccountId} (TTL 10s)
4. HTTP GET to Account Service → validates balance is sufficient
5. Saves Transfer{status=PENDING} in transaction_db
6. Publishes TransferInitiated → Kafka
7. Releases lock
8. Returns 202 Accepted to client

── Asynchronous from here ──

9.  Account Service consumes TransferInitiated
10. Executes direct SQL UPDATE: balance = balance - amount
11. Publishes AccountDebited → Kafka

12. Transaction Service consumes AccountDebited
13. HTTP POST to Account Service → credits target account
14. Updates Transfer status to COMPLETED (direct SQL UPDATE)
15. Saves idempotency key in Redis (TTL 24h)
16. Publishes TransferCompleted → Kafka

── Compensation (if step 10 fails) ──

17. Account Service publishes AccountDebitFailed
18. Transaction Service consumes AccountDebitFailed
19. Updates Transfer status to CANCELLED
20. Publishes TransferCancelled
```

---

## Running Tests

> Tests are currently being added. This section will be updated as coverage increases.

```bash
# Run all tests
./gradlew test

# Run tests for a specific service
./gradlew auth-service:test
./gradlew account-service:test
./gradlew transaction-service:test
```

Tests use Testcontainers for integration tests — PostgreSQL, Kafka, and Redis spin up as real Docker containers during the test run.

---

## Deployment — AWS

> This section will be updated when the AWS deployment is complete.

### Infrastructure

```
VPC (private subnets)
├── ALB → HTTPS (ACM certificate)
├── ECS Fargate (one task per service)
├── RDS PostgreSQL (db.t3.micro, Multi-AZ)
├── ElastiCache Redis (cache.t3.micro)
├── Amazon MSK (Kafka managed)
└── AWS Secrets Manager (RSA keys, DB credentials)
```

### CI/CD Pipeline

```
Push to main
    → GitHub Actions
    → Build + Tests
    → Docker build
    → Push to Amazon ECR
    → Deploy to ECS Fargate (task definition update)
    → Health check via /actuator/health
```

---

## Roadmap

- [x] Auth Service — register, login, JWT RS256
- [x] Account Service — create account, get balance, debit/credit
- [x] Transaction Service — Saga choreography, idempotency, distributed lock
- [x] API Gateway — JWT validation, rate limiting, routing
- [ ] Prometheus metrics + Grafana dashboards
- [ ] Zipkin distributed tracing
- [ ] AWS deployment (ECS Fargate, RDS, ElastiCache, MSK)
- [ ] GitHub Actions CI/CD pipeline
- [ ] Config Server connected to all services

---

## Author

**Jesús Pacheco** — Backend Java Developer
- GitHub: [JesussPacheco](https://github.com/JesussPacheco)
- Stack: Java · Spring Boot · Kafka · PostgreSQL · Redis · AWS