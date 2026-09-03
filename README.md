# Sunrise Dental Clinic - Appointment & Patient Management System

A Java web application (Servlets + JSP + JDBC + MySQL) built for CIS6003
Advanced Programming, replacing Sunrise Dental Clinic's paper-based
appointment process.

## Features

1. **Login** - staff must authenticate; every other page is protected by `AuthFilter`.
2. **Register New Appointment** - captures patient + appointment details and generates a unique appointment number.
3. **Display Appointment Details** - look up an appointment by number.
4. **Calculate & Print Bill** - computes consultation + treatment fee (with an optional insurance discount) and produces a printable receipt.
5. **Help** - in-app step-by-step usage guide.
6. **Exit** - logout safely ends the session.
7. **JSON web service** - `GET /api/appointments?number=APT-...` returns appointment data as JSON for external/distributed clients.

## Requirements

- JDK 11+
- Apache Maven
- Apache Tomcat 9 (Servlet 4.0 / JSP)
- MySQL 8.x

## Setup

1. Create the database and seed data:
   ```
   mysql -u root -p < sql/schema.sql
   ```
2. Update `src/main/resources/db.properties` with your local MySQL username/password.
3. Build the WAR:
   ```
   mvn clean package
   ```
4. Deploy `target/sunrise-dental-clinic.war` to Tomcat 9 (or copy it into `webapps/`).
5. Open `http://localhost:8080/sunrise-dental-clinic/` and log in with:
   - Username: `admin`
   - Password: `admin123`

## Running the tests

```
mvn test
```

`AppointmentServiceTest` and `BillingStrategyTest`/`ValidationUtilTest` cover
the business-logic classes using JUnit 5 and Mockito, without needing a live
database (DAOs are mocked).

## Architecture

```
Servlet (Controller)  ->  Service  ->  DAO (interface)  ->  DAOImpl (JDBC)  ->  MySQL
JSP (View)             /
```

- **model** - plain data classes (`Patient`, `Dentist`, `Treatment`, `Appointment`, `Bill`, `User`).
- **dao / dao.impl** - one interface + one JDBC implementation per table.
- **service** - business logic, orchestrates DAOs, sits between servlets and persistence.
- **servlet** - HTTP controllers; thin, delegate to services.
- **filter** - `AuthFilter` enforces "authorized staff only".
- **webapp/*.jsp** - views, styled with `css/style.css`.

## Design patterns used

| Pattern | Where | Why |
|---|---|---|
| **MVC** | Servlets (controller) / JSP (view) / model classes | Standard separation of concerns for a web app. |
| **DAO** | `dao` package | Isolates SQL from business logic; each entity has its own DAO. |
| **Singleton** | `DBConnection` | One place loads `db.properties` and the JDBC driver; avoids repeated setup. |
| **Factory** | `DAOFactory`, `BillingStrategyFactory` | Callers depend on interfaces, not concrete classes; swapping implementations later doesn't ripple through the codebase. |
| **Strategy** | `BillingStrategy` / `StandardBillingStrategy` / `InsuranceBillingStrategy` | Bill calculation varies (standard vs. insured patients) without `if/else` branching inside `BillService`. |
| **Dependency Injection** (constructor-based) | `AppointmentService`, `BillService`, `AuthService` | Each has a no-arg constructor (wires real DAOs via `DAOFactory`) and a DAO-accepting constructor used by unit tests with mocks. |

## Assumptions made

- Each appointment is billed once; reprinting a bill returns the existing record rather than creating a duplicate (`BillService.generateBill`).
- A flat consultation fee (Rs. 1,500) is charged in addition to the treatment fee.
- "Insurance" is a simple yes/no flag chosen at billing time, applying a 20% discount to the treatment fee only.
- Contact numbers follow the local 10-digit format starting with 0 (e.g. `0771234567`).
- Only one role distinction is modelled (`ADMIN` / `STAFF`); both can use all features - the brief did not require per-role permissions, but the `Role` enum leaves room to add that later.
- Appointment numbers follow the pattern `APT-yyyyMMdd-####` and are checked for uniqueness before saving.

## Git / GitHub workflow (Task D)

Suggested approach for a defensible commit history:

1. `git init`, then commit the initial scaffold (this structure) as commit 1.
2. Commit each subsequent change separately and descriptively as you build/adjust features - e.g. "Add appointment registration validation", "Add insurance billing strategy", "Add JUnit tests for AppointmentService" - rather than one giant final commit.
3. Push to a **public** GitHub repository.
4. Link this README/report from the repository, and link the repository from the report.
5. If you want to demonstrate a workflow, use a simple feature branch + pull request into `main` for at least one change, and/or add a basic GitHub Actions workflow that runs `mvn test` on push.
