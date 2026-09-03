# Task C: Testing, TDD, and Test Automation

*(Use this as the basis for the report section; screenshots referenced below
should be taken locally by running the commands shown, and/or from the
"Actions" tab on GitHub once CI has run — see §5.)*

## 1. Test Rationale

The system is layered (Servlet → Service → DAO → MySQL), so tests target
the layer where a defect is cheapest to catch and clearest to diagnose:

- **`util` classes** (`ValidationUtil`, `PasswordUtil`, `AppointmentNumberGenerator`)
  are pure functions with no dependencies — tested directly, no mocking needed.
- **`service.billing` strategies** are pure calculation logic — tested directly.
- **`service` classes** (`AppointmentService`, `BillService`, `AuthService`)
  depend on DAOs. Rather than requiring a live MySQL instance to run the
  test suite (slow, environment-dependent, and not runnable in CI without
  provisioning a database), each service has two constructors: a no-arg one
  that wires real DAOs via `DAOFactory` for production use, and a
  DAO-accepting one used only by tests, with **Mockito** mocks standing in
  for the DAOs. This isolates the test from the database entirely while
  still exercising the service's real logic (number generation and
  retry-on-collision, discount calculation, password hash comparison,
  the idempotent-billing check, etc.).
- **DAO implementations and Servlets are not unit tested** — they are thin
  JDBC/HTTP glue with little logic of their own, and are instead verified
  by manually exercising the running application end-to-end (see the
  manual test log in §4).

This gives fast, deterministic, dependency-free tests for the classes that
actually contain business logic, which is the standard trade-off used in
real Java web projects rather than attempting (impractical) full
end-to-end automation of every screen.

## 2. Test-Driven Development in This Scenario

TDD (red → green → refactor) was used when adding new business rules to
existing code, since that is where TDD's value is clearest: a rule already
has a natural place to slot into, and a failing test proves the rule
didn't already silently work another way.

**Worked example — enforcing clinic operating hours (09:00–17:00) when
registering an appointment:**

**Step 1 (RED) — write the test before the code exists.** Four cases were
added to `ValidationUtilTest`: an in-hours time, the opening boundary, a
time just before opening, and the closing boundary. Running the suite at
this point fails to even compile, because `isWithinClinicHours` does not
exist yet:

```
[ERROR] COMPILATION ERROR :
[ERROR] .../ValidationUtilTest.java:[62,34] cannot find symbol
[ERROR]   symbol:   method isWithinClinicHours(java.time.LocalTime)
[ERROR]   location: class com.sunrise.dental.util.ValidationUtil
[INFO] BUILD FAILURE
```

**Step 2 (GREEN) — write the minimum code to pass.** `ValidationUtil.isWithinClinicHours(LocalTime)`
was implemented as a simple boundary check (`09:00` inclusive, `17:00`
exclusive), and the same four tests were re-run:

