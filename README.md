# CureCart — Medicine Inventory & Ordering System

A full-stack online medicine store. Customers browse a medicine catalogue, manage a cart and
delivery addresses, and place orders; administrators manage inventory and advance order status.

| Layer | Technology |
|---|---|
| Backend | Spring Boot 3.5.5, Java 21, Spring Security + JWT, Spring Data JPA |
| Frontend | Angular 18 (standalone components), RxJS |
| Database | MySQL 8 |
| API tests | Cucumber 7 + TestNG + REST Assured |
| UI tests | Cucumber 7 + TestNG + Selenium 4 (Page Object Model) |
| Docs | springdoc-openapi (Swagger UI) |

---

## Features

**Customer**
- Register and sign in (JWT-based, 10-hour tokens)
- Browse and search the medicine catalogue, filter by composition/category
- Add to cart with inline quantity stepper; cart persists server-side
- Save multiple delivery addresses and mark one as default
- Place an order (5% tax applied) and review order history

**Administrator**
- Add, edit and delete medicines, with low-stock highlighting
- View all orders across all customers
- Advance order status (`PENDING → SHIPPED / DELIVERED / CANCELLED`)

---

## Project structure

```
.
├── backend/                     # Spring Boot API (Maven artifact com.example:Meds)
│   └── src/
│       ├── main/java/com/example/Meds/
│       │   ├── controller/      # REST endpoints (DTO in, DTO out)
│       │   ├── service/ + impl/ # Business logic
│       │   ├── repository/      # Spring Data JPA interfaces
│       │   ├── entity/          # JPA entities
│       │   ├── dto/             # Request + response DTOs
│       │   ├── mapper/          # Hand-written entity <-> DTO mappers
│       │   ├── security/        # JwtUtil, JwtRequestFilter, OwnershipValidator
│       │   ├── config/          # SecurityConfig, WebConfig
│       │   └── exception/       # Custom exceptions + GlobalExceptionHandler
│       └── test/java/com/example/
│           ├── api/             # REST Assured suite (context, runners, steps, utils)
│           └── ui/              # Selenium suite (context, pages, runners, steps)
├── frontend/                    # Angular 18 SPA (medical-inventory-dashboard)
│   └── src/app/
│       ├── auth/                # signin, signup
│       ├── user/                # browse-medicines, cart, delivery-info, my-profile, ...
│       ├── admin/               # medicines-list, manage-medicines, admin-orders
│       ├── services/            # cart.service, address.service
│       └── auth.service.ts, auth.guard.ts, app.routes.ts
└── sql/db-setup.sql             # Schema + seed data
```

---

## Prerequisites

- JDK 21
- Node.js 18+ and npm
- MySQL 8 running on `localhost:3306`
- Google Chrome (only for the UI test suite)

---

## Getting started

### 1. Database

```bash
mysql -u root -p < sql/db-setup.sql
```

Creates the `meds2` schema and seeds 6 users, 19 medicines, addresses and sample orders.

> Note: the backend runs with `spring.jpa.hibernate.ddl-auto=update`, so Hibernate will also
> evolve the schema at startup. `sql/db-setup.sql` is the starting point, not a migration tool.

### 2. Backend

```bash
cd backend
./mvnw spring-boot:run
```

Runs on **http://localhost:8099**.

Swagger UI is available at `/swagger-ui.html`, but note that the security rules end with
`anyRequest().authenticated()` and springdoc's paths are not whitelisted — so the docs
endpoints require a valid token too.

### 3. Frontend

```bash
cd frontend
npm install
npm start
```

Runs on **http://localhost:4200**. CORS on the backend is configured for exactly this origin.

---

## Configuration

All settings live in `backend/src/main/resources/application.properties` and can be overridden
with environment variables:

| Variable | Default | Purpose |
|---|---|---|
| `MEDS_DB_URL` | `jdbc:mysql://localhost:3306/meds2` | JDBC connection string |
| `MEDS_DB_USERNAME` | `root` | Database user |
| `MEDS_DB_PASSWORD` | `root` | Database password |
| `MEDS_JWT_SECRET` | *(empty)* | Base64-encoded HS256 signing key, 256 bits minimum |

**`MEDS_JWT_SECRET` must be set for any deployed instance.** When it is empty, the application
generates a random signing key at startup and logs a warning — convenient for local development,
but it means every restart invalidates previously issued tokens, so an open browser session will
start returning 401s until the user signs in again.

Generate a suitable key with:

```bash
openssl rand -base64 32
```

---

## Demo credentials

From `sql/db-setup.sql`:

