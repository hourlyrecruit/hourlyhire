# Define Backend Architecture

HourlyHire,backend project structure using Java, Spring Boot, Maven, PostgreSQL, and a modular monolith architecture.

The structure has **10 modules**, one Spring Boot application, and one database. All planned features remain in scope.

## 1\. Recommended project structure

```text
hourlyhire/
│
├── pom.xml
├── mvnw
├── mvnw.cmd
├── .mvn/
│
├── README.md
├── .gitignore
├── .env.example
├── docker-compose.yml
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── hourlyhire/
│   │   │
│   │   │           ├── HourlyHireApplication.java
│   │   │
│   │   │           ├── shared/
│   │   │           │   ├── config/
│   │   │           │   ├── security/
│   │   │           │   ├── exception/
│   │   │           │   ├── response/
│   │   │           │   └── observability/
│   │   │
│   │   │           ├── identity/
│   │   │           │   ├── controller/
│   │   │           │   ├── service/
│   │   │           │   ├── repository/
│   │   │           │   ├── entity/
│   │   │           │   ├── dto/
│   │   │           │   │   ├── request/
│   │   │           │   │   └── response/
│   │   │           │   └── enums/
│   │   │
│   │   │           ├── workforce/
│   │   │           │   ├── employer/
│   │   │           │   ├── worker/
│   │   │           │   ├── category/
│   │   │           │   ├── availability/
│   │   │           │   └── servicearea/
│   │   │
│   │   │           ├── discovery/
│   │   │           │   ├── controller/
│   │   │           │   ├── service/
│   │   │           │   ├── repository/
│   │   │           │   └── dto/
│   │   │
│   │   │           ├── workmanagement/
│   │   │           │   ├── booking/
│   │   │           │   ├── attendance/
│   │   │           │   ├── hours/
│   │   │           │   └── completion/
│   │   │
│   │   │           ├── trust/
│   │   │           │   ├── review/
│   │   │           │   ├── complaint/
│   │   │           │   └── dispute/
│   │   │
│   │   │           ├── notification/
│   │   │           │   ├── controller/
│   │   │           │   ├── service/
│   │   │           │   ├── repository/
│   │   │           │   ├── entity/
│   │   │           │   ├── channel/
│   │   │           │   └── event/
│   │   │
│   │   │           ├── administration/
│   │   │           │   ├── controller/
│   │   │           │   ├── service/
│   │   │           │   ├── audit/
│   │   │           │   └── reporting/
│   │   │
│   │   │           ├── ai/
│   │   │           │   ├── controller/
│   │   │           │   ├── service/
│   │   │           │   ├── provider/
│   │   │           │   └── dto/
│   │   │
│   │   │           └── payment/
│   │   │               ├── controller/
│   │   │               ├── service/
│   │   │               ├── repository/
│   │   │               ├── entity/
│   │   │               ├── provider/
│   │   │               ├── webhook/
│   │   │               └── dto/
│   │
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-local.yml
│   │       ├── application-test.yml
│   │       └── db/
│   │           └── migration/
│   │               ├── V1__create_identity_tables.sql
│   │               ├── V2__create_workforce_tables.sql
│   │               ├── V3__create_booking_tables.sql
│   │               └── ...
│   │
│   └── test/
│       ├── java/
│       │   └── com/
│       │       └── hourlyhire/
│       │           ├── identity/
│       │           ├── workforce/
│       │           ├── discovery/
│       │           ├── workmanagement/
│       │           ├── trust/
│       │           ├── notification/
│       │           ├── administration/
│       │           ├── ai/
│       │           └── payment/
│       │
│       └── resources/
│           └── application-test.yml
│
└── docs/
    ├── project-requirements.md
    ├── architecture.md
    ├── api-guidelines.md
    ├── database-guidelines.md
    └── business-decisions.md
```

## 2\. What each module does

