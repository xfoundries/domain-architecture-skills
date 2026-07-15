---
name: using-jfoundry
description: Guide AI agents and developers when starting or modifying Java business projects that use the jfoundry framework. Use for Maven dependency selection, Hexagonal or Onion package layout, jMolecules/JFoundry architecture annotations, aggregate repository versus read-side contract or port decisions, Outbox/Inbox integration, and ArchUnit test setup. Do not use for maintaining jfoundry internals.
---

# Using JFoundry

## Purpose

Use this skill to build business applications on jfoundry without drifting from the framework's intended architecture. It is for application projects, not for changing the jfoundry framework internals.

Resolve the primary architecture style from a confirmed `Architecture Guidance Result`, existing project evidence, established conventions that are sufficient for the requested change, or an explicit user choice. Do not select Hexagonal, Onion, Layered, CQRS, or another style merely because no earlier decision is available. When the missing choice materially changes dependency direction, package or module placement, templates, or architecture tests, return `needs-input` and recommend `domain-architecture-guidance` rather than guessing. Do not default the runtime framework to Spring Boot; choose Spring only when the user selects Spring Framework, Spring Boot, or a Spring-specific starter. After the architecture is resolved, copy the matching bundled templates before adapting names and packages.

## First-Time Workflow

At task classification, for project setup, modification, or framework-landing work, read `references/implementation-guidance-result.md` before deciding `completed`, `needs-input`, or `not-applicable`. Simple conceptual explanations are excluded. If an exact version or runtime blocks an early workflow step, return the structured `needs-input` result instead of continuing to resolve landing details.

1. Identify whether a single Maven artifact, a balanced `domain` / `application` / `adapter` / `boot` module set, or a further adapter split is justified by a build, ownership, deployment, or dependency-isolation boundary. Do not turn transports or Hexagonal package directions into Maven modules by default.
2. Resolve one primary architecture style from a confirmed `Architecture Guidance Result`, project evidence, established conventions, or an explicit user choice. Preserve an existing style. For simple CRUD, continue with established or explicitly simple conventions when no new architecture decision is required. In Hexagonal, one `adapter` module may contain both `adapter.in` and `adapter.out`; messaging and schedulers remain `adapter.in` alongside web. When a real boundary justifies splitting it, use `interface` for all inbound transports and `infrastructure` for `adapter.out`. In Onion, use the inward rings and an outer `infrastructure` module instead; do not import Hexagonal `adapter.in/out` semantics. Return `needs-input` only when a missing choice blocks responsible landing. Do not mix Hexagonal and Onion in the same ArchUnit analysis scope.
3. When a runtime is selected, record the application runtime integration policy: framework-neutral contracts only, selected-runtime orchestration support at declared application boundaries, or a documented hybrid. Domain code remains runtime-free in every option. Do not block framework-neutral work while the runtime is undecided.
4. Read `references/version-selection.md`. For an existing project, preserve its selected jfoundry version; for a new project, preserve a user-specified version or resolve an exact stable version as directed there.
5. Read `references/dependencies.md`, choose the framework-neutral or runtime-specific BOM, and copy the matching Maven template snippets from `assets/templates/maven/`.
6. Read `references/architecture.md` and copy the matching package structure from `assets/templates/structure/`.
7. Copy one architecture test template from `assets/templates/java/`, replace `PACKAGE_NAME`, and add it under the business project's test source set.
8. Read `references/repository-and-read-contracts.md` before creating aggregate repositories, read-side contracts or ports, query contracts, lookup contracts, read models, or maintenance contracts.
9. Read `references/persistence-data-mappers.md` before implementing `AggregateData`, `DataMapper`, MyBatis-Plus data objects, or MapStruct mappers.
10. Read `references/spring-runtime.md` when the selected runtime is Spring Framework or Spring Boot.
11. Read `references/distributed-locks.md` only when the use case requires cross-instance coordination for the same resource.
12. Read `references/outbox-inbox.md` only when the project needs reliable external event publication or idempotent message consumption.
13. Run the smallest relevant Maven verification command, usually `mvn test`, or a module-scoped `mvn -pl <module> test`.
14. Complete the JFoundry Implementation Guidance Result with the selected artifacts, constraints, open questions, and verification commands.

## Core Rules

