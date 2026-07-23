# Exception Handling Guidance

Use the selected JFoundry release's domain and application failure model. Do not introduce a generic business-exception hierarchy.

## Source Of Truth

Verify exception types, persistence translation, and runtime web mapping against the selected release's API and runtime documentation. Do not infer exact exception classes, packages, or mappings from a different release.

## Choosing An Exception

| Exception | Owner and meaning | Example choice |
|---|---|---|
| Domain rule failure | Domain model or domain service: a business rule cannot be satisfied | Reject an operation because a quota or limit is exceeded or the balance is insufficient |
| Domain state failure | Aggregate or domain object: its current lifecycle state forbids the behavior | Reject shipping an already shipped order, deleting a running environment, or retrying a non-failed task |
| Invalid application input | Application use case: a command or query argument is invalid | Reject a use-case date range or option combination after transport parsing |
| Required-data absence | Application use case: required data is absent | Fail when the order that the use case must load does not exist |
| Application conflict | Application workflow: current application state, optimistic concurrency, or idempotency conflicts with the request | Reject a stale version or reused idempotency key with different input |
| Technical external-access failure | Application-owned outbound contract: a technical access or availability failure prevents use of a database, HTTP, cache, broker, file, or SDK capability | Translate a database outage or remote-client access failure while preserving its cause |

Transport parsing and bean validation belong to the primary adapter. Use the selected release's invalid-input outcome for application-owned command or query arguments, not as a replacement for adapter-local request decoding and validation.

## Boundary Rules

- Domain code must not depend on application exceptions or HTTP concepts. Do not force a domain-owned port to depend on application exceptions. Prefer loading external data in the application service and passing the value into the domain model; when a domain-owned port is justified, express failures through domain-meaningful contract semantics.
- At an application-owned outbound contract boundary, an infrastructure implementation catches known client or driver exceptions, preserves the cause, and raises the selected release's technical-access outcome when the contract expects it. In Hexagonal Architecture this is normally an outbound contract and adapter boundary; Onion does not require those role names. Do not catch broad exceptions for this translation.
- Use the selected release's documented persistence-failure translation boundary. Translate a duplicate key to the application-conflict outcome only when the adapter can identify the violated constraint as the intended business conflict. Do not treat every duplicate or integrity failure in a multi-table aggregate as “aggregate already exists.”
- Do not add runtime persistence-exception dependencies to application or domain code.
- Represent expected remote outcomes such as absence, business rejection, or conflict in the outbound contract result. The application interprets them as the selected release's absence, conflict, or domain outcome as appropriate; they are not automatically technical-access failures.
- Let expected domain and application exceptions pass through without indiscriminate wrapping.
- Leave unexpected programming and configuration defects unexpected so runtime handling can produce a server error and diagnostic logging. Do not disguise them as business, invalid-input, or technical-access outcomes.
- Do not put credentials, tokens, connection strings, personal data, or other secrets in exception messages.

## Spring MVC Boundary

For Spring MVC HTTP APIs, resolve the selected release's supported web assembly and exception mapper from its runtime guide. HTTP status and response shape remain primary-adapter concerns; domain and application code should not select status codes. Verify the release's mappings and client-visible detail policy before relying on them. Never include secrets or raw external-system details in client-visible messages.

## Testing

- Unit-test domain rules and lifecycle guards for the expected domain exception type.
- Test application services for invalid input, absence, conflict, and expected remote-outcome interpretation.
- Test that a known technical client or driver failure becomes the selected technical-access outcome with the identical cause.
- Test that expected domain/application exceptions pass through and unrelated programming defects remain unwrapped.
- Test the selected runtime's documented HTTP mappings, verify raw external details are absent where the mapper promises that protection, and keep client-visible exception messages safe.
