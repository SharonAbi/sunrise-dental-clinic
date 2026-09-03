# Task A: System Design with UML Diagrams

*(Paste the diagrams as images into the report; use the text below as the
supporting explanation, editing to match your own voice.)*

## 1. Use Case Diagram

**File:** `01_usecase.png`

The system has a single actor, **Staff Member**, since the assignment scope
restricts use of the system to "authorized staff" — patients do not interact
with the system directly (they are recorded *by* staff, not as users of it).

Six primary use cases map directly to the six functional requirements in the
brief: Login, Register New Appointment, Display Appointment Details,
Calculate & Print Bill, View Help, and Logout.

**`<<include>>` relationships:** *Register New Appointment* always performs
two sub-behaviours regardless of outcome, so they are modelled as included
use cases rather than optional ones:
- *Validate Patient & Appointment Input* — every submission is checked
  (name format, 10-digit contact number, date not in the past, etc.)
  before anything is saved.
- *Generate Unique Appointment Number* — every successful registration
  must produce a number, and the system retries generation until a
  collision-free one is found.

**`<<extend>>` relationship:** *Apply Insurance Discount* extends
*Calculate & Print Bill* only when the extension condition is met (staff
tick "patient has insurance"). This is optional/conditional behaviour,
which is exactly what `<<extend>>` is for, as opposed to `<<include>>`
which is unconditional.

**Assumption:** All use cases other than Login require an authenticated
session. Rather than draw a repetitive `<<include>>` arrow from every
use case to Login (which would clutter the diagram without adding
information), this is documented as a note and enforced in the
implementation by `AuthFilter`, a Servlet filter applied to every
protected URL.

## 2. Class Diagram — Domain Model

**File:** `02_class_domain.png`

This diagram shows the core data entities and their relationships, matching
`com.sunrise.dental.model`.

- **Multiplicity:** one `Appointment` references exactly one `Patient`, one
  `Dentist`, and one `Treatment` (`Appointment "0..*" --> "1" X`), while a
  `Dentist` or `Treatment` can be referenced by many appointments over time.
  A `Bill` has a `0..1` relationship back to its `Appointment` — an
  appointment may or may not have been billed yet.
- **Navigability:** all associations are unidirectional, pointing from
  `Appointment`/`Bill` toward the entity they reference. The model classes
  are deliberately simple POJOs with no back-references (e.g. `Patient`
  does not hold a list of its appointments); lookups run the other
  direction through the DAO layer instead. This keeps the model classes
  free of persistence concerns.
- **Access modifiers:** all fields are `private` with `public` getters
  (and setters, omitted from the diagram for readability) — standard
  encapsulation.
- **Enumerations:** `AppointmentStatus` (SCHEDULED / COMPLETED / CANCELLED)
  and `Role` (ADMIN / STAFF) are modelled as UML enumerations rather than
  plain strings, so invalid values are impossible at compile time.

**Assumption / documented simplification:** in the current implementation,
`PatientDAO.save()` always inserts a *new* patient row on every
registration — there is no lookup-by-contact-number to reuse an existing
patient record for a returning visitor. The diagram shows the intended
1-to-many relationship between `Patient` and `Appointment` (one patient,
many visits over time), which is the correct conceptual model; the current
code is a simplification of it. A natural extension would be adding
`PatientDAO.findByContactNumber()` and checking it before inserting, so a
returning patient's history is linked together instead of creating a
duplicate record each visit. This trade-off was made deliberately to keep
registration a single, self-contained step, matching how a walk-in clinic
front desk actually works (a new patient card per visit is filled in by
hand today, so this mirrors the manual process being replaced) — but it is
worth being upfront about in evaluation, since deduplication would be
needed for real deployment.

## 3. Class Diagram — Architecture & Design Patterns

**File:** `03_class_architecture.png`

This second diagram deliberately separates *architecture* from *domain
data* (Diagram 2) so each stays readable. It shows how the four design
patterns used in Task B are structured:

- **DAO pattern:** every entity has a DAO *interface* (`PatientDAO`,
  `AppointmentDAO`, `BillDAO`, `UserDAO`) realised by a JDBC
  implementation (`PatientDAOImpl`, etc., shown with the dashed
  "realisation" arrow `..|>`). Services and servlets depend only on the
  interfaces, never the JDBC classes directly.
- **Factory pattern:** `DAOFactory` centralises DAO creation
  (`getPatientDAO()`, etc.) so callers never write `new PatientDAOImpl()`
  themselves. `BillingStrategyFactory` does the same for billing
  strategies. Both are marked `«Factory»`.
- **Singleton pattern:** `DBConnection` is marked `«Singleton»` — it loads
  `db.properties` and the JDBC driver exactly once (`getInstance()`
  guards construction), and every DAO implementation depends on it for
  connections.
- **Strategy pattern:** `BillingStrategy` is an interface with two
  interchangeable implementations, `StandardBillingStrategy` and
  `InsuranceBillingStrategy`. `BillService` never checks
  `if (hasInsurance)` itself — it asks `BillingStrategyFactory` for the
  right strategy and calls it polymorphically. Adding a third billing
  rule (e.g. a corporate-account discount) means adding one new class,
  with no existing code touched.
- **Dependency direction:** servlets depend on services, services depend
  on DAO interfaces (never impls), and DAO impls depend on the
  `DBConnection` singleton. This top-down dependency flow is what makes
  the classes in `AppointmentServiceTest` and friends unit-testable with
  mocked DAOs instead of a live database.

## 4. Sequence Diagrams

Three sequence diagrams were produced, covering the three most significant
interaction flows (as recommended by the brief) — authentication, the core
"register an appointment" transaction, and billing (which is where the
Strategy pattern is actually exercised at runtime).

### 4.1 Login (`04_sequence_login.png`)

Shows `LoginServlet` delegating to `AuthService`, which loads the `User` by
username via `UserDAO`, hashes the submitted password with `PasswordUtil`
(SHA-256), and compares it against the stored hash — the plain-text
password is never persisted or compared directly, satisfying the
assignment's ETHICAL/EDGE requirement to protect user data. The `alt`
fragment shows both outcomes: a valid login creates an `HttpSession` and
redirects to the dashboard; an invalid one re-displays the login form with
an error message.

### 4.2 Register New Appointment (`05_sequence_register.png`)

Shows validation happening in the servlet *before* any service/DAO call is
made (fail fast, no wasted database round-trip on bad input), then the
`loop` fragment showing `AppointmentService` regenerating the appointment
number until `AppointmentDAO.existsByAppointmentNumber()` confirms
uniqueness — this loop is what guarantees the "unique appointment number"
requirement in the brief actually holds, rather than merely hoping for no
collisions.

### 4.3 Calculate & Print Bill (`06_sequence_bill.png`)

Shows the `alt` fragment for idempotent billing: if a bill already exists
for the appointment (re-printing a receipt), it's returned as-is; only a
first-time billing request goes through `BillingStrategyFactory` to obtain
the correct `BillingStrategy` and calculate the discount/total. This is the
sequence diagram that most directly demonstrates the Strategy pattern in
motion, complementing the static view in the architecture class diagram.
