
# HourlyHire

## Backend Project Requirements & Workflow Guide

Team onboarding document | Modular monolith | Backend-first | No deployment in current scope

| Document purpose | Project direction |
| --- | --- |
| Help every team member understand what HourlyHire does, its major features, how the work flows, and how backend code should be organized. | One Spring Boot application, one Maven project, one PostgreSQL database, clear module boundaries, all planned features retained and delivered incrementally. |

Version 1.0 • Working requirements baseline

## 1. Executive summary

HourlyHire is a platform intended to connect employers who need workers for hourly work with workers who can offer their skills and availability. The backend supports accounts and permissions, profiles, skills, availability, discovery, booking, attendance, hours approval, reviews, disputes, notifications, administration, AI-assisted capabilities, and payments.

The project will be implemented as a modular monolith. This means there is one backend application and one deployment unit, but the code is divided into modules with clear responsibilities. It is not a microservices project. The first goal is to build and test the backend locally; deployment and infrastructure rollout are explicitly out of scope for now.

### The agreed direction

- Keep the full planned feature scope. Reduce unnecessary module boundaries, not product capabilities.
- Use Java, Spring Boot, Maven, Spring MVC, Spring Security, Spring Data JPA/Hibernate, PostgreSQL, and Flyway.
- Use REST APIs under `/api/v1` with DTOs, validation, consistent errors, and pagination where lists can grow.
- Use Kafka only for workflows that benefit from asynchronous event processing; it is not required for every feature.
- Build in small phases, with automated tests and explicit business rules.

## 2. Product purpose and main users

| User / actor | What they need to do |
| --- | --- |
| Worker | Create and manage a worker profile, provide skills and service areas, maintain availability, discover and respond to work opportunities, record attendance/hours, view approved work history, receive notifications, and participate in reviews or dispute workflows. |
| Employer | Create an employer or organization profile, define work needs, discover suitable workers, create and manage bookings, review attendance and hours, make or track payments when enabled, and provide reviews or raise disputes. |
| Administrator | Manage permitted platform operations, review relevant records, handle escalated cases, inspect audit trails, and access approved reporting. |
| System / background processing | Send notifications, process domain events, retry transient failures safely, record delivery outcomes, and run permitted AI or payment integrations. |

These are role-level capabilities. The final product policy must specify exactly which actions each role can perform in each state. A role alone is not sufficient authorization: the backend must also verify record ownership, organization membership, and workflow state.

## 3. Scope and boundaries

### Included in the target backend

- Account lifecycle: registration, login, authentication, access control, email verification, secure password reset, and token lifecycle.
- Employer and worker profiles, organization relationships where applicable, skills/categories, availability, and service areas.
- Worker discovery and filtering based on supported profile and availability data.
- Booking lifecycle, attendance/time records, hour review/approval, and work completion.
- Reviews and ratings, complaints/disputes, notifications, administration, audit/reporting.
- AI integration as an assistive capability that cannot override backend business rules.
- Payment-related records and workflows, with provider-specific behavior implemented only after decisions are approved.

### Out of scope for the current phase

- Production deployment, cloud hosting, Kubernetes, production infrastructure, and release rollout.
- Splitting the application into microservices.
- Adding Redis, Elasticsearch, a workflow engine, or other infrastructure without a demonstrated requirement.
- Making up unresolved business policies such as cancellation fees, verification evidence, payment commission, or dispute outcomes.

Local developer tooling is allowed. For example, Docker Compose may be used to run PostgreSQL or Kafka locally, but that is development support—not a deployment deliverable.

## 4. Architecture overview

The application is a single Spring Boot process with module-oriented packages. Modules share the same application runtime and initially use one PostgreSQL database, while each module owns its domain rules and persistence access.

