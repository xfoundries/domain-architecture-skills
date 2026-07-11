---
name: using-jfoundry
description: Guide AI agents and developers when starting or modifying Java business projects that use the jfoundry framework. Use for Maven dependency selection, Hexagonal or Onion package layout, jMolecules/JFoundry architecture annotations, aggregate repository versus read-side port decisions, Outbox/Inbox integration, and ArchUnit test setup. Do not use for maintaining jfoundry internals.
---

# Using JFoundry

## Purpose

Use this skill to build business applications on jfoundry without drifting from the framework's intended architecture. It is for application projects, not for changing the jfoundry framework internals.

For straightforward project scaffolding with no prior architecture decision request, prefer a new Maven project on a Java version compatible with the selected jfoundry release and runtime, using Hexagonal Architecture unless the user explicitly chooses Onion. This is a scaffolding default, not an architecture analysis conclusion. When the user asks for architecture analysis, ADRs, domain modeling, architecture style selection, or similar design work, do not treat Hexagonal as preselected; evaluate Layered, Onion, Hexagonal, and CQRS applicability from the domain model and integration constraints first. Do not default the runtime framework to Spring Boot; choose Spring only when the user selects Spring Framework, Spring Boot, or a Spring-specific starter. Prefer copying the bundled templates first, then adapting names and packages.

## First-Time Workflow

1. Identify the project shape: single Maven module/application artifact, multi-module Maven app, or dedicated domain/application/infrastructure Maven modules. Prefer multi-module Maven for normal DDD projects, but do not turn every Hexagonal package role into a Maven module without a build, ownership, or deployment reason.
2. Choose one primary architecture style. For direct scaffolding, prefer Hexagonal for new business projects; choose Onion only when the user asks for it or the codebase already uses it. For architecture analysis or ADR work, compare candidate styles first and document the decision before selecting templates. Do not mix Hexagonal and Onion in the same ArchUnit analysis scope.
3. Read `references/version-selection.md`. For an existing project, preserve its selected jfoundry version; for a new project, preserve a user-specified version or resolve an exact stable version as directed there.
4. Read `references/dependencies.md`, choose the framework-neutral or runtime-specific BOM, and copy the matching Maven template snippets from `assets/templates/maven/`.
5. Read `references/architecture.md` and copy the matching package structure from `assets/templates/structure/`.
6. Copy one architecture test template from `assets/templates/java/`, replace `PACKAGE_NAME`, and add it under the business project's test source set.
7. Read `references/repository-and-ports.md` before creating aggregate repositories, read-side ports, query ports, lookup ports, read models, or maintenance ports.
8. Read `references/persistence-data-converters.md` before implementing `AggregateData`, `DataConverter`, MyBatis-Plus data objects, or MapStruct converters.
9. Read `references/spring-runtime.md` when the selected runtime is Spring Framework or Spring Boot.
10. Read `references/distributed-locks.md` only when the use case requires cross-instance coordination for the same resource.
11. Read `references/outbox-inbox.md` only when the project needs reliable external event publication or idempotent message consumption.
12. Run the smallest relevant Maven verification command, usually `mvn test`, or a module-scoped `mvn -pl <module> test`.

## Core Rules

- Keep domain code free of Spring, MyBatis, persistence models, message broker clients, and framework lifecycle APIs.
- Model business behavior in the domain when rules and invariants are meaningful; use simpler CRUD or transaction scripts for low-complexity areas.
- For non-trivial domain behavior, confirm aggregate, command, invariant, event, repository, outbound-port ownership, and read-side assumptions before coding; keep open domain questions visible.
- Put use case orchestration and transaction-facing workflow in the application layer.
- Express outbound needs as secondary ports. Put each port near the core code that owns the need: application workflow ports in application modules, narrow domain-decision ports in domain modules. Put MyBatis, JPA, Redis, HTTP clients, MQ clients, and other technology details in infrastructure adapters.
- Use jfoundry's compact exception model instead of creating a generic `BusinessException` hierarchy. Domain code throws only `DomainException` subtypes (`DomainRuleViolationException`, `DomainStateException`). Application code may throw `InvalidArgumentException`, `NotFoundException`, `ConflictException`, and `ExternalAccessException`.
- In infrastructure modules, group adapters by technical shape before external-system name when that improves clarity, such as `persistence`, `query`, `client.<external-system>`, `messaging`, `cache`, and `file`. Use `query` as the neutral default for read-side query adapters; reserve `readmodel` for projects that explicitly use read-model terminology. Keep HTTP/API `*Request` and `*Response` DTOs in primary adapter packages; use application-owned `*Command` and `*Result` models for primary-port boundaries when that naming fits the project.
- In jfoundry Hexagonal projects, primary adapters such as controllers, message listeners, CLI commands, and schedulers must call primary ports or application services, not secondary adapters directly. In Onion projects, outer infrastructure/web/messaging code should call application services, not persistence or client adapters directly.
- Use aggregate repositories for aggregate lifecycle and command-side aggregate loading. For non-aggregate reads, prefer read-side ports and split them into lookup/query/maintenance roles only when that distinction helps the project.
- Keep persistence data converters infrastructure-local. Prefer MapStruct `@Mapper` interfaces with `INSTANCE`; keep `toEntity(...)` explicit and call aggregate `restore(...)`.
- Enable Outbox only for reliable publication to an external process or broker. Use local domain event dispatch when events stay in-process.
- Enable Inbox only when a consumer must handle duplicate delivery safely.
- Add ArchUnit tests early. They are part of the project skeleton, not a late cleanup.