| Role | Email | Password |
|---|---|---|
| Admin | `admin@meds.com` | `AdminPass123!` |
| User | `papa.jones@meds.com` | `UserPass123!` |
| User | `jane.smith@meds.com` | `UserPass123!` |

Remaining seeded users (`sam.brown@`, `alice.ray@`, `bob.lee@`) also use `UserPass123!`.

---

## API reference

Resources owned by a user are nested under that user. All endpoints require a
`Authorization: Bearer <token>` header except where noted.

| Method | Endpoint | Access |
|---|---|---|
| `POST` | `/api/auth/login` | Public |
| `POST` | `/api/users/register` | Public |
| `GET` | `/api/users` | Admin |
| `GET` `PUT` | `/api/users/{id}` | Owner or admin |
| `GET` | `/api/medicines`, `/api/medicines/{id}` | Authenticated |
| `POST` `PUT` `DELETE` | `/api/medicines`, `/api/medicines/{id}` | Admin |
| `GET` `POST` | `/api/users/{userId}/addresses` | Owner or admin |
| `PUT` | `/api/users/{userId}/addresses/{addressId}/default` | Owner or admin |
| `DELETE` | `/api/users/{userId}/addresses/{addressId}` | Owner or admin |
| `GET` `DELETE` | `/api/users/{userId}/cart` | Owner or admin |
| `POST` | `/api/users/{userId}/cart/items` | Owner or admin |
| `PUT` `DELETE` | `/api/users/{userId}/cart/items/{itemId}` | Owner or admin |
| `GET` `POST` | `/api/users/{userId}/orders` | Owner or admin |
| `GET` | `/api/users/{userId}/orders/{orderId}` | Owner or admin |
| `PUT` | `/api/users/{userId}/orders/{orderId}/status?status=` | Admin |
| `GET` | `/api/admin/orders` | Admin |

### Authentication flow

1. `POST /api/auth/login` with `{ email, password }` returns `{ token, user }`.
2. The client sends `Authorization: Bearer <token>` on every subsequent request.
3. `JwtRequestFilter` validates the token, loads the user by email, and populates the
   security context. The user's **role is not stored in the token** — it is re-read from the
   database on every request.
4. `OwnershipValidator` additionally confirms that the `{userId}` in the path belongs to the
   authenticated caller (admins bypass this), so one user cannot read or modify another's data.

Failures return `401` for missing/invalid authentication and `403` for insufficient permission,
with a JSON body of `{ status, message, timestamp }`.

---

## Testing

Both suites are Cucumber + TestNG and live inside the backend module. They run against a
**live application**, so start the services first.

```bash
# API suite requires: backend + MySQL running
# UI suite requires:  backend + MySQL + frontend + Chrome
cd backend
./mvnw test
```

| Suite | Runner | Features | Step definitions |
|---|---|---|---|
| API | `com.example.api.runners.APIRunner` | `src/test/resources/features/api` | `com.example.api.stepdefinitions` |
| UI | `com.example.ui.runners.TestRunner` | `src/test/resources/features/ui` | `com.example.ui.stepdefinitions` |

Run a single runner or filter by tag:

```bash
./mvnw test -Dtest=APIRunner
./mvnw test -Dcucumber.filter.tags="@cart"
```

Available API tags: `@api`, `@auth`, `@user`, `@medicine`, `@address`, `@cart`, `@order`, `@admin`.
Feature files are numbered (`01_auth` … `06_order`) to express the intended progression.

**Notes on the suites**
- Scenarios run in parallel; shared state is held in a `ThreadLocal` `APITestContext` keyed by a
  `ContextKeys` enum. Put scenario data there, never in step-class instance fields.
- Isolation relies on unique emails generated from `System.nanoTime()` rather than database
  cleanup, so test runs accumulate rows.
- The admin scenarios require the seeded `admin@meds.com` account to exist.
- UI reports are written to `target/cucumber-reports/`; failing scenarios attach a screenshot.
- A few UI scenarios are tagged `@ignore` because they depend on specific seed data.
- Headless mode is available by uncommenting `--headless=new` in
  `src/test/java/com/example/ui/context/WebDriverManager.java`.

---

## Known limitations

This is a learning/demo project and has gaps that matter before any real deployment —
notably that stock is never decremented when an order is placed, order items do not snapshot
the purchase price (historical orders re-price when a medicine's price changes), and there are
no unit or integration tests, only the end-to-end suites above.

See **[learnings.md](learnings.md)** for a detailed walkthrough of the architecture, the
conventions to follow when changing the code, and a catalogue of known issues.
