# Architecture Constraints

Use this file when deciding whether a dependency, call path, package reference, or boundary crossing is acceptable.

The key rule: do not confuse "not a DDD rule" with "not an architecture rule." DDD supplies domain modeling concepts, ubiquitous language, boundaries, and invariant-focused tactical patterns. Layered, Onion, Hexagonal / Ports and Adapters, and CQRS supply many structural constraints.

Do not describe DDD as the "best practice" of those architectures. A more accurate framing is: DDD is often used with these architectures in domain-heavy systems, and these architectures can provide disciplined ways to implement or protect a DDD model. Each architecture still has its own rules and should be followed on its own terms.

Clean Architecture is intentionally not modeled here as a separate supported architecture because this skill is aligned with jMolecules concepts, which provide Layered, Onion, Hexagonal / Ports and Adapters, and CQRS modules. Treat Clean Architecture as a synthesis and terminology bridge discussed in `source-policy.md`, not as a separate first-class architecture for this skill.

## Rule Source Levels

Use strong language only after identifying the source of the rule:

- **DDD core discipline**: strong once the project chooses DDD. Keep ubiquitous language, bounded-context meaning, invariant-protecting aggregates, identity/value distinction, and domain behavior placement coherent.
- **Architecture constraints**: strong only inside the chosen architecture. Layered, Onion, Hexagonal / Ports and Adapters, and CQRS each add their own dependency and responsibility rules.
- **Framework conventions**: strong only for projects using that framework or rule set, such as jfoundry annotations, starter placement, or ArchUnit rule groups.
- **Heuristics**: default recommendations that need context, such as keeping aggregates small, preferring one aggregate per transaction, or splitting read ports by responsibility.
- **Project policy**: strong inside that codebase when documented, but do not present it as a universal DDD rule.

## Decision Order

1. Identify the architecture the project claims or already implements.
2. Identify whether the rule comes from DDD core discipline, the chosen architecture, framework convention, heuristic guidance, or project policy.
3. Check whether the project has documented a local exception.
4. Only then decide whether simplification is appropriate.

Do not relax an explicit architecture constraint just because a simpler CRUD style would also be valid in another project.

## Layered Architecture

Common strict flow:

```text
Interface/API -> Application -> Domain
Infrastructure -> Application/Domain contracts
```

Typical constraints:

- Controllers should call application services or use-case handlers, not repositories directly.
- Controllers should not contain business workflow logic.
- Domain code should not depend on interface/API code.
- Application code should not depend on concrete infrastructure adapters unless the project intentionally uses a relaxed layered style.
- Infrastructure implements persistence, messaging, and external integration details.

Controller-to-repository can be acceptable in a simple CRUD style, but in a project that explicitly enforces layered boundaries it is usually a violation.

## Onion Architecture

Core rule:

```text
Dependencies point inward toward the domain model.
```

Typical constraints:

- Domain model is the center and should not depend on infrastructure.
- Application services depend inward and coordinate use cases.
- Infrastructure depends on inner rings and implements outer concerns.
- Persistence, web, messaging, serialization, and dependency injection details stay outside the domain model unless the project intentionally trades purity for framework integration.

Do not call this "overengineering" when the project has intentionally chosen Onion Architecture for boundary protection.

## Hexagonal Architecture / Ports and Adapters

Core rule:

```text
Primary adapters -> primary ports/application
Application/domain core -> secondary ports
Secondary adapters -> secondary ports/application contracts
```

Typical constraints:

- Primary adapters such as controllers, CLI commands, or message consumers should drive the application through primary ports or application services.
- Primary adapters should not call secondary adapters or repositories directly when that bypasses application rules.
- Application or domain core code should express outbound needs through secondary ports owned near the consumer.
- Secondary adapters implement ports for databases, message brokers, external APIs, file systems, email, and other outside technologies.
- Inside/core code should not depend on adapter implementations.

Ports should represent meaningful boundaries. Avoid creating an interface for every class when no boundary value exists.

## CQRS

Core rule:

```text
Commands change state. Queries read state.
```

Typical constraints:

- Commands should express business intent and be handled by command handlers or application services.
- Queries should not mutate state.
- Read models may be optimized for query shape and performance.
- Write models should protect invariants.
- CQRS does not imply Event Sourcing.

Do not introduce CQRS for symmetry alone. If the project has chosen CQRS, keep command and query responsibilities separated.

## Review Language

Use precise wording:

- "This violates the chosen Hexagonal boundary because the controller calls a secondary adapter directly."
- "This is not a DDD requirement, but it is a Layered or Hexagonal application boundary in this project."
- "This direct repository call could be acceptable in simple CRUD, but it conflicts with the layered architecture documented here."
- "This abstraction is optional because no architecture in this project requires a port at this boundary."
- "This is a DDD modeling problem because the aggregate no longer protects the invariant it owns."
- "This is a project policy, not an industry-wide DDD rule; enforce it here but label it correctly."