## Runtime Rules

- Do not assume Spring Boot. If the user has not selected Spring Framework, Spring Boot, or a Spring-specific starter, keep the project on framework-neutral jfoundry modules.
- Treat `boot` as a runtime assembly name, not a Spring-only concept. For non-Spring runtimes, it may contain Helidon, Micronaut, Quarkus, CLI, or custom runtime wiring.
- Put runtime framework starters only in the runtime assembly module/package, never in domain or application modules.
- For Spring Framework or Spring Boot projects, read `references/spring-runtime.md` before selecting Spring BOMs, Spring Boot starters, WebMVC ProblemDetail mapping, event/outbox/inbox auto-configuration, or transaction integration.
- For Quarkus, Micronaut, Helidon, or another runtime, use only framework-neutral jfoundry dependencies unless jfoundry has an explicit adapter for that runtime. Do not copy Spring starter snippets into non-Spring projects.

## Bundled Templates

Copy templates instead of rewriting them from memory:

- `assets/templates/java/HexagonalArchitectureTest.java`
- `assets/templates/java/OnionSimpleArchitectureTest.java`
- `assets/templates/maven/dependency-management-core.xml`
- `assets/templates/maven/dependency-management-spring.xml`
- `assets/templates/maven/domain-module-dependencies.xml`
- `assets/templates/maven/application-module-dependencies.xml`
- `assets/templates/maven/infrastructure-mybatis-plus-dependencies.xml`
- `assets/templates/maven/architecture-test-dependencies.xml`
- `assets/templates/maven/spring-boot-app-dependencies.xml`
- `assets/templates/maven/spring-boot-webmvc-dependencies.xml`
- `assets/templates/maven/spring-boot-mybatis-plus-dependencies.xml`
- `assets/templates/maven/lock-core-dependencies.xml`
- `assets/templates/maven/lock-redisson-dependencies.xml`
- `assets/templates/maven/outbox-dependencies.xml`
- `assets/templates/maven/outbox-mybatis-plus-dependencies.xml`
- `assets/templates/maven/inbox-dependencies.xml`
- `assets/templates/maven/inbox-mybatis-plus-dependencies.xml`
- `assets/templates/maven/broker-dependencies.xml`
- `assets/templates/structure/hexagonal-package-structure.txt`
- `assets/templates/structure/onion-simple-package-structure.txt`

Replace placeholders such as `PACKAGE_NAME`. Replace `JFOUNDRY_VERSION` only after selecting the exact version through `references/version-selection.md`. Keep optional snippets optional; do not add distributed-lock, Outbox, Inbox, MyBatis-Plus, or broker starters unless the use case needs them.

## Reference Routing

- Read `references/first-use.md` when the user is starting a new project or asks how to invoke this skill.
- Read `references/version-selection.md` before selecting dependency templates or changing a jfoundry version in an existing project.
- Read `references/architecture.md` for architecture style selection, Maven module versus package role boundaries, package roles, annotations, and dependency direction.
- Read `references/dependencies.md` for starter selection and Maven snippets.
- Read `references/spring-runtime.md` for Spring Framework / Spring Boot dependency selection, Spring WebMVC exception mapping, and Spring runtime wiring rules.
- Read `references/distributed-locks.md` only when cross-instance coordination is required for the same resource.
- Read `references/repository-and-ports.md` before modeling persistence, aggregate repositories, read models, or query ports.
- Read `references/persistence-data-converters.md` before writing `DataConverter` implementations, persistence data objects, or MapStruct mapping rules.
- Read `references/outbox-inbox.md` before adding event externalization, broker adapters, Outbox tables, dispatchers, or consumer idempotency.
- Read `references/testing.md` before adding or changing architecture tests.

## Common First Prompt

When guiding direct project scaffolding, start by asking for the base package, project/module shape, runtime stack, persistence choice, and whether external messaging is required. If the user has no preference and has not requested architecture analysis first, proceed with:

- a Java version compatible with the selected jfoundry release and runtime
- Maven
- no runtime framework binding yet
- Hexagonal Architecture
- `jfoundry-dependencies` BOM
- `jfoundry-domain-starter`
- `jfoundry-application-starter`
- `JFoundryRules.hexagonalStrict()` and `JFoundryRules.jmoleculesDdd()`

If the user asks for architecture analysis, ADRs, domain modeling, or architecture style selection, do not use these defaults as conclusions. First model the domain and compare architecture styles, then apply the selected jfoundry template.

Suggested user prompt for a new business project:

```text
Use $using-jfoundry to create the initial architecture for a new Java business project.
Base package: com.example.order
Project shape: multi-module Maven
Runtime: undecided
Persistence: MyBatis-Plus
Messaging: Kafka later, not in the initial skeleton
Architecture: default unless you need to choose
```

If details are missing, ask one concise question or proceed with the defaults above when the choice is low risk.