| Area | Decision / requirement |
| --- | --- |
| Application | Java + Spring Boot; one runnable backend application. |
| Build | Maven; one project initially, with packages grouped by module. |
| API | REST endpoints rooted at `/api/v1`. |
| Database | PostgreSQL as the primary source of truth. |
| Schema changes | Flyway versioned migrations; Hibernate schema mode set to validate rather than create/update production-like schemas automatically. |
| Persistence | Spring Data JPA / Hibernate; DTOs at API boundaries, not database entities exposed directly. |
| Security | Spring Security, JWT access tokens, rotating refresh tokens, BCrypt password hashing, role and ownership checks. |
| Validation / errors | Jakarta Bean Validation and centralized exception handling with a stable error response format. |
| API docs | Springdoc OpenAPI for documenting and trying APIs during development. |
| Testing | JUnit 5, Mockito, MockMvc, and PostgreSQL Testcontainers for database integration tests. |
| Events | Kafka only where asynchronous processing is useful; use reliable event publishing and idempotent consumers for important events. |
| Observability | Structured application logs, correlation/request IDs, and basic health/metrics endpoints for local diagnosis. |

## 5. Final module structure (10 modules)

The module count is a code-organization choice, not a reduction in feature scope. Keep internal responsibilities clear, and split a module later only when its complexity or team ownership makes that useful.

| # | Module | Responsibilities | Important boundary |
| --- | --- | --- | --- |
| 1 | Shared Foundation | Configuration, common API response/error types, technical utilities, security plumbing, logging/correlation IDs. | Only technical shared code. Do not put product business rules here. |
| 2 | Identity & Access | Registration, login, password reset, email verification, access/refresh tokens, roles and account status. | Owns identity and authentication data. Other modules use its application interfaces. |
| 3 | Profiles & Workforce | Employer/organization profile, worker profile, skills/categories, availability, service areas, verification status/data. | Owns profile/workforce records. Do not let other modules write its repositories directly. |
| 4 | Discovery | Search/filter workers and work-relevant profile data; rank/filter based on deterministic criteria. | Reads approved profile/search data through module interfaces or a deliberate read model. |
| 5 | Work Management | Booking, booking status, attendance, timesheets/hours, approval, completion, corrections. | Owns the work engagement lifecycle. Enforce overlap and state-transition rules transactionally. |
| 6 | Trust & Resolution | Reviews, ratings, complaints, disputes, case status and resolution records. | Keep review eligibility and dispute decisions distinct internally, even in one module. |
| 7 | Notifications & Events | Notification templates/delivery, delivery status, event handlers, retry and dead-letter handling. | Should not become the owner of the business event's source data. |
| 8 | Administration & Audit | Admin operations, audit history, controlled reporting, support workflows. | Admin actions must call the owning module's service; no direct cross-module data edits. |
| 9 | AI | Provider adapter, prompts/configuration, AI-assisted matching or summaries where approved, usage and failure handling. | AI output is advisory; deterministic authorization, booking, payment, and policy rules remain authoritative. |
| 10 | Payments | Payment intents/transactions, fees, refunds, payouts, reconciliation and provider integration when policy is finalized. | Money movement must be auditable, idempotent, and isolated from AI and general profile logic. |

## 6. Typical package layout

Illustrative layout for one Maven project:

hourlyhire/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── hourlyhire/
│   │   │           ├── shared/
│   │   │           │   ├── config/
│   │   │           │   ├── exception/
│   │   │           │   ├── response/
│   │   │           │   ├── security/
│   │   │           │   └── observability/
│   │   │           │
│   │   │           ├── identity/
│   │   │           │   ├── controller/
│   │   │           │   ├── service/
│   │   │           │   ├── repository/
│   │   │           │   ├── entity/
│   │   │           │   └── dto/
│   │   │           │
│   │   │           ├── workforce/
│   │   │           │   ├── controller/
│   │   │           │   ├── service/
│   │   │           │   ├── repository/
│   │   │           │   ├── entity/
│   │   │           │   └── dto/
│   │   │           │
│   │   │           ├── discovery/
│   │   │           ├── workmanagement/
│   │   │           ├── trust/
│   │   │           ├── notification/
│   │   │           ├── administration/
│   │   │           ├── ai/
│   │   │           └── payment/
│   │   │
│   │   └── resources/
│   │       ├── application.yml
│   │       └── db/
│   │           └── migration/
│   │               ├── V1__initial_schema.sql
│   │               └── V2__next_change.sql
│   │
│   └── test/
│       └── java/
│           └── com/
│               └── hourlyhire/
│                   └── ... unit and integration tests
│
├── pom.xml
└── README.md

