# Dependency Guidance

## Version First

Read `version-selection.md` and select an exact jfoundry version before choosing a BOM or copying dependency templates. Replace `JFOUNDRY_VERSION` only after that selection; never put a dynamic version selector in generated Maven configuration.

## BOM Choice

Choose the BOM by runtime binding, not by architecture style:

- Use `jfoundry-dependencies` for framework-neutral projects that need DDD, architecture annotations, application contracts, SPI, and non-Spring adapters.
- Use `jfoundry-spring-dependencies` only when the project uses Spring Framework adapters, Spring Boot auto-configuration, or Spring Boot starters. It imports `jfoundry-dependencies` and adds Spring-related dependency management.

Copy `assets/templates/maven/dependency-management-core.xml` or `assets/templates/maven/dependency-management-spring.xml` and replace `JFOUNDRY_VERSION`.

For Quarkus, Micronaut, Helidon, CLI, or custom runtimes, keep `jfoundry-dependencies` unless jfoundry provides a dedicated runtime BOM. Do not import Spring BOMs or Spring Boot starters just because the project uses jfoundry.

## Framework-Neutral Starters

Choose starters by Maven module or layer. In a serious DDD project, prefer multi-module Maven because dependency boundaries become physical build boundaries. A small project may start as a single Maven module/application artifact, but it must still preserve package boundaries and ArchUnit tests.

| Module / layer | Add | Never add |
|---|---|---|
| `domain` | `jfoundry-domain-starter` | runtime framework APIs, MyBatis-Plus, JPA, MQ clients, HTTP clients, Spring Boot starters |
| `application` | `jfoundry-application-starter` | runtime framework starters, MyBatis mappers/services, broker adapters |
| Hexagonal `infrastructure` secondary-adapter module | `jfoundry-infrastructure-mybatis-plus-starter` only for MyBatis-Plus adapters | controllers, application entry points, runtime auto-configuration starters |
| Onion outer `infrastructure` module/package | adapter starters needed by outer-ring concerns, such as persistence, web, messaging, and clients | domain model or business rule implementation |
| runtime assembly | selected runtime framework starters | domain model or business rule implementation |
| architecture tests | `jfoundry-architecture-test` with `test` scope | production scope |

Use only the capabilities the module actually needs:

- Domain module: `jfoundry-domain-starter`
- Application module: `jfoundry-application-starter`, which aggregates `jfoundry-application-core`, `jfoundry-transaction-core`, CQRS stereotypes, and the domain starter.
- Application module that only needs explicit transaction boundary contracts: `jfoundry-transaction-core`
- Application module that compiles `LockTemplate`, `LockOptions`, `DistributedLockClient`, or `@DistributedLock`: optional `jfoundry-lock-core`; `jfoundry-application-starter` does not include it.
- Infrastructure module with MyBatis-Plus repositories: `jfoundry-infrastructure-mybatis-plus-starter`
- Architecture tests: `jfoundry-architecture-test` with `test` scope

In Hexagonal projects, keep primary adapters such as controllers in `web` or `interface`, separate from the secondary-adapter `infrastructure` module. In Onion Simple projects, `infrastructure.web` is an acceptable outer-ring package because web delivery is infrastructure; still keep runtime assembly and global runtime framework configuration in `boot` when a boot module exists.

## Spring Runtime Dependencies

When the project selects Spring Framework or Spring Boot, read `references/spring-runtime.md`.

Spring-specific starters belong in the runtime assembly module/package:

