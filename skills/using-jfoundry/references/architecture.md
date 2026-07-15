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

In Onion Simple, web/controllers are infrastructure concerns because delivery mechanisms sit in the outer ring. In Hexagonal, this skill places primary and secondary adapters below one selected direction pair for clearer port/adapter roles.

Onion Architecture has no Primary/Secondary Port or Adapter taxonomy and no official class-name
suffix convention. Its rings and inward dependency direction are architectural roles; class and
interface names should first follow DDD ubiquitous language, then their application, CQRS, or
technical responsibility. An inner-ring interface implemented by infrastructure is a dependency
contract, but it need not be named `*Port`, and its implementation need not be named `*Adapter`.

For simple CRUD or another confirmed style, preserve the selected boundaries and do not force a Hexagonal or Onion template merely because those templates are bundled. Do not mix Hexagonal and Onion annotations in the same ArchUnit analysis scope.

## Hexagonal Roles

For general Hexagonal semantics, primary/secondary port direction, and application-service naming, read `domain-architecture-guidance/references/hexagonal-architecture.md`. This section only maps those roles to jfoundry annotations and package locations.

- `@Application`: marks application core code. It can include use cases, application services, and domain-facing orchestration. Do not confuse this with a Maven module named `application`; a multi-module project may split the Hexagonal inside/core across domain and application modules. Do not mark an entire domain root package as `@Application`.
- `@PrimaryPort`: marks inbound interfaces exposed to driving adapters. Put them under `port.in`, `ports.in`, `port.inbound`, `ports.inbound`, `usecase`, or `usecases`. Java examples in this skill family often name these `*UseCase`.
- `@PrimaryAdapter`: marks inbound drivers such as REST controllers, message listeners, CLI commands, schedulers, or batch triggers.
- `@SecondaryPort`: marks outbound interfaces owned by the consumer. Put application-owned ports under application `port.out`; put only narrow domain-policy ports under domain `port.out`.
- `@SecondaryAdapter`: marks implementations of secondary ports, such as MyBatis/JPA persistence, Redis, HTTP clients, broker senders, file storage, or external SDK adapters.

These annotations define Hexagonal roles; they do not impose suffixes. Prefer a domain-first name
such as `ApprovedExpenseAmountReader` over a generic `MonthlyApprovedAmountPort` when the former
describes the capability more accurately. For a CQRS command exposed to a primary adapter, use a
business-named `*UseCase` as the single-operation `@PrimaryPort` and let the corresponding command
handler implement it. Do not add a fixed `*CommandDispatcher` merely to delegate every known
command to its handler; reserve a dispatcher for genuine runtime routing through a shared command
pipeline.

An aggregate repository has an independent DDD role. In a Hexagonal project it may also be marked
`@SecondaryPort`, while remaining under the domain repository package. Do not duplicate it as an
application `port.out` interface. A `@SecondaryAdapter` may implement either a regular secondary
port or a DDD repository.

In Onion, express the same dependency inversion with rings: the aggregate repository contract is
inside the domain ring and its implementation is in the infrastructure ring. Do not mix Hexagonal
port/adapter annotations into the Onion analysis scope.

For Onion CQRS commands, name application handlers or services from the business action and let
web, messaging, or scheduler infrastructure call that application boundary directly. Do not copy
Hexagonal `port.in`, `@PrimaryPort`, or `*UseCase` requirements into Onion merely because commands
exist. A generic dispatcher still needs a real runtime-routing reason; fixed forwarding remains
unhelpful in either architecture.

## Hexagonal Adapter Package Direction

Select one package vocabulary for all Hexagonal adapters: `adapter.in` / `adapter.out`, or
`adapter.primary` / `adapter.secondary`. Both map to the same `@PrimaryAdapter` and
`@SecondaryAdapter` roles. The bundled template uses `adapter.in/out`; change both the package
tree and its selected ArchUnit rule together when choosing the primary/secondary alternative. Do
not mix these pairs in one project except during an explicitly bounded migration. This is a
JFoundry project convention, not an Onion concept or a Cockburn-mandated package tree.

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

- `PROJECT-web` or `PROJECT-interface`: physical module for primary adapters such as REST controllers, admin APIs, schedulers, CLI commands, or message listeners; package them below the selected `adapter.in` or `adapter.primary` root.
- `PROJECT-application`: primary ports under `port.in`, application services, and application-owned secondary ports under `port.out`.
- `PROJECT-domain`: aggregates, value objects, domain events, aggregate repository contracts, and optional narrow domain-facing secondary ports under `port.out`.
- `PROJECT-infrastructure`: physical module for secondary adapter implementations such as persistence, query adapters, remote SDK/HTTP clients, broker senders, Redis, and file storage; package them below the selected `adapter.out` or `adapter.secondary` root. Use `lookup.<feature>` for read-only facts needed to execute commands or make domain decisions, and `query.<feature>` only for caller-facing pages, lists, reports, exports, or other read use cases. Reserve `readmodel` for projects that explicitly use read-model terminology or CQRS-style read models. For an event- or state-change-driven read-model materialization flow, use the optional technical shape `projection.<feature>` rather than `query.<feature>`; it writes the read model and is not a query adapter. Group adapters by technical shape first and business feature or external system second, such as `persistence.<aggregate>`, `lookup.<feature>`, `query.<feature>`, `projection.<feature>` when applicable, `client.<external-system>`, `messaging.<topic>`, `file.<feature>`, and `cache.<feature>`. Keep global runtime framework configuration out of this module when a runtime assembly module exists.
- `PROJECT-boot`: runtime assembly, dependency wiring, runtime framework configuration, and selected runtime framework starters. The name `boot` is common for Spring Boot, but the role is runtime assembly, not a Spring-only rule.

