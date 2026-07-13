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

- Choose a transaction boundary only for an application workflow that needs atomic persistence changes; a transaction is not mandatory for every application service.
- Use `TransactionRunner` from `jfoundry-transaction-core` for an explicit, framework-neutral transaction block while keeping application code free of Spring `TransactionTemplate`.
- Alternatively, annotate a Spring-managed application-service method with `org.jfoundry.application.transaction.ApplicationTransactional` for a declarative whole-method boundary. The Spring interceptor delegates to `TransactionRunner`; it does not introduce a separate transaction mechanism.
- Normal application modules usually get `TransactionRunner` through `jfoundry-application-starter`; depend on `jfoundry-transaction-core` directly only when the module needs transaction contracts without the full application starter.
- `jfoundry-spring-boot-starter` brings the Spring runtime adapter. When a `PlatformTransactionManager` exists, Spring Boot auto-configuration creates a `TransactionRunner` backed by Spring `TransactionTemplate`.
- `jfoundry.application.transaction.annotation.enabled` defaults to `true`; set it to `false` to disable the `@ApplicationTransactional` advisor.
- `@ApplicationTransactional` applies only when a call enters a Spring-managed instance through its proxy. Self-invocation and unproxied instances bypass the advisor; use `TransactionRunner` when proxy interception cannot be guaranteed.
- With the Spring Boot starter, jfoundry creates `SpringTransactionRunner` after Boot has configured a `PlatformTransactionManager`, then registers the annotation advisor after a runner exists. A user-defined `TransactionRunner` takes precedence and is also eligible for the advisor.
- Keep either form in the application layer. Domain objects and domain services should not control transactions directly.
- `jfoundry-persistence-spring` provides a transaction-bound `AggregatePersistenceContext` when
  tracked persistence state is selected. Spring Boot auto-configuration injects it into aware
  repositories; business constructors do not receive it and business code does not open a scope.
  Keep aggregate load and modify/remove in the same transaction; `REQUIRES_NEW` receives an
  independent context and resumes the outer context afterward.
- MyBatis-Plus applications must still register `OptimisticLockerInnerInterceptor`; jfoundry does
  not mutate an application-owned interceptor bean. JPA applications rely on their provider's
  `@Version` handling. Neither runtime path supports detached aggregate merge by default.

## Distributed Locks

Read `references/distributed-locks.md` only when a workflow requires cross-instance coordination for the same resource. Do not add the Redisson starter or a distributed lock for ordinary in-process concurrency or as a default transaction companion.

## WebMVC Exception Mapping

For Spring Boot MVC HTTP APIs, use `jfoundry-webmvc-spring-boot-starter`. For plain Spring Framework MVC, add `jfoundry-webmvc-spring` to the runtime assembly and explicitly register or component-scan `ProblemDetailExceptionHandler`; do not use the Boot starter unless Spring Boot is selected.

It maps jfoundry domain/application exceptions to RFC 9457 `ProblemDetail` responses:

- `InvalidArgumentException` -> `400 Bad Request`
- `NotFoundException` -> `404 Not Found`
- `ConflictException` -> `409 Conflict`
- `DomainRuleViolationException` -> `422 Unprocessable Entity`
- `DomainStateException` -> `409 Conflict`
- `ExternalAccessException` -> `503 Service Unavailable`

HTTP status and response shape are Web adapter concerns. Domain and application code should throw jfoundry exceptions, not expose HTTP status concepts. The other five mappings use the exception message as the detail, so those messages must be client-safe. For `ExternalAccessException`, the built-in handler uses a safe default detail rather than exposing the raw exception message. Do not put secrets in exception messages.

## Event, Outbox, Inbox, And Messaging

- Use `jfoundry-event-spring-boot-starter` for local Spring domain event dispatch.
- Use Outbox only when domain events must be reliably published outside the process.
- Use Inbox only when duplicate message delivery must be handled idempotently.
- Use one broker starter, such as Kafka, RabbitMQ, or RocketMQ, only when production dispatch actually targets that broker.

Read `references/outbox-inbox.md` before adding Outbox or Inbox.

## Non-Spring Runtimes

Do not reuse Spring Boot starters for Quarkus, Micronaut, Helidon, CLI, or custom runtimes. Use framework-neutral jfoundry modules until an explicit runtime adapter exists.
