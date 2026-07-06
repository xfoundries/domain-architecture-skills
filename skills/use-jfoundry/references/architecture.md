# Architecture Guidance

## Style Selection

For straightforward scaffolding, prefer Hexagonal Architecture for new business projects because it gives AI agents clear package and dependency boundaries:

```text
primary adapter -> primary port / application service -> domain
inside core consumer -> secondary port
secondary adapter -> secondary port
```

When the task is architecture analysis, ADR writing, domain modeling, or style selection, do not treat Hexagonal as preselected. Compare Layered, Onion, Hexagonal, and CQRS applicability from the domain model, integration boundaries, dependency constraints, and read/write needs, then select the matching jfoundry template.

Use Onion Simple when the team explicitly wants ring terminology:

```text
infrastructure -> application -> domain
```

Do not mix Hexagonal and Onion annotations in the same ArchUnit analysis scope. If a project already chose a style, preserve it.

## Hexagonal Roles

For general Hexagonal semantics, primary/secondary port direction, and application-service naming, read `domain-architecture-guidance/references/hexagonal-architecture.md`. This section only maps those roles to jfoundry annotations and package locations.

- `@Application`: marks application core code. It can include use cases, application services, and domain-facing orchestration. Do not confuse this with a Maven module named `application`; a multi-module project may split the Hexagonal inside/core across domain and application modules. Do not mark an entire domain root package as `@Application`.
- `@PrimaryPort`: marks inbound interfaces exposed to driving adapters. Put them under `port.in`, `ports.in`, `port.inbound`, `ports.inbound`, `usecase`, or `usecases`. Java examples in this skill family often name these `*UseCase`.
- `@PrimaryAdapter`: marks inbound drivers such as REST controllers, message listeners, CLI commands, schedulers, or batch triggers.
- `@SecondaryPort`: marks outbound interfaces owned by the consumer. Put application-owned ports under application `port.out`; put only narrow domain-policy ports under domain `port.out`.
- `@SecondaryAdapter`: marks implementations of secondary ports, such as MyBatis/JPA persistence, Redis, HTTP clients, broker senders, file storage, or external SDK adapters.

Keep aggregate repository contracts under the domain repository package instead of duplicating them as application `port.out` interfaces.

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
  PROJECT-boot
```

Map Hexagonal roles inside those modules:

- `PROJECT-web` or `PROJECT-interface`: primary adapters such as REST controllers, admin APIs, schedulers, CLI commands, or message listeners.
- `PROJECT-application`: primary ports under `port.in`, application services, and application-owned secondary ports under `port.out`.
- `PROJECT-domain`: aggregates, value objects, domain events, aggregate repository contracts, and optional narrow domain-facing secondary ports under `port.out`.
- `PROJECT-infrastructure`: secondary adapter implementations such as persistence, external SDK clients, HTTP clients, broker senders, Redis, and file storage. Keep runtime framework configuration out of this module when a boot module exists.
- `PROJECT-boot`: runtime assembly, dependency wiring, runtime framework configuration, and selected runtime framework starters.

Recommended package shape:

```text
PACKAGE_NAME
  domain
    port.out        # optional: narrow ports consumed by domain services or policies
  application
    port.in
    port.out        # workflow/read ports consumed by application services
    service
  web             # or interface for non-HTTP-heavy inbound surfaces
  infrastructure  # secondary adapter implementations: persistence, clients, messaging, cache, files
  boot
    config        # runtime framework configuration and bean wiring
```

Keep `domain` and `application` free of MyBatis, Spring MVC, broker clients, and persistence models. Primary adapters in `web` or `interface` call application services or primary ports. Secondary adapters in `infrastructure` implement secondary ports from either the domain or application module. Put Spring `@Configuration`, `@ConfigurationProperties`, component scanning, and runtime bean wiring in `boot`, not `infrastructure`.

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
