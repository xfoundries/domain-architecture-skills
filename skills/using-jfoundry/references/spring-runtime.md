# Spring Runtime Guidance

Use this reference only when a business project explicitly selects Spring Framework, Spring Boot, or a Spring-specific jfoundry starter.

## Dependency Management

- Import `jfoundry-spring-dependencies`, not `jfoundry-dependencies` directly. The Spring BOM imports the framework-neutral BOM and adds Spring official and Spring integration dependency management.
- Put Spring Boot starters only in the runtime assembly module/package, usually named `boot`.
- Keep `domain` and `application` modules free of Spring Boot starters. Application services may use jfoundry application contracts; runtime transaction wiring belongs in the Spring runtime adapter or assembly layer.

## Runtime Assembly

The Spring Boot runtime assembly module commonly contains:

- the Spring Boot main class;
- Spring `@Configuration`, `@ConfigurationProperties`, component scanning, and bean wiring;
- selected jfoundry Spring Boot starters;
- global MVC, event, transaction, Outbox, Inbox, or broker configuration.

Do not put business rules or aggregate behavior in the runtime assembly module.

## Transaction Boundaries

- Use `TransactionRunner` from `jfoundry-transaction-core` when an application service needs an explicit transaction block while keeping application code free of Spring `TransactionTemplate`.
- Normal application modules usually get `TransactionRunner` through `jfoundry-application-starter`; depend on `jfoundry-transaction-core` directly only when the module needs transaction contracts without the full application starter.
- `jfoundry-spring-boot-starter` brings the Spring runtime adapter. When a `PlatformTransactionManager` exists, Spring Boot auto-configuration creates a `TransactionRunner` backed by Spring `TransactionTemplate`.
- Whole-method Spring transactions may still use `@Transactional` in Spring application services. Domain objects and domain services should not control transactions directly.

## WebMVC Exception Mapping

For Spring MVC HTTP APIs, prefer `jfoundry-webmvc-spring-boot-starter`.

It maps jfoundry domain/application exceptions to RFC 9457 `ProblemDetail` responses:

- `InvalidArgumentException` -> `400 Bad Request`
- `NotFoundException` -> `404 Not Found`
- `ConflictException` and domain rule/state failures -> `409 Conflict`
- `ExternalAccessException` -> `503 Service Unavailable`

HTTP status and response shape are Web adapter concerns. Domain and application code should throw jfoundry exceptions, not expose HTTP status concepts.

## Event, Outbox, Inbox, And Messaging

- Use `jfoundry-event-spring-boot-starter` for local Spring domain event dispatch.
- Use Outbox only when domain events must be reliably published outside the process.
- Use Inbox only when duplicate message delivery must be handled idempotently.
- Use one broker starter, such as Kafka, RabbitMQ, or RocketMQ, only when production dispatch actually targets that broker.

Read `references/outbox-inbox.md` before adding Outbox or Inbox.

## Non-Spring Runtimes

Do not reuse Spring Boot starters for Quarkus, Micronaut, Helidon, CLI, or custom runtimes. Use framework-neutral jfoundry modules until an explicit runtime adapter exists.