Each module should use only the layers it needs. Do not create empty controllers, services, repositories, DTOs, or entities just to fill a folder template. Use names that reflect the actual domain.

## 7. End-to-end business workflows

### 7.1 Account registration and sign-in

1. A person submits registration details. The API validates required fields and input formats.
2. The backend checks uniqueness rules (for example, email uniqueness), stores a password hash—not the raw password—and creates an account in the appropriate initial state.
3. The system sends an email verification flow if required by the approved product policy.
4. The user verifies the account and signs in. The backend checks account status and credentials before issuing a short-lived access token and refresh token.
5. Protected endpoints validate the token, role, ownership, organization membership, and requested action.
6. Refresh-token rotation and revocation rules must be implemented so stolen or reused refresh tokens can be detected and invalidated.

### 7.2 Profile setup and readiness

1. A worker or employer completes the fields relevant to their account type.
2. The backend validates the submitted fields and persists them through the Profiles & Workforce module.
3. Workers may add skills/categories, service areas, availability, and verification information according to product policy.
4. The backend exposes a clear profile/readiness state so discovery and booking can apply the same eligibility rules.
5. Only approved, visible, and eligible records should be exposed in discovery results.

### 7.3 Worker discovery

1. An employer submits search filters such as skill/category, service area, and relevant availability.
2. The Discovery module applies supported filters and pagination using PostgreSQL queries and indexes.
3. The result includes only information the caller is allowed to see; sensitive verification evidence and private account data are not exposed.
4. Discovery is not the final authority for booking. Eligibility and availability must be checked again when a booking is created.

### 7.4 Booking and work lifecycle

1. The employer requests a booking with worker, time window, location/service area, and any required work details.
2. The backend validates caller permissions, profile eligibility, booking rules, and the requested time window.
3. Inside a transaction, the backend rechecks availability and prevents overlapping confirmed bookings according to the approved conflict policy.
4. The booking moves through explicit allowed states (for example requested, confirmed, cancelled, completed). Exact states and transitions must be agreed by the product team.
5. Relevant events may be published after the database change is safely committed; notifications inform the affected parties.
6. All state transitions are checked on the server. The client cannot set an arbitrary status by submitting a field.

### 7.5 Attendance, hours, and completion

1. The appropriate party records check-in/check-out or the supported attendance information.
2. The backend validates that the record belongs to the correct booking and actor, and that the workflow permits the action.
3. Hours are calculated using approved time-zone, break, rounding, and correction policies; timestamps should be stored as UTC instants.
4. The employer or designated approver reviews submitted hours and approves, rejects, or requests correction according to policy.
5. Approved hours become the authoritative input for completion records and any financial calculations. Do not calculate final payment from unapproved hours.
6. Changes after approval must use an auditable correction workflow, not silent overwrites.

### 7.6 Reviews and disputes

1. A review is accepted only from an eligible participant and only when the booking reaches the policy-defined state.
2. The backend prevents duplicate or otherwise disallowed reviews and records ratings/text according to moderation policy.
3. A complaint/dispute is linked to the relevant booking and participants, with evidence and status changes controlled by permissions.
4. Only authorized roles may make resolution decisions. Every significant decision and administrative action should be auditable.
5. Dispute deadlines, evidence requirements, appeals, and the impact on payment/refunds require explicit product decisions.

### 7.7 Payments

1. Payment flows begin only after the provider, currency, fee/commission, refund, payout, and compliance rules are approved.
2. The backend records payment state changes and provider references; it must not treat a browser redirect as proof of successful payment.
3. Use idempotency keys or equivalent deduplication for payment requests and webhook processing.
4. Reconcile local records with the provider's authoritative transaction status. Handle duplicate, delayed, and out-of-order callbacks safely.
5. Keep payment secrets out of source control and logs. Store money as `BigDecimal` / database `NUMERIC` with an explicit currency.