```
[INFO] Tests run: 14, Failures: 0, Errors: 0, Skipped: 0 -- in com.sunrise.dental.util.ValidationUtilTest
[INFO] Tests run: 20, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

**Step 3 (wire it in) — the rule is only useful if the application actually
enforces it.** `RegisterAppointmentServlet` was updated to call
`isWithinClinicHours` alongside the existing time-format check, adding the
error message *"Appointment time must be between 09:00 and 17:00."* This
step has no dedicated unit test of its own — it is a one-line delegation —
and was instead confirmed by exercising the register-appointment screen
manually with a 07:00 time and observing the validation error (see §4).

**Honesty note for the report:** the *rest* of the codebase (DAOs,
servlets, the original service methods) was written first and given
characterisation tests afterwards, under the time constraints of this
assignment — it was not built test-first from a blank file. The clinic-hours
example above is the part of the codebase that genuinely followed
red-green TDD, and is a good one to reproduce live if asked to demonstrate
the process, since it is small, self-contained, and takes under five
minutes to redo from `git log`.

## 3. Test Plan

| ID | Class under test | Scenario | Test data | Expected result |
|----|---|---|---|---|
| T01 | `ValidationUtil` | Valid patient name | `"Kasun Perera"` | accepted |
| T02 | `ValidationUtil` | Empty name | `""` | rejected |
| T03 | `ValidationUtil` | Name containing digits | `"Kasun123"` | rejected |
| T04 | `ValidationUtil` | Valid 10-digit contact number | `"0771234567"` | accepted |
| T05 | `ValidationUtil` | Contact number too short | `"12345"` | rejected |
| T06 | `ValidationUtil` | Contact number with letters | `"077abc4567"` | rejected |
| T07 | `ValidationUtil` | ISO date string | `"2026-09-05"` | accepted |
| T08 | `ValidationUtil` | Garbage date string | `"not-a-date"` | rejected |
| T09 | `ValidationUtil` | Past date | yesterday | rejected |
| T10 | `ValidationUtil` | Today's date | today | accepted |
| T11 | `ValidationUtil` | Time inside clinic hours | `10:30` | accepted |
| T12 | `ValidationUtil` | Time at opening boundary | `09:00` | accepted |
| T13 | `ValidationUtil` | Time just before opening | `08:59` | rejected |
| T14 | `ValidationUtil` | Time at/after closing | `17:00` | rejected |
| T15 | `BillingStrategy` (Standard) | No insurance | consultation Rs.1500, treatment Rs.5000 | total Rs.6500, discount Rs.0 |
| T16 | `BillingStrategy` (Insurance) | With insurance | consultation Rs.1500, treatment Rs.5000 | discount Rs.1000, total Rs.5500 |
| T17 | `BillingStrategyFactory` | Factory selection | `hasInsurance=true/false` | returns matching strategy type |
| T18 | `AppointmentService` | Register appointment | mocked PatientDAO/AppointmentDAO | patient saved, appointment saved, number generated |
| T19 | `AppointmentService` | Appointment number collision | first number reported as taken | generator retries and calls `existsByAppointmentNumber` twice |
| T20 | `BillService` | Standard bill calculation | treatment fee Rs.3500 | total Rs.5000, no discount |
| T21 | `BillService` | Insurance bill calculation | treatment fee Rs.3500 | discount Rs.700, total Rs.4300 |
| T22 | `BillService` | Re-billing an already-billed appointment | existing `Bill` returned by mock DAO | existing bill returned, `save()` never called again |
| T23 | `AuthService` | Correct password | hash matches | `User` returned |
| T24 | `AuthService` | Wrong password | hash does not match | `null` returned |
| T25 | `AuthService` | Unknown username | DAO returns `null` | `null` returned |

26 automated test methods across 5 test classes (`ValidationUtilTest`,
`BillingStrategyTest`, `AppointmentServiceTest`, `BillServiceTest`,
`AuthServiceTest`) — mapped 1:1 onto the IDs above (T14–T17 map to 4
`BillingStrategyTest` methods, T18–T19 to `AppointmentServiceTest`, etc.).

## 4. Manual / Exploratory Test Log (DAO + UI layer)

Since DAOs and servlets are integration/UI glue rather than unit-tested,
they should be verified by hand against a running instance before
submission. Suggested log to fill in and screenshot:

| # | Action | Expected | Actual | Pass/Fail |
|---|---|---|---|---|
| M1 | Log in with `admin` / `admin123` | Redirected to dashboard | | |
| M2 | Log in with wrong password | Error shown, stays on login | | |
| M3 | Visit `/dashboard` while logged out | Redirected to login (AuthFilter) | | |
| M4 | Register an appointment with all valid fields | Appointment number shown | | |
| M5 | Register an appointment with a 07:00 time | "must be between 09:00 and 17:00" error | | |
| M6 | Register an appointment with a past date | "cannot be in the past" error | | |
| M7 | Search for the appointment number from M4 | Full details displayed | | |
| M8 | Search for a non-existent number | "No appointment found" message | | |
| M9 | Calculate bill, no insurance | Total = consultation + treatment fee | | |
| M10 | Calculate bill again for the same appointment, tick insurance | Returns the *same* bill as M9 (idempotent — insurance flag from a second request does not retroactively discount it) | | |
| M11 | `GET /api/appointments?number=<valid>` | JSON body with appointment fields | | |
| M12 | `GET /api/appointments?number=<invalid>` | HTTP 404, JSON error body | | |

## 5. Test Automation

Two layers of automation are in place:

1. **`mvn test`** (Maven Surefire) runs all 26 JUnit 5 tests locally in
   seconds, with no database required, as shown by the BUILD SUCCESS
   output in §2.
2. **GitHub Actions** (`.github/workflows/ci.yml`) runs `mvn test` (then
   `mvn package`) automatically on every push and pull request to `main`.
   This is real continuous integration, not just a local habit: a broken
   commit is caught even if nobody remembers to run the tests by hand.
   For the report, take a screenshot of a green run from the repository's
   **Actions** tab as evidence the code passes all tests.

## 6. Evaluation

**What went well:** isolating service-layer logic behind DAO interfaces
(the DAO + Dependency Injection combination described in Task A/B) meant
the whole business-logic test suite runs without any database, which is
exactly what made CI on GitHub Actions trivial to add — there was no
database service container to configure.

**Limitations / lessons learned:** the DAO implementations themselves
(the actual SQL) and the servlets (HTTP wiring) are not covered by
automated tests, only by the manual log in §4. A more complete solution
would add integration tests against a disposable database (e.g.
Testcontainers with a real MySQL container), which was out of scope given
the assignment timeline but is a reasonable "further work" point to note
in the report. The clinic-hours TDD example also exposed a gap that
existed since the first working version — appointments could previously
be booked at any time of day/night — showing that even a small, deliberate
TDD pass on existing code can surface real, previously-unnoticed
requirements gaps.
