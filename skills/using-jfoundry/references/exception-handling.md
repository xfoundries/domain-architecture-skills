# Exception Handling Guidance

Use jfoundry's compact domain and application exception model. Do not introduce a generic `BusinessException` hierarchy.

## Source Of Truth

Verify framework behavior against these jfoundry modules and packages:

- Domain exceptions: `jfoundry-domain/src/main/java/org/jfoundry/domain/exception/` (`DomainException`, `DomainRuleViolationException`, and `DomainStateException`)
- Application exceptions: `jfoundry-application/jfoundry-application-core/src/main/java/org/jfoundry/application/exception/` (`ApplicationException`, `InvalidArgumentException`, `NotFoundException`, `ConflictException`, and `ExternalAccessException`)
- Spring WebMVC mapping: `jfoundry-spring/jfoundry-spring-runtime/jfoundry-webmvc-spring/src/main/java/org/jfoundry/webmvc/spring/ProblemDetailExceptionHandler.java` and `CoreProblemCode.java`

## Choosing An Exception

| Exception | Owner and meaning | Example choice |
|---|---|---|
| `DomainRuleViolationException` | Domain model or domain service: a business rule cannot be satisfied | Reject an operation because a quota or limit is exceeded or the balance is insufficient |
| `DomainStateException` | Aggregate or domain object: its current lifecycle state forbids the behavior | Reject shipping an already shipped order, deleting a running environment, or retrying a non-failed task |
| `InvalidArgumentException` | Application use case: a command or query argument is invalid | Reject a use-case date range or option combination after transport parsing |
| `NotFoundException` | Application use case: required data is absent | Fail when the order that the use case must load does not exist |
| `ConflictException` | Application workflow: current application state, optimistic concurrency, or idempotency conflicts with the request | Reject a stale version or reused idempotency key with different input |
| `ExternalAccessException` | Application-owned secondary port contract: a technical access or availability failure prevents use of an outbound database, HTTP, cache, broker, file, or SDK capability | Translate a database outage or remote-client access failure while preserving its cause |

Transport parsing and bean validation belong to the primary adapter. Use `InvalidArgumentException` for invalid application-owned command or query arguments, not as a replacement for adapter-local request decoding and validation.

## Boundary Rules

- Domain code must not depend on application exceptions or HTTP concepts. Do not force a domain-owned port to depend on application exceptions. Prefer loading external data in the application service and passing the value into the domain model; when a domain-owned port is justified, express failures through domain-meaningful contract semantics.
- At an application-owned secondary port boundary, an infrastructure adapter catches known client or driver exceptions, preserves the cause, and throws `ExternalAccessException` when the port contract expects it. Do not catch broad `Exception` or `RuntimeException` for this translation.
- For jfoundry aggregate repositories, prefer the runtime-neutral `PersistenceFailureTranslator` boundary supplied by `AbstractAggregateRepository`. Core defaults to pass-through behavior. The optional Spring runtime translates only known resource, transient-resource, and timeout failures; it intentionally leaves duplicate keys, integrity and locking failures, SQL or mapper defects, and unknown exceptions unchanged.
- Translate a duplicate key to `ConflictException` in the business persistence adapter only when the adapter can identify the violated constraint as the intended business conflict. Do not treat every duplicate or integrity failure in a multi-table aggregate as “aggregate already exists.”
- Query adapters and other persistence ports that do not extend `AbstractAggregateRepository` may invoke the same translator explicitly with the matching `PersistenceOperation`; do not add a Spring `DataAccessException` dependency to application or domain code.
- Represent expected remote outcomes such as absence, business rejection, or conflict in the port result or return contract. The application interprets them as `NotFoundException`, `ConflictException`, or a domain decision as appropriate; they are not automatically `ExternalAccessException` failures.
- Let expected domain and application exceptions pass through without indiscriminate wrapping.
- Leave unexpected programming and configuration defects unexpected so runtime handling can produce a `500` response and diagnostic logging. Do not disguise them as `BusinessException`, `InvalidArgumentException`, or `ExternalAccessException`.
- Do not put credentials, tokens, connection strings, personal data, or other secrets in exception messages.

## Spring MVC Boundary

For Spring Boot MVC HTTP APIs, use `jfoundry-webmvc-spring-boot-starter`. For plain Spring Framework MVC, add `jfoundry-webmvc-spring` to the runtime assembly and explicitly register or component-scan `ProblemDetailExceptionHandler`. Read `references/spring-runtime.md` for detailed runtime setup.

The built-in handler maps exceptions to RFC 9457 `ProblemDetail` responses:

| Exception | HTTP status |
|---|---|
| `InvalidArgumentException` | `400 Bad Request` |
| `NotFoundException` | `404 Not Found` |
| `ConflictException` | `409 Conflict` |
| `DomainRuleViolationException` | `422 Unprocessable Entity` |
| `DomainStateException` | `409 Conflict` |
| `ExternalAccessException` | `503 Service Unavailable` |

HTTP status and response shape are primary-adapter concerns; domain and application code should not select status codes. The built-in handler exposes the exception message as the detail for the other five mapped exceptions, so domain and application messages must be client-safe. For `ExternalAccessException`, it uses a safe default detail rather than exposing the raw exception message. Never include secrets or raw external-system details in client-visible messages.

## Testing

- Unit-test domain rules and lifecycle guards for the expected domain exception type.
- Test application services for argument, absence, conflict, and expected remote-outcome interpretation.
- Test that a known technical client or driver failure becomes `ExternalAccessException` with the identical cause.
- Test that expected domain/application exceptions pass through and unrelated programming defects remain unwrapped.
- Test Spring WebMVC `ProblemDetail` status mappings, verify raw external details are absent for `ExternalAccessException`, and keep the other five exception messages client-safe.