### 7.8 Notifications and asynchronous events

1. A business operation updates its own database state in a transaction.
2. For important events that must not be lost, persist an outbox record in the same transaction as the business change.
3. A background publisher forwards outbox events to Kafka (if Kafka is enabled for that workflow).
4. Consumers process events idempotently, retry transient errors with backoff, and route exhausted failures to a dead-letter path for inspection.
5. Notification failure should normally not roll back a completed booking or approved hours. Track delivery status and provide safe retries.

### 7.9 AI-assisted features

1. A module calls the AI provider through a controlled adapter, with timeouts, usage limits, and failure handling.
2. Send only the data needed for the approved feature; do not expose secrets or unnecessary personal information.
3. Treat model output as untrusted suggestions. Validate the format and apply normal business rules before using it.
4. AI must not bypass authorization, confirm a booking, approve hours, decide a payment, or resolve a dispute without the deterministic policy and authorized workflow.


## 8. API standards

- Prefix backend endpoints with `/api/v1`. Use resource-oriented paths and consistent HTTP methods/status codes.
- Use request and response DTOs. Do not serialize JPA entities directly from controllers.
- Validate input at the API boundary and revalidate important business invariants in the service/domain layer.
- Use one centralized error format with a stable machine-readable error code, human-readable message, request/correlation ID, and field errors when relevant.
- Use pagination for potentially large lists; define stable sorting and filtering behavior.
- Return appropriate status codes: 2xx for success, 4xx for invalid/unauthorized/conflicting requests, and 5xx for unexpected server failures.
- Never leak stack traces, tokens, passwords, provider secrets, or sensitive evidence in API errors.
- Document endpoints, authentication requirements, request/response examples, and error cases with OpenAPI.

## 9. Database and persistence standards

- PostgreSQL is the source of truth for core business state.
- Use Flyway versioned migrations for schema changes. Do not rely on Hibernate auto-update to evolve shared schemas.
- Use database constraints for invariants that must hold even under concurrent requests, supported by service-level validation.
- Use transactions around multi-step state changes that must succeed or fail together.
- Store timestamps as UTC instants and apply explicit business time-zone rules for local work schedules.
- Use `BigDecimal` in Java and `NUMERIC` in PostgreSQL for monetary values; always retain currency.
- Add indexes based on real query patterns, especially discovery filters, booking lookups, foreign keys, and status/date searches.
- Use optimistic locking or other concurrency controls where two actors may update the same record.
- Do not let one module update another module's tables through direct repository access. Cross-module reads/writes should use exposed services or deliberate event/read-model contracts.

## 10. Security, privacy, and audit requirements

- Hash passwords with BCrypt; never store plaintext passwords.
- Use short-lived access tokens and rotating refresh tokens with revocation/reuse protections.
- Apply role-based authorization plus resource ownership and organization-membership checks.
- Rate-limit sensitive endpoints such as login, password reset, verification, and other abuse-prone operations.
- Keep secrets in environment/configuration mechanisms, not in source code or committed configuration files.
- Do not log passwords, authorization headers, raw tokens, payment secrets, or sensitive verification documents.
- Use audit records for high-impact actions such as account/security changes, booking state changes, hour approvals/corrections, dispute decisions, admin actions, and payment state transitions.
- Return only the minimum personal data needed by each endpoint and role.

## 11. Kafka and event design: use only when useful

Kafka is not the default way for modules to communicate. A direct application-service call is simpler for an immediate request/response operation inside the monolith. Use asynchronous events for decoupled work such as notification delivery or other operations that can complete after the main transaction.