| Module | Responsibility |
|--------|--------|
| `shared` | Common technical configuration, security setup, errors, responses, logging and request correlation |
| `identity` | Registration, login, JWT, refresh tokens, password reset, email verification and roles |
| `workforce` | Employer and worker profiles, categories, skills, availability and service areas |
| `discovery` | Search and filtering for relevant workers |
| `workmanagement` | Bookings, attendance, working hours, approvals and completion |
| `trust` | Reviews, ratings, complaints and disputes |
| `notification` | Notification delivery, event handling, retries and delivery tracking |
| `administration` | Administrative workflows, audit records and reporting |
| `ai` | AI provider integration and approved AI-assisted features |
| `payment` | Payment records, fees, refunds, payouts, provider integration and reconciliation |

## 3\. How to organize code inside a module

For example, the `booking` feature could use\:

```text
workmanagement/
└── booking/
    ├── controller/
    │   └── BookingController.java
    ├── service/
    │   └── BookingService.java
    ├── repository/
    │   └── BookingRepository.java
    ├── entity/
    │   └── Booking.java
    ├── dto/
    │   ├── request/
    │   │   └── CreateBookingRequest.java
    │   └── response/
    │       └── BookingResponse.java
    ├── enums/
    │   └── BookingStatus.java
    └── mapper/
        └── BookingMapper.java
```

- **Controller\:** accepts HTTP requests and returns API responses.
- **Service\:** implements business rules and coordinates transactions.
- **Repository\:** accesses the database.
- **Entity\:** represents persisted data.
- **DTO\:** defines the data accepted or returned by the API.
- **Enum\:** defines controlled values, such as booking status.
- **Mapper\:** converts between entities and DTOs when useful.

Use these folders only when needed; don't create empty boilerplate classes for every feature.

## 4\. Important architecture rules

1. **One application\:** all modules run inside the same Spring Boot application.
2. **One Maven project\:** no separate Maven project per module at this stage.
3. **One PostgreSQL database\:** use Flyway migrations to manage schema changes.
4. **Module ownership\:** a module should access its own repositories. Other modules should call its application services or use defined events.
5. **API versioning\:** REST endpoints start with `/api/v1`\.
6. **Security\:** use authentication, role checks, ownership checks and organization permissions where applicable.
7. **Kafka\:** introduce it only for workflows that benefit from asynchronous processing.
8. **Local development only\:** `docker-compose.yml` may run local dependencies such as PostgreSQL or Kafka. Deployment infrastructure is not part of the current scope.
9. **Configuration\:** never commit real passwords, tokens, API keys or payment secrets.
10. **Testing\:** maintain unit tests and integration tests for each module as it is implemented.

**My recommendation\:** Start with the project skeleton, `shared`, and `identity`\. Then build `workforce`, followed by `discovery` and `workmanagement`\. Add the remaining modules in phases while keeping the application runnable and tested.

# HourlyHire API Standards

For HourlyHire, I recommend defining one consistent API standard that every backend module follows. The goal is simple: whether a team member works on Identity, Workforce, Booking, Payments, or any other module, the API should behave in a predictable way.

This is a proposed implementation standard aligned with our agreed Spring Boot modular monolith architecture.

## 1\. API design rules

| Standard | HourlyHire rule | Example |
|--------|--------|--------|
| Base path | All APIs start with `/api/v1` | `/api/v1/workers` |
| Naming | Use lowercase, plural resource names | `/api/v1/bookings` |
| HTTP methods | Use methods according to the operation | `GET`, `POST`, `PATCH`, `DELETE` |
| Request/response | Use DTOs, never expose JPA entities directly | `CreateBookingRequest` |
| JSON | Use camelCase field names | `workerId`, `startTime` |
| Dates | ISO 8601 timestamps with timezone/UTC representation | `2026-10-21T09:00:00Z` |
| Pagination | Use consistent pagination parameters | `?page=0&size=20` |
| Validation | Validate incoming data and business rules | Required fields, valid date range |
| Errors | Return a consistent error structure | `code`, `message`, `path` |
| Security | Enforce authentication, authorization, and ownership | A worker cannot edit another worker's profile |
| Documentation | Document endpoints and schemas using OpenAPI | Springdoc Swagger UI |