- Spring Boot runtime module: `jfoundry-spring-boot-starter`
- Spring Boot MVC web/runtime module: `jfoundry-webmvc-spring-boot-starter`
- Spring Boot runtime module with MyBatis-Plus business persistence: `jfoundry-mybatis-plus-spring-boot-starter`
- Local Spring domain event dispatch: `jfoundry-event-spring-boot-starter`
- Messaging transport contracts and default logging sender: `jfoundry-messaging-spring-boot-starter`
- Kafka sender adapter: `jfoundry-messaging-kafka-spring-boot-starter`
- RabbitMQ sender adapter: `jfoundry-messaging-rabbitmq-spring-boot-starter`
- RocketMQ sender adapter: `jfoundry-messaging-rocketmq-spring-boot-starter`
- Outbox core + Spring transaction/scheduling integration: `jfoundry-outbox-spring-boot-starter`
- Outbox MyBatis-Plus store: `jfoundry-outbox-mybatis-plus-spring-boot-starter`
- Outbox JobRunr dispatcher: `jfoundry-outbox-jobrunr-spring-boot-starter`
- Inbox core + `InboxTemplate`: `jfoundry-inbox-spring-boot-starter`
- Inbox MyBatis-Plus store: `jfoundry-inbox-mybatis-plus-spring-boot-starter`

When cross-instance coordination for the same resource is required, read `references/distributed-locks.md`. Add `jfoundry-lock-redisson-spring-boot-starter` to the runtime assembly only after choosing Redisson as the distributed-lock adapter; it is optional and managed by `jfoundry-spring-dependencies`.

For Spring Boot MVC applications, use `jfoundry-webmvc-spring-boot-starter` in the runtime assembly module. It brings Spring MVC web support and the jfoundry RFC 9457 `ProblemDetail` exception mapping. Do not add `spring-boot-starter-web` separately when this starter is selected. Plain Spring Framework MVC applications use `jfoundry-webmvc-spring` in the runtime assembly and explicitly register or component-scan `ProblemDetailExceptionHandler`; do not use the Boot starter unless Spring Boot is selected.

## Template Mapping

- Use `dependency-management-core.xml` unless the project selects Spring Framework or Spring Boot.
- Use `dependency-management-spring.xml` when any selected starter is Spring-specific.
- Use `domain-module-dependencies.xml` for a dedicated domain module.
- Use `application-module-dependencies.xml` for a dedicated application module.
- Use `lock-core-dependencies.xml` in an application module only when it compiles jfoundry lock APIs.
- Use `infrastructure-mybatis-plus-dependencies.xml` for a dedicated infrastructure module using MyBatis-Plus.
- Use `architecture-test-dependencies.xml` in the module that runs ArchUnit tests, usually the runtime assembly module test source set or a dedicated architecture-test module.
- Use `spring-boot-app-dependencies.xml` only for a Spring Boot runtime assembly module.
- Use `spring-boot-webmvc-dependencies.xml` only for a Spring Boot MVC app that exposes HTTP APIs and should use jfoundry ProblemDetail exception mapping.
- Use `spring-boot-mybatis-plus-dependencies.xml` only in the Spring Boot runtime assembly module when the application uses MyBatis-Plus for business persistence.
- Use `lock-redisson-dependencies.xml` only when cross-instance same-resource coordination is required and the Spring Boot runtime selects the Redisson lock adapter.
- Use `outbox-dependencies.xml` only when reliable external publication is required and the runtime is Spring Boot.
- Use `outbox-mybatis-plus-dependencies.xml` only when Outbox uses the MyBatis-Plus store in a Spring Boot runtime.
- Use `inbox-dependencies.xml` only when consumer idempotency is required and the runtime is Spring Boot.
- Use `inbox-mybatis-plus-dependencies.xml` only when Inbox uses the MyBatis-Plus store in a Spring Boot runtime.
- Use `broker-dependencies.xml` only when selecting a real Spring Boot broker adapter. Pick one broker starter unless the application truly publishes to multiple brokers.

## Avoid

- Do not add Outbox/Inbox starters by default.
- Do not add a distributed-lock starter by default; local synchronization, idempotency, or database constraints may already satisfy the concurrency requirement.
- Do not assume `jfoundry-application-starter` includes `jfoundry-lock-core`.
- Do not assume MyBatis-Plus is present just because the project uses Spring Boot.
- Do not depend directly on low-level adapter modules from business code unless the project is doing an advanced custom assembly.
- Do not put Spring Boot starters into pure domain or application modules.
- Do not copy Spring dependency templates into Quarkus, Micronaut, Helidon, CLI, or custom runtime projects.
