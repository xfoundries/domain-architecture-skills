# Spring Runtime Assembly

Use this reference only when a project explicitly selects Spring Framework, Spring Boot, or a Spring-specific jfoundry starter. Otherwise keep framework-neutral dependencies and assembly.

## Assembly Rules

- Put Spring Boot starters, the main class, global configuration, component scanning, and runtime wiring in the runtime assembly module/package, commonly `boot`.
- Keep domain code free of Spring. Keep application code free of HTTP, JPA, mapper, broker-record, and client-SDK types.
- Choose `jfoundry-spring-dependencies` and copy the selected runtime template from `assets/templates/maven/`.
- The business JPA or MyBatis-Plus starter does not imply Outbox, Inbox, a broker, or a distributed lock. Add those only when the use case selects them.
- When JPA entities sit outside the package of the Spring Boot application class, register their package with `@EntityScan`. Entity registration is separate from schema management: keep Flyway or Liquibase as the application-owned schema authority and do not use Hibernate DDL creation for jfoundry tables.

## Application Boundaries

Use a transaction boundary only for an application workflow that requires atomic changes. Keep it in application orchestration, whether the project uses a framework-neutral `TransactionRunner` or a selected Spring integration. Keep aggregate load and tracked modification in the same transaction.

For Spring MVC, HTTP response mapping remains a primary-adapter concern. Domain and application code select domain/application exceptions, not HTTP status codes. Use the JFoundry WebMVC starter only when the project chooses its `ProblemDetail` integration.

Outbox, Inbox, broker, and lock decisions remain optional capabilities. Read their specialized references before selecting a starter. For non-Spring runtimes, do not reuse Spring Boot starters.

Read `references/upstream-documentation.md` for exact auto-configuration conditions, transaction annotations, properties, proxy constraints, and exception mappings.