| Do | Avoid |
| --- | --- |
| Publish versioned event contracts with stable identifiers and timestamps. | Using Kafka to perform synchronous validation that the API caller needs immediately. |
| Use an outbox for important events that must stay consistent with a database transaction. | Writing to the database and then publishing an event with no recovery strategy. |
| Make consumers idempotent because messages may be delivered more than once. | Assuming every message is delivered exactly once. |
| Retry transient failures with backoff and use a dead-letter process for exhausted failures. | Retrying forever without visibility or controls. |
| Keep event payloads minimal and avoid unnecessary personal data. | Treating event payloads as a replacement for authoritative domain records. |

## 12. Cross-module dependency rules

- Shared Foundation may be used by all modules, but it must not depend on business modules.
- Identity & Access provides identity/authorization capabilities; it should not own profile, booking, or payment business logic.
- Discovery can query eligible profile/search information, but booking must recheck current eligibility and availability at creation time.
- Work Management owns booking, attendance, and approved hours. Payments consumes approved-hour and booking facts through a defined interface/event.
- Notifications consumes events and delivers messages; it does not decide whether a booking is valid.
- Administration invokes the relevant owning module's service and records who performed the action.
- AI can recommend or summarize, but all consequential operations pass through the owning module's deterministic rules.
- Avoid circular dependencies. If two modules need each other, introduce a narrow interface/event or reconsider the boundary.

## 13. Suggested implementation sequence

| Phase | Build focus | Definition of done |
| --- | --- | --- |
| 1. Foundation & Identity | Project skeleton, config, errors, validation, Flyway, DB connection, security, registration/login/token flows, API docs and test setup. | Application starts locally; migrations run; auth/security tests pass; API errors are consistent. |
| 2. Profiles & Workforce | Employer/worker profiles, skills/categories, availability, service areas, verification state. | Create/read/update flows work with validation and authorization; migrations and tests are present. |
| 3. Discovery & Booking | Search/filter/pagination; booking creation and state transitions; overlap prevention. | Search is permission-safe; booking rechecks availability transactionally; concurrency/conflict tests pass. |
| 4. Attendance & Trust | Attendance, hour submission/approval/correction, completion, reviews, complaints/disputes. | State transitions and permissions are enforced; approved hours are authoritative; audit coverage exists. |
| 5. Notifications & Administration | Event/outbox patterns where justified, notification delivery/retry, admin workflows, audit/reporting. | Retries are safe; important events are not silently lost; admin actions are authorized and auditable. |
| 6. AI & Payments | Provider adapters, approved AI use cases, payment lifecycle, idempotency, reconciliation and failure handling. | Product rules and provider choices are approved; integration and failure-path tests pass. |

This is an implementation order, not a statement that later modules are optional. AI and Payments remain in the target scope, but their consequential behavior should wait for the relevant product and compliance decisions.

## 14. Minimum testing expectations

| Test type | What it should prove |
| --- | --- |
| Unit tests | Business rules, eligibility, allowed state transitions, calculations, validation decisions. |
| Controller/API tests | Request validation, response contracts, HTTP status codes, authentication and authorization outcomes. |
| Database integration tests | Flyway migrations, constraints, transactions, queries, persistence behavior using PostgreSQL/Testcontainers. |
| Workflow tests | Registration-to-profile, discovery-to-booking, booking-to-approved-hours, review/dispute eligibility. |
| Concurrency tests | Two simultaneous booking requests cannot create conflicting confirmed bookings. |
| Security tests | Users cannot read/change another user's records without permission; expired/revoked tokens fail. |
| Event tests | Duplicate events do not duplicate effects; retries and dead-letter handling work. |
| Payment integration tests | Duplicate requests/webhooks, delayed/out-of-order callbacks, failed payment, refund and reconciliation paths. |

## 15. Key business decisions that must be approved

The architecture can be agreed now, but the backend team must not silently invent the following policies. Record decisions in a shared decision log before implementing the affected behavior.