- Keep domain code free of Spring, MyBatis, persistence models, message broker clients, and framework lifecycle APIs.
- Application code should usually prefer jfoundry's framework-neutral transaction, lock, and messaging contracts, but a selected runtime may be used directly for an application-owned orchestration boundary when an additional abstraction adds no value. Do not let HTTP, ORM, mapper, broker-record, or client-SDK types leak into application code; keep those in adapters or infrastructure.
- Model business behavior in the domain when rules and invariants are meaningful; use simpler CRUD or transaction scripts for low-complexity areas.
- For non-trivial domain behavior, confirm aggregate, command, invariant, event, repository, outbound-dependency ownership, and read-side assumptions before coding; keep open domain questions visible.
- Put use case orchestration and transaction-facing workflow in the application layer.
- Express outbound needs in the vocabulary of the selected architecture. In Hexagonal projects, use secondary ports owned near the application or domain consumer. In Onion projects, define responsibility-named inner-ring contracts only where dependency inversion is useful; do not require `*Port` or `*Adapter` suffixes. Put MyBatis, JPA, Redis, HTTP clients, MQ clients, and other technology details in the outer infrastructure implementation.
- Use jfoundry's compact domain/application exception model; do not create a generic `BusinessException` hierarchy. Read `references/exception-handling.md` when selecting exceptions, translating failures, or mapping them at a runtime boundary.
- In infrastructure modules, group adapters by technical shape before external-system name when that improves clarity, such as `persistence`, `query`, `client.<external-system>`, `messaging`, `cache`, and `file`. Use `query` as the neutral default for read-side query adapters; reserve `readmodel` for projects that explicitly use read-model terminology. Keep HTTP/API `*Request` and `*Response` DTOs in primary adapter packages; use application-owned `*Command` and `*Result` models for primary-port boundaries when that naming fits the project.
- In jfoundry Hexagonal projects, primary adapters such as controllers, message listeners, CLI commands, and schedulers must call primary ports or application services, not secondary adapters directly. Onion itself permits an outer ring to call any inner ring; when a DDD/CQRS project selects an application boundary, document and enforce the stronger project policy that web and messaging code enter through that boundary instead of calling persistence or client implementations.
- Use aggregate repositories for aggregate lifecycle and command-side aggregate loading. For non-aggregate reads, define application-owned read contracts only where useful and name them by business capability and responsibility. Use Hexagonal port terminology only in Hexagonal projects; split lookup, query, and maintenance responsibilities only when their result shapes or change reasons diverge.
- Keep persistence-owned optimistic-lock versions and managed ORM entities out of domain aggregates. For tracked repositories, keep load and modify in one transaction; runtime integration injects `AggregatePersistenceContext`, so business constructors do not receive it. Do not assume detached aggregate merge support.
- Treat jfoundry persistence base classes as optional implementation support, not as a DDD requirement. A custom adapter may implement the aggregate repository contract directly when that is clearer.
- Use `MybatisPlusAggregateRepository` directly when one `AggregateData`, one `DataMapper`, and one `BaseMapper` fully store and restore the aggregate. When one MyBatis-Plus root Data anchors a multi-table aggregate, override complete `do*` operations and use its protected complete-operation helpers; keep aggregate restoration and dependent synchronization in the business adapter. Use `AbstractAggregateRepository` or a direct port implementation when no single MyBatis-Plus root Data exists or the helpers do not fit.
- Do not override the public `add`, `modify`, `addAll`, `modifyAll`, `remove`, or `findById` methods of jfoundry repository bases. Their protected `do*` operations are the persistence extension points.
- Keep persistence data mappers infrastructure-local. Prefer MapStruct `@Mapper` interfaces with `INSTANCE`; keep `toEntity(...)` explicit and call aggregate `restore(...)`.
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
- `assets/templates/maven/infrastructure-jpa-dependencies.xml`
- `assets/templates/maven/architecture-test-dependencies.xml`
- `assets/templates/maven/spring-boot-app-dependencies.xml`
- `assets/templates/maven/spring-boot-webmvc-dependencies.xml`
- `assets/templates/maven/spring-boot-mybatis-plus-dependencies.xml`
- `assets/templates/maven/spring-boot-jpa-dependencies.xml`
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

- For project setup, modification, or framework-landing work, read `references/implementation-guidance-result.md` at task classification before deciding result status or selecting landing details.
- Read `references/first-use.md` when the user is starting a new project or asks how to invoke this skill.
- Read `references/version-selection.md` before selecting dependency templates or changing a jfoundry version in an existing project.
- Read `references/architecture.md` for architecture style selection, Maven module versus package role boundaries, package roles, annotations, and dependency direction.
- Read `references/exception-handling.md` before choosing domain/application exceptions, translating adapter failures, or mapping errors at an HTTP/runtime boundary.
- Read `references/dependencies.md` for starter selection and Maven snippets.
- Read `references/spring-runtime.md` for Spring Framework / Spring Boot dependency selection, Spring WebMVC exception mapping, and Spring runtime wiring rules.
- Read `references/distributed-locks.md` only when cross-instance coordination is required for the same resource.
- Read `references/repository-and-read-contracts.md` before modeling persistence, aggregate repositories, read models, or read-side contracts and ports.
- Read `references/persistence-data-mappers.md` before writing `DataMapper` implementations, persistence data objects, composite aggregate adapters, or MapStruct mapping rules.
- Read `references/outbox-inbox.md` before adding event externalization, broker adapters, Outbox tables, dispatchers, or consumer idempotency.
- Read `references/testing.md` before adding or changing architecture tests.

## Common First Prompt

When guiding project scaffolding, start by asking for the base package, project/module shape, runtime stack, persistence choice, external messaging needs, and the source of the architecture decision. Explain that messaging and schedulers are inbound adapters, not evidence that a `web` module should be retained or that a separate Maven module is needed. Inspect an existing project before asking for information its ADRs, dependencies, packages, or architecture tests already establish. If no architecture is resolved and that choice materially changes the generated landing, recommend `domain-architecture-guidance` and ask the smallest blocking question.

Architecture-neutral defaults may include:

- a Java version compatible with the selected jfoundry release and runtime
- Maven
- no runtime framework binding yet
- `jfoundry-dependencies` BOM
- no optional persistence, messaging, Outbox, Inbox, or distributed-lock integration until required

Select domain/application starters, package templates, architecture annotations, and ArchUnit rules only after the relevant project roles and architecture are clear. A simple CRUD change may preserve established conventions without introducing a richer architecture. A new domain-heavy project should complete Domain Modeling and Architecture Guidance before template selection.

Suggested user prompt for a new business project:

```text
Use $using-jfoundry to create the initial architecture for a new Java business project.
Base package: com.example.order
Project shape: single module, Hexagonal domain/application/adapter/boot, a justified Hexagonal adapter split, or Onion domain/application/infrastructure/boot
Runtime: undecided
Persistence: MyBatis-Plus
Messaging: Kafka later, not in the initial skeleton
Architecture: confirmed result, existing project style, explicit choice, or undecided
```

If details are missing, ask one concise question or proceed with the architecture-neutral defaults above when the choice is low risk. Do not treat `undecided` as permission to select Hexagonal Architecture.