### HTTP method conventions

| Method | Use | Example |
|--------|--------|--------|
| `GET` | Read a resource or list | `GET /api/v1/bookings/123` |
| `POST` | Create a resource or execute a defined action | `POST /api/v1/bookings` |
| `PUT` | Replace a resource representation, if full replacement is supported | `PUT /api/v1/workers/123/profile` |
| `PATCH` | Partially update a resource | `PATCH /api/v1/workers/123/profile` |
| `DELETE` | Delete a resource where deletion is permitted | `DELETE /api/v1/workers/123/availability/456` |

Use `PATCH` for normal partial updates. For business actions, explicit action endpoints can be clearer than changing status fields directly. For example, `POST /api/v1/bookings/123/cancel` rather than allowing a client to set `status: "CANCELLED"` arbitrarily.

## 2\. Standard success response

I recommend using a consistent envelope for successful API responses, with the actual resource inside `data`\.

Example: `GET /api/v1/workers/123`

```json
{
  "success": true,
  "message": "Worker retrieved successfully",
  "data": {
    "id": "wrk_123",
    "fullName": "Alex Morgan",
    "skills": ["Warehouse", "Packing"],
    "active": true
  },
  "timestamp": "2026-10-21T09:30:00Z",
  "path": "/api/v1/workers/wrk_123"
}
```

Field definitions\:

| Field | Required? | Purpose |
|--------|--------|--------|
| `success` | Yes | Indicates the request succeeded |
| `message` | Optional | Short, human-readable message |
| `data` | Yes for resource responses | Returned resource or result |
| `timestamp` | Yes | Time the response was generated |
| `path` | Yes | API path associated with the response |

**Important\:** Keep `data` flexible. It can hold an object, an array, or a small result such as a token response. Avoid putting business logic in the response envelope.

For an operation with no response body, such as a successful deletion, use HTTP `204 No Content` and return no JSON body. Don't force an envelope where the HTTP standard says there should be no content.

## 3\. Standard error response

All modules should return errors in the same structure. Do not let each controller invent its own error format.

Example: a booking request conflicts with an existing booking.

HTTP status: `409 Conflict`

```json
{
  "success": false,
  "error": {
    "code": "BOOKING_TIME_CONFLICT",
    "message": "The worker is already booked during the requested time.",
    "details": []
  },
  "timestamp": "2026-10-21T09:30:00Z",
  "path": "/api/v1/bookings",
  "correlationId": "req-a81f92"
}
```

Recommended error fields\:

| Field | Purpose |
|--------|--------|
| `success` | Always `false` |
| `error.code` | Stable machine-readable code for client handling |
| `error.message` | Safe, readable explanation |
| `error.details` | Optional list of field-level or specific error details |
| `timestamp` | Error response time in UTC |
| `path` | Request path |
| `correlationId` | ID for tracing the request through logs |

The `code` is more important for application logic than the message. A frontend should handle `BOOKING_TIME_CONFLICT` reliably even if the message wording changes.

### Common error examples

400 **Bad Request - invalid input**

```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_FAILED",
    "message": "Please correct the invalid fields.",
    "details": [
      {
        "field": "email",
        "code": "INVALID_FORMAT",
        "message": "Enter a valid email address."
      }
    ]
  },
  "timestamp": "2026-10-21T09:30:00Z",
  "path": "/api/v1/auth/register",
  "correlationId": "req-001"
}
```

401 **Unauthorized - authentication missing or invalid**

```json
{
  "success": false,
  "error": {
    "code": "UNAUTHENTICATED",
    "message": "Authentication is required.",
    "details": []
  },
  "timestamp": "2026-10-21T09:30:00Z",
  "path": "/api/v1/bookings",
  "correlationId": "req-002"
}
```

