# Architecture Guidance

## Style Selection

Select templates only after resolving the architecture from a confirmed `Architecture Guidance Result`, existing project evidence, established conventions sufficient for the requested change, or an explicit user choice. Preserve an existing style. Do not use a JFoundry template to make the architecture decision.

Use the Hexagonal template when Hexagonal Architecture has been selected. Its core dependency flow is:

```text
primary adapter -> primary port / application service -> domain
inside core consumer -> secondary port
secondary adapter -> secondary port
```

Use Onion Simple when Onion Architecture has been selected:

```text
infrastructure -> application -> domain
```

In Onion Simple, web/controllers are infrastructure concerns because delivery mechanisms sit in the outer ring. In Hexagonal, this skill normally separates primary adapters into `web` or `interface` and secondary adapters into `infrastructure` for clearer port/adapter roles.

For simple CRUD or another confirmed style, preserve the selected boundaries and do not force a Hexagonal or Onion template merely because those templates are bundled. Do not mix Hexagonal and Onion annotations in the same ArchUnit analysis scope.

## Hexagonal Roles

For general Hexagonal semantics, primary/secondary port direction, and application-service naming, read `domain-architecture-guidance/references/hexagonal-architecture.md`. This section only maps those roles to jfoundry annotations and package locations.

- `@Application`: marks application core code. It can include use cases, application services, and domain-facing orchestration. Do not confuse this with a Maven module named `application`; a multi-module project may split the Hexagonal inside/core across domain and application modules. Do not mark an entire domain root package as `@Application`.
- `@PrimaryPort`: marks inbound interfaces exposed to driving adapters. Put them under `port.in`, `ports.in`, `port.inbound`, `ports.inbound`, `usecase`, or `usecases`. Java examples in this skill family often name these `*UseCase`.
- `@PrimaryAdapter`: marks inbound drivers such as REST controllers, message listeners, CLI commands, schedulers, or batch triggers.
- `@SecondaryPort`: marks outbound interfaces owned by the consumer. Put application-owned ports under application `port.out`; put only narrow domain-policy ports under domain `port.out`.
- `@SecondaryAdapter`: marks implementations of secondary ports, such as MyBatis/JPA persistence, Redis, HTTP clients, broker senders, file storage, or external SDK adapters.

An aggregate repository has an independent DDD role. In a Hexagonal project it may also be marked
`@SecondaryPort`, while remaining under the domain repository package. Do not duplicate it as an
application `port.out` interface. A `@SecondaryAdapter` may implement either a regular secondary
port or a DDD repository.

In Onion, express the same dependency inversion with rings: the aggregate repository contract is
inside the domain ring and its implementation is in the infrastructure ring. Do not mix Hexagonal
port/adapter annotations into the Onion analysis scope.

## Package Defaults

For new Hexagonal projects, copy `assets/templates/structure/hexagonal-package-structure.txt`.

The template package structure describes architectural roles, not mandatory Maven modules. Do not create separate Maven modules named `adapter-in` and `adapter-out` unless the project has a real build, ownership, or deployment reason.

For normal multi-module Maven business projects, prefer a small physical module set first:

```text
PROJECT
  PROJECT-domain
  PROJECT-application
  PROJECT-infrastructure
  PROJECT-web          # or PROJECT-interface when the inbound surface is broader than HTTP
  PROJECT-boot         # runtime assembly; name may differ outside Spring Boot projects
```

Map Hexagonal roles inside those modules:

- `PROJECT-web` or `PROJECT-interface`: primary adapters such as REST controllers, admin APIs, schedulers, CLI commands, or message listeners.
- `PROJECT-application`: primary ports under `port.in`, application services, and application-owned secondary ports under `port.out`.
- `PROJECT-domain`: aggregates, value objects, domain events, aggregate repository contracts, and optional narrow domain-facing secondary ports under `port.out`.
- `PROJECT-infrastructure`: secondary adapter implementations such as persistence, query adapters, remote SDK/HTTP clients, broker senders, Redis, and file storage. Use `query` as the neutral package name for read-side query adapters; reserve `readmodel` for projects that explicitly use read-model terminology or CQRS-style read models. Group adapters by technical shape first and business feature or external system second, such as `persistence.<aggregate>`, `query.<feature>`, `client.<external-system>`, `messaging.<topic>`, `file.<feature>`, and `cache.<feature>`. Keep global runtime framework configuration out of this module when a runtime assembly module exists.
- `PROJECT-boot`: runtime assembly, dependency wiring, runtime framework configuration, and selected runtime framework starters. The name `boot` is common for Spring Boot, but the role is runtime assembly, not a Spring-only rule.