| Decision area | Questions to resolve |
| --- | --- |
| Booking | Who can request/accept/confirm? Are employer approval and worker acceptance both required? What are the allowed statuses and transitions? Can a booking be rescheduled? How are overlapping shifts handled? |
| Cancellation | Who may cancel, by when, and with what consequences? Are fees or penalties applied? What happens to already-recorded hours? |
| Verification | Which worker/employer evidence is required? Who reviews it? What are the statuses, expiry rules, rejection reasons, and privacy/retention rules? |
| Time and attendance | Which time zone defines the shift? How are breaks, late arrival, overtime, rounding, missed check-in/out, corrections, and disputes handled? |
| Hours approval | Who approves? What is the review deadline? Can approval be reversed? What audit trail is required for adjustments? |
| Reviews and disputes | Who is eligible to review? Are edits allowed? What evidence and deadlines apply to complaints? Who decides outcomes and appeals? |
| Notifications | Which channels are required (email, SMS, push, in-app)? Which events trigger them? What are delivery, retry, preference, and retention rules? |
| AI | Which use cases are approved? Which provider? What data may be sent? What are consent, retention, cost limits, timeouts, and fallback behavior? |
| Payments | Which provider, currencies, commission/fees, payout timing, refunds, failed payments, chargebacks, reconciliation, tax/compliance obligations, and dispute holds apply? |
| Organizations and roles | Can one account belong to multiple organizations? Which organization roles exist? Who can invite members or act on behalf of an organization? |

## 16. Definition of ready and done for a feature

### A feature is ready to implement when

- The user/actor and expected outcome are clear.
- The business rules, permissions, and relevant status transitions are written down.
- Request/response data and validation rules are known.
- Database changes and migration impact are understood.
- Dependencies on other modules and events are identified.
- Acceptance criteria and important failure cases are agreed.

### A feature is done when

- The endpoint/service behavior matches the approved rules.
- Input validation, authorization, ownership checks, and error handling are in place.
- Database migration, constraints, and indexes are included where required.
- Unit and integration tests cover success, invalid input, unauthorized access, conflicts, and relevant failure paths.
- OpenAPI documentation and any event contract are updated.
- Sensitive information is not exposed in responses or logs.
- The feature works locally with the documented configuration and no deployment is required.

## 17. Team working principles

- Keep the first implementation boring and understandable. Avoid new infrastructure unless a concrete requirement justifies it.
- Prefer explicit business rules over hidden behavior, magic status changes, or implicit side effects.
- Do not return persistence entities directly from APIs.
- Do not bypass module ownership for convenience.
- Use transactions for state changes that must be atomic and test race conditions where appropriate.
- Make side effects idempotent and observable.
- Do not mark unresolved policy as implemented using guessed behavior; raise the decision early.
- Build the full scope in increments, keeping the main branch runnable and tests meaningful.

## 18. Glossary

| Term | Simple explanation |
| --- | --- |
| Modular monolith | One application organized into modules with clear boundaries; not a set of independently deployed services. |
| DTO | A data object designed for API input/output, separate from the database entity. |
| Migration | A versioned, repeatable change to the database schema, managed here with Flyway. |
| Idempotency | Repeating the same request/event does not accidentally repeat its business effect. |
| Outbox pattern | Store an event record in the same database transaction as the business change, then publish it asynchronously. |
| Dead-letter queue/path | A place to route messages that have failed processing after configured retries so they can be inspected and recovered. |
| RBAC | Role-based access control; roles define broad permissions, with ownership checks adding resource-specific authorization. |
| Reconciliation | Comparing local payment records with the payment provider's records/status to identify and correct mismatches. |
| UTC instant | A globally unambiguous point in time; display and scheduling rules still need the correct business time zone. |

## 19. Final agreement

HourlyHire will be developed as a single Java/Spring Boot modular monolith with Maven and PostgreSQL. The codebase will use 10 modules: Shared Foundation, Identity & Access, Profiles & Workforce, Discovery, Work Management, Trust & Resolution, Notifications & Events, Administration & Audit, AI, and Payments.

All planned product capabilities remain in the target scope. Development begins with the foundation and identity, then progresses through workforce data, discovery/booking, attendance/trust, events/admin, and finally AI/payments as their business rules are approved. The current deliverable is a tested local backend; deployment is deferred.