403 **Forbidden - user lacks permission**

```json
{
  "success": false,
  "error": {
    "code": "ACCESS_DENIED",
    "message": "You do not have permission to perform this action.",
    "details": []
  },
  "timestamp": "2026-10-21T09:30:00Z",
  "path": "/api/v1/workers/wrk_123/profile",
  "correlationId": "req-003"
}
```

404 **Not Found - resource does not exist or is not accessible**

```json
{
  "success": false,
  "error": {
    "code": "WORKER_NOT_FOUND",
    "message": "The requested worker was not found.",
    "details": []
  },
  "timestamp": "2026-10-21T09:30:00Z",
  "path": "/api/v1/workers/wrk_999",
  "correlationId": "req-004"
}
```

500 **Internal Server Error - unexpected failure**

```json
{
  "success": false,
  "error": {
    "code": "INTERNAL_ERROR",
    "message": "An unexpected error occurred.",
    "details": []
  },
  "timestamp": "2026-10-21T09:30:00Z",
  "path": "/api/v1/bookings",
  "correlationId": "req-005"
}
```

For security, don't expose stack traces, SQL errors, internal class names, secrets, or sensitive personal information in error responses.

## 4\. HTTP status code standard

Use these consistently across all modules.

| Status | When to use |
|--------|--------|
| `200 OK` | Successful read or update returning a response |
| `201 Created` | Resource successfully created |
| `202 Accepted` | Request accepted for asynchronous processing, not yet completed |
| `204 No Content` | Successful operation with no response body |
| `400 Bad Request` | Invalid input or malformed request |
| `401 Unauthorized` | Missing, expired, or invalid authentication |
| `403 Forbidden` | Authenticated user lacks permission |
| `404 Not Found` | Requested resource is not found |
| `409 Conflict` | Booking overlap, duplicate operation, or state conflict |
| `422 Unprocessable Content` | Optional: syntactically valid request that fails a defined semantic rule, if the team chooses to distinguish it from `400` |
| `429 Too Many Requests` | Rate limit exceeded |
| `500 Internal Server Error` | Unexpected server-side failure |
| `503 Service Unavailable` | Service temporarily unavailable |

For simplicity, I recommend using `400` for most request-validation failures and `409` for state or concurrency conflicts. Use `422` only if the team adopts it consistently.

## 5\. Request validation standard

Validate input at the API boundary using Jakarta Bean Validation. Enforce business rules again in the service layer.

Example: create a booking.

`POST /api/v1/bookings`

Request body\:

```json
{
  "workerId": "wrk_123",
  "startTime": "2026-10-25T09:00:00Z",
  "endTime": "2026-10-25T17:00:00Z",
  "locationId": "loc_456",
  "notes": "Warehouse packing shift"
}
```

Validation requirements\:

- `workerId` is required and must identify an eligible worker.
- `startTime` and `endTime` are required and must be valid timestamps.
- `endTime` must be after `startTime`\.
- `locationId` must be valid and accessible to the caller.
- `notes` must meet the defined maximum length.
- The backend must check availability and booking overlap again during creation, not rely on a previous search result.

Successful response: `201 Created`

```json
{
  "success": true,
  "message": "Booking created successfully",
  "data": {
    "id": "bkg_789",
    "workerId": "wrk_123",
    "startTime": "2026-10-25T09:00:00Z",
    "endTime": "2026-10-25T17:00:00Z",
    "status": "PENDING"
  },
  "timestamp": "2026-10-21T09:30:00Z",
  "path": "/api/v1/bookings"
}
```

`PENDING` is an illustrative initial state. The final booking states and transition rules must follow the approved HourlyHire business policy.

## 6\. Pagination and list response standard

Every API that returns a potentially large collection should support pagination.

Example request\:

```http
GET /api/v1/workers?page=0&size=20&sort=createdAt,desc
```

Example response\:

```json
{
  "success": true,
  "message": "Workers retrieved successfully",
  "data": [
    {
      "id": "wrk_123",
      "fullName": "Alex Morgan"
    },
    {
      "id": "wrk_124",
      "fullName": "Jamie Lee"
    }
  ],
  "pagination": {
    "page": 0,
    "size": 20,
    "totalElements": 52,
    "totalPages": 3,
    "hasNext": true,
    "hasPrevious": false
  },
  "timestamp": "2026-10-21T09:30:00Z",
  "path": "/api/v1/workers"
}
```

Rules\:

- `page` starts at `0`\.
- Default `size` is `20`; set a maximum, such as `100`\.
- Use an allowlist of supported sort fields to prevent invalid or unsafe sorting.
- Apply authorization and visibility rules before returning results.
- Keep the pagination format consistent for Discovery, Bookings, Notifications, and Admin APIs.

## 7\. Authentication standard

Protected endpoints should use the standard Bearer token header.

```http
GET /api/v1/bookings
Authorization: Bearer <access_token>
Accept: application/json
```

For requests with a body\:

```http
POST /api/v1/bookings
Authorization: Bearer <access_token>
Content-Type: application/json
Accept: application/json
```

The backend must verify\:

1. The token is valid and not expired or revoked under the applicable token rules.
2. The user has the required role or permission.
3. The user owns the resource or belongs to the relevant organization, where applicable.
4. The operation is permitted in the resource's current state.

Authentication and authorization errors must follow the common error response format, including when rejected by Spring Security filters before a controller is called.

## 8\. API naming conventions

| Item | Convention | Example |
|--------|--------|--------|
| URL resources | Lowercase plural nouns | `/api/v1/workers` |
| Path IDs | Resource identifier | `/api/v1/workers/{workerId}` |
| JSON fields | `camelCase` | `startTime` |
| Java classes | `PascalCase` | `BookingService` |
| Java methods and fields | `camelCase` | `createBooking()` |
| Database tables | Consistent `snake_case` | `worker_profiles` |
| Enum values | `UPPER_SNAKE_CASE` | `PENDING_APPROVAL` |
| Error codes | Stable `UPPER_SNAKE_CASE` | `BOOKING_TIME_CONFLICT` |
| API version | Version in URL | `/api/v1/...` |

Use ISO 8601 timestamps with an explicit offset or UTC `Z`\. For monetary values, return a clear decimal representation and currency, for example\:

```json
{
  "amount": "1250.00",
  "currency": "INR"
}
```

## 9\. Recommended Spring Boot implementation

Use a small shared API infrastructure rather than implementing response handling separately in every module.

**`shared/response/`**

`ApiResponse.java`, `ApiErrorResponse.java`, `FieldErrorDetail.java`, `PaginationMetadata.java`

---

**`shared/exception/`**

`GlobalExceptionHandler.java`, `BusinessException.java`, `ResourceNotFoundException.java`, domain-specific exceptions as needed.

---

**`shared/security/`**

Security configuration, JWT authentication handling, and consistent `401` / `403` responses.

Use Spring's `ResponseEntity` where you need to choose the HTTP status and headers. Use `@RestControllerAdvice` to map validation errors, known business exceptions, missing resources, and unexpected exceptions into the standard error format.

One practical design choice: keep a success envelope for ordinary JSON responses, but don't wrap `204 No Content` responses. For errors, maintain one common envelope everywhere.

## 10\. Final standard to share with your team

These are the rules I would put into the HourlyHire backend guidelines\:

- All APIs use `/api/v1`\.
- All JSON responses follow the agreed success or error structure.
- All validation errors return consistent field-level details.
- All endpoints use correct HTTP status codes.
- All list endpoints support consistent pagination where appropriate.
- All controllers use DTOs rather than exposing persistence entities.
- All protected operations enforce permissions and ownership.
- All errors include a stable error code and correlation ID.
- All endpoints are documented in OpenAPI.
- All modules follow the same conventions.

---