Recommended package shape:

```text
PACKAGE_NAME
  domain
    port.out        # optional: narrow ports consumed by domain services or policies
  application
    <capability>
      command       # optional CQRS organization
        port.in
        service
      query         # optional CQRS organization
        port.in
        port.out    # workflow/read ports consumed by application services
        view        # neutral application-owned models shared by both directions
        service
  adapter
    in
      web           # or interface for non-HTTP-heavy inbound surfaces
        <feature>
          request   # HTTP/API DTOs, adapter-local
          response
      messaging
      scheduler
    out
      persistence
        <aggregate-or-feature>
      lookup
        <feature>
      query
        <feature>
      projection      # only for event/state-change-driven read-model updates
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

Keep `domain` and `application` free of MyBatis, Spring MVC, broker clients, and persistence models. Primary adapters in the selected inbound direction package call application services or primary ports. HTTP/API DTOs in primary adapters should use adapter-local names such as `*Request` and `*Response`; primary ports should use application-owned models such as `*Command` and `*Result` when that naming fits the project. Secondary adapters in the selected outbound direction package implement secondary ports from either the domain or application module. Put global runtime framework configuration, component scanning, and runtime bean wiring in the runtime assembly module/package, not in adapter packages.

When Primary and Secondary Ports in the same capability share a query or view model, keep that
model in a neutral application package such as `application.<capability>.query.view`. A Primary Port
must not import a model owned by `port.out`, and a Secondary Port must not import a model owned by
`port.in`. JFoundry's Hexagonal convention rules recognize `port.in` and `port.out` at any package
depth, so both global and capability-oriented layouts are valid. Capability-first organization is
the recommendation for non-trivial business applications, not a Cockburn-mandated package tree.

For Spring projects, runtime configuration includes Spring `@Configuration`, `@ConfigurationProperties`, component scanning, and auto-configuration customization. Read `references/spring-runtime.md` before adding those dependencies or configuration classes.

For Onion Simple package layouts, use the Onion template instead of the Hexagonal template. Its `infrastructure.web` package is expected because web delivery is an outer-ring concern. When a separate runtime assembly module/package exists, keep global runtime assembly and component scanning there; use infrastructure-local config only for adapter details that are not global runtime wiring.

For Onion CQRS flows, `infrastructure.query.<feature>` remains read-only. When an infrastructure
component consumes an event or state change to materialize or refresh a query-optimized read model,
`infrastructure.projection.<feature>` is an available technical shape. The inner-ring contract may
be named for its responsibility, but it is not a Hexagonal Port and the implementation is not a
Hexagonal Adapter. `projection` remains conditional vocabulary and does not require Event Sourcing.

Use `infrastructure.lookup.<feature>` for a read-only fact needed by command workflow or a domain
decision. That role differs from caller-facing `infrastructure.query.<feature>` even though both
may execute a database `SELECT`.

Within a ring, prefer business capability as the first organization axis. If the project uses CQRS,
place `command` and `query` below the capability they serve instead of creating peer top-level
`command`, `query`, `port`, and `service` taxonomies. Package annotations work well for homogeneous
role packages; use type annotations when a capability package intentionally contains mixed roles.
For Onion projects, shared query/view models remain owned by their application capability, but do
not add `port.in` / `port.out` packages to express that ownership: Onion has no directional Port
taxonomy.

## Exception Boundaries

Use jfoundry's compact domain/application exception model and do not create a generic `BusinessException` hierarchy. Domain code must not depend on application exceptions or HTTP concepts. Infrastructure implementations translate technical failures at the outbound contract boundary they own while preserving the cause; do not indiscriminately wrap domain failures.

The `using-jfoundry` skill routes exception selection, adapter translation, runtime mapping, and testing decisions to `references/exception-handling.md`. Read that reference instead of duplicating its decision table here.

## Annotation Placement

Prefer package-level annotations when an entire package has one role. A package-level role applies
to every type in that package, including records, DTOs, and nested types declared by a port. If a
port package also owns input/output models or other non-port types, use type-level annotations on
the port interfaces or move those models to a separate package. Use class-level annotations for
incremental migration or any other mixed package; do not weaken the architecture rule to accept
non-interface types as ports.

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
package PACKAGE_NAME.adapter.in;
```

```java
@org.jfoundry.architecture.hexagonal.SecondaryAdapter
package PACKAGE_NAME.adapter.out;
```

## Do Not

- Do not put controllers, schedulers, or message listeners in the application package.
- In Hexagonal projects, do not let controllers call repositories, mappers, secondary ports, or secondary adapters directly. In Onion projects, enforce an application-service entry boundary only when the project's DDD, CQRS, Layered, or local policy selects it; Onion's inward dependency rule alone does not require that call path.
- Do not put MyBatis `Mapper`, `Wrapper`, `Page`, `IPage`, Spring Data repositories, or JPA specifications in domain/application signatures.
- Do not create one interface for every class. Create ports for real boundaries and outbound needs.
- Do not enable CQRS only for symmetry. Use it when command and query models actually diverge.
- Do not create custom HTTP error response wrappers for new Spring MVC jfoundry projects unless the user explicitly rejects RFC 9457 `ProblemDetail`.