Recommended package shape:

```text
PACKAGE_NAME
  domain
    port.out        # optional: narrow ports consumed by domain services or policies
  application
    port.in
    port.out        # workflow/read ports consumed by application services
    service
  web               # or interface for non-HTTP-heavy inbound surfaces
    <feature>
      request       # HTTP/API DTOs, adapter-local
      response      # HTTP/API DTOs, adapter-local
  infrastructure    # secondary adapter implementations
    persistence
      <aggregate-or-feature>
    query
      <feature>
    client
      <external-system>
    messaging
      <topic-or-system>
    cache
      <feature>
    file
      <feature>
  boot
    config        # runtime framework configuration and bean wiring
```

Keep `domain` and `application` free of MyBatis, Spring MVC, broker clients, and persistence models. Primary adapters in `web` or `interface` call application services or primary ports. HTTP/API DTOs in primary adapters should use adapter-local names such as `*Request` and `*Response`; primary ports should use application-owned models such as `*Command` and `*Result` when that naming fits the project. Secondary adapters in `infrastructure` implement secondary ports from either the domain or application module. Put global runtime framework configuration, component scanning, and runtime bean wiring in the runtime assembly module/package, not in `infrastructure`.

For Spring projects, runtime configuration includes Spring `@Configuration`, `@ConfigurationProperties`, component scanning, and auto-configuration customization. Read `references/spring-runtime.md` before adding those dependencies or configuration classes.

For Onion Simple package layouts, use the Onion template instead of the Hexagonal template. Its `infrastructure.web` package is expected because web delivery is an outer-ring concern. When a separate runtime assembly module/package exists, keep global runtime assembly and component scanning there; use infrastructure-local config only for adapter details that are not global runtime wiring.

## Exception Boundaries

Use jfoundry's compact domain/application exception model and do not create a generic `BusinessException` hierarchy. Domain code must not depend on application exceptions or HTTP concepts. Infrastructure adapters translate technical failures at the port boundary they own while preserving the cause; do not indiscriminately wrap domain failures.

The `using-jfoundry` skill routes exception selection, adapter translation, runtime mapping, and testing decisions to `references/exception-handling.md`. Read that reference instead of duplicating its decision table here.

## Annotation Placement

Prefer package-level annotations when an entire package has one role. Use class-level annotations for incremental migration or mixed packages.

For Hexagonal, add `package-info.java` files in role packages where useful:

```java
@org.jfoundry.architecture.hexagonal.Application
package PACKAGE_NAME.application;
```

```java
@org.jfoundry.architecture.hexagonal.PrimaryPort
package PACKAGE_NAME.application.port.in;
```

```java
@org.jfoundry.architecture.hexagonal.SecondaryPort
package PACKAGE_NAME.application.port.out;
```

For a narrow domain-facing port:

```java
@org.jfoundry.architecture.hexagonal.SecondaryPort
package PACKAGE_NAME.domain.port.out;
```

```java
@org.jfoundry.architecture.hexagonal.PrimaryAdapter
package PACKAGE_NAME.web;
```

```java
@org.jfoundry.architecture.hexagonal.SecondaryAdapter
package PACKAGE_NAME.infrastructure;
```

## Do Not

- Do not put controllers, schedulers, or message listeners in the application package.
- Do not let controllers call repositories, mappers, secondary ports, or secondary adapters directly.
- Do not put MyBatis `Mapper`, `Wrapper`, `Page`, `IPage`, Spring Data repositories, or JPA specifications in domain/application signatures.
- Do not create one interface for every class. Create ports for real boundaries and outbound needs.
- Do not enable CQRS only for symmetry. Use it when command and query models actually diverge.
- Do not create custom HTTP error response wrappers for new Spring MVC jfoundry projects unless the user explicitly rejects RFC 9457 `ProblemDetail`.
