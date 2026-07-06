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

- `@Application`: application core role. It can include use cases, application services, and domain-facing orchestration. Do not confuse this with a Maven module named `application`; a multi-module project may split the Hexagonal inside/core across domain and application modules. Do not mark an entire domain root package as `@Application`.
- `@PrimaryPort`: inbound API contract exposed by the application/use-case layer to driving adapters. It should be an interface and live under `port.in`, `ports.in`, `port.inbound`, `ports.inbound`, `usecase`, or `usecases`. Domain modules do not normally expose primary ports.
- `@PrimaryAdapter`: inbound driver such as REST controller, message listener, CLI command, scheduler, or batch trigger.
- `@SecondaryPort`: outbound need expressed by an inside/core consumer. It should be an interface and live under `port.out`, `ports.out`, `port.outbound`, or `ports.outbound` near the code that owns the need.
- `@SecondaryAdapter`: implementation of a secondary port, such as MyBatis/JPA persistence, Redis, HTTP clients, broker senders, file storage, or external SDK adapters.

## Secondary Port Ownership

Place a secondary port by consumer ownership, not by the word `Application` in Hexagonal Architecture:

- If an application service needs workflow context, command enrichment, a read model, or a query shape, put the port in the application module.
- If a domain service or policy needs an external fact to make a domain decision, put a narrow domain-facing port in the domain module.
- Keep domain-facing ports small and named by domain meaning, such as `EmcAvailabilityPort`, `CreditLimitPolicyPort`, or `EnvAppDependencyPort`.
- Do not move broad read/query ports into the domain module just because one domain service needs one method. Split a narrow domain-facing port instead.

Aggregate repositories are DDD repository contracts for aggregate lifecycle and command-side loading. In Hexagonal implementations they are outbound contracts implemented by infrastructure, but keep them under the domain repository package instead of duplicating them as application `port.out` interfaces.

## Package Defaults

For new Hexagonal projects, copy `assets/templates/structure/hexagonal-package-structure.txt`.

Recommended package shape:

```text
PACKAGE_NAME
  domain
    port.out        # optional: narrow ports consumed by domain services or policies
  application
    port.in
    port.out        # workflow/read ports consumed by application services
    service
  adapter.in
  adapter.out
  infrastructure
  boot
```

Keep `domain` and `application` free of MyBatis, Spring MVC, broker clients, and persistence models. Primary adapters call application services or primary ports. Secondary adapters implement secondary ports from either the domain or application module.

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
package PACKAGE_NAME.adapter.in;
```

```java
@org.jfoundry.architecture.hexagonal.SecondaryAdapter
package PACKAGE_NAME.adapter.out;
```

## Do Not

- Do not put controllers, schedulers, or message listeners in the application package.
- Do not let controllers call repositories, mappers, secondary ports, or secondary adapters directly.
- Do not put MyBatis `Mapper`, `Wrapper`, `Page`, `IPage`, Spring Data repositories, or JPA specifications in domain/application signatures.
- Do not create one interface for every class. Create ports for real boundaries and outbound needs.
- Do not enable CQRS only for symmetry. Use it when command and query models actually diverge.
