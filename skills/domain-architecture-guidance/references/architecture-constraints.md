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

## Application-Owned Shared Models

When multiple application contracts in one business capability use the same command, result, query,
or view model, place that model in a neutral application package owned by the capability. For
example, `application.claim.query.view` may serve both a query entry contract and a persistence
query contract.

Do not move an application query/view model into the domain merely to share it. Do not let
infrastructure define a model that application contracts must import. Package ownership should
follow the model's meaning and dependency direction, not whichever interface happened to need it
first.

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

## Runtime Integration By Layer

Keep the domain model strictly free of runtime frameworks and technical APIs such as Spring,
ORMs, HTTP, broker clients, cache clients, and serialization libraries. Application code has a
different trade-off: it should usually prefer framework-neutral contracts, but may deliberately use
runtime support for application-owned orchestration concerns such as transaction boundaries,
idempotency, distributed locks, or scheduling when an extra abstraction would add no real value.

That allowance does not make delivery or persistence technology an application concern. Keep
`HttpServletRequest`, HTTP responses, ORM mappers/entities, database sessions, broker records, and
client SDK types in adapters or infrastructure. Treat a direct runtime dependency in application as
a project/runtime decision and keep it out of the domain model.

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
- Within each ring, organize non-trivial code by business capability before technical role when that improves cohesion. Shared application models remain owned by the application capability.

Onion Architecture does not define Primary/Secondary Port or Adapter roles, and it does not prescribe
`*Port`, `*Adapter`, or `*UseCase` class-name suffixes. Inner rings may define interfaces that outer
rings implement, but name those interfaces from domain language and their actual responsibility.
Calling such an interface a port is a Hexagonal or project-local interpretation, not an Onion rule.
Do not introduce `port.in` / `port.out` ownership rules merely to organize Onion application
contracts; use responsibility names and the ring dependency direction instead.

The foundational Onion dependency rule also allows an outer ring to call any inner ring. Requiring
controllers or message consumers to enter only through application services can be a useful DDD,
CQRS, Layered, or project-local policy, but do not attribute that stronger call-path rule to Onion
Architecture itself.

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
- In non-trivial applications, organizing by business capability before port direction usually keeps use cases and their models cohesive. This is an implementation recommendation, not a package structure mandated by Hexagonal Architecture.
- When direction packages are used, a primary port should not depend on models owned by `port.out`, and a secondary port should not depend on models owned by `port.in`. Put shared application models in a neutral capability package.
- For a command-oriented use case, a business-named `*UseCase` primary port is usually the clearest inbound contract. Its command handler or application service implements that contract; a primary adapter calls the use case directly.

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

A `CommandDispatcher` or command bus is not the default CQRS entrypoint. Use it only when the
application genuinely needs generic runtime command routing, such as a shared message pipeline,
pluggable handler registration, or a common authorization/audit dispatch pipeline. Do not introduce
a dispatcher merely to hide several primary-adapter dependencies or to forward each statically known
command to one handler. In Hexagonal Architecture, let the command handler implement the
business-named `*UseCase` primary port. In Onion Architecture, use a business-named application
handler or service directly; Onion does not gain `port.in` or `@PrimaryPort` semantics from CQRS.

Not every read is a query-side read model. A lookup obtains a fact needed to execute a command or
make a domain decision; it remains read-only but belongs to command-side workflow context. A query
serves a page, list, report, export, or other caller-facing read use case. Keep these responsibilities
separate when their models or reasons to change differ.

When a component consumes an event or another state change to materialize or refresh a
query-optimized read model, `projection` is useful conditional CQRS vocabulary. Such a component
may write the read model, but it is not a query and does not execute a business command or modify
the write aggregate. Keep query contracts and query adapters read-only; do not place projection
writers in a `query` package. A technical package such as `projection.<feature>` is available only
for these flows, not as a universal package or suffix. The source of a projection can be an event
or a state-change notification; it need not be an event-sourced stream.

When technical grouping is useful, use `persistence.<aggregate>` for aggregate lifecycle storage,
`lookup.<feature>` for command-side fact reads, `query.<feature>` for caller-facing read models, and
`projection.<feature>` for event/state-change-driven read-model materialization. These are
responsibility categories, not mandatory package roots.

Do not introduce CQRS for symmetry alone. If the project has chosen CQRS, keep command and query responsibilities separated.

## Application Support Naming

An application-layer helper shared by several command handlers may centralize repeated aggregate
loading, clock access, application-level authorization checks, or persistence calls without taking
over domain behavior. Name it for that actual support responsibility, for example
`ExpenseClaimCommandSupport` or `OrderCommandOperations`.

Do not call such a helper `*Context` merely because it carries dependencies. Reserve `Context` for
an object that represents an execution scope or contextual data, such as a request, tenant,
security, or transaction context. This is a naming heuristic, not a DDD, CQRS, Hexagonal, or Onion
architecture rule.

## Helper And Shared Code Placement

Do not create a cross-layer `utils`, `common`, or `shared` package by default. A helper's package
ownership follows its responsibility and allowed dependencies, not its static-method shape or the
number of callers.

- Model a business concept, invariant, calculation, or policy in the domain. Prefer a value object,
  domain service, or business-named policy over a `*Utils` type.
- Put repeated use-case orchestration in the owning application's capability package, usually a
  `support` package when that grouping improves clarity. It may load aggregates, coordinate clock
  access, or apply application-level authorization, but must not take over domain behavior.
- Keep HTTP, broker, persistence, serialization, remote-protocol, and client-SDK helpers in the
  adapter or infrastructure package that owns that technology boundary. Application and domain code
  must not depend on those helpers.
- A small framework-neutral, business-free shared package is justified only when it has stable
  ownership and cannot become a dependency sink. Keep it independent of application, adapter,
  runtime, ORM, HTTP, broker, and SDK types; prefer a responsibility-specific name over a generic
  `utils` package.

Apply the same responsibility rule in every architecture style. In Hexagonal Architecture, place
technology helpers in the matching primary or secondary adapter without inventing a port for each
helper. In Onion Architecture, place them in the appropriate `infrastructure` concern and preserve
inward dependencies; do not import Hexagonal `adapter.in/out` or port vocabulary merely to organize
helpers.

## Domain Events And Integration Contracts

Do not assume an internal domain event is automatically a suitable public integration contract.
The two types may share business meaning while serving different ownership and evolution needs:

- Direct externalization is reasonable when the event is deliberately designed, owned, and versioned as a stable public contract.
- Otherwise translate the domain event at the application or infrastructure boundary into a versioned integration event.
- Keep broker routing, serialization metadata, and consumer-specific payload concerns outside the domain model.
- Write the integration event to Outbox in the same transaction as the business state when reliable cross-process publication is required.
- Treat duplicate delivery as expected and use consumer idempotency when the handling effect must occur once per logical consumer.

This is an integration-boundary decision, not a requirement that every domain event be published. It also does not make CQRS or Event Sourcing mandatory.

## Review Language

Use precise wording:

- "This violates the chosen Hexagonal boundary because the controller calls a secondary adapter directly."
- "This is not a DDD requirement, but it is a Layered or Hexagonal application boundary in this project."
- "This direct repository call could be acceptable in simple CRUD, but it conflicts with the layered architecture documented here."
- "This abstraction is optional because no architecture in this project requires a port at this boundary."
- "This is a DDD modeling problem because the aggregate no longer protects the invariant it owns."
- "This is a project policy, not an industry-wide DDD rule; enforce it here but label it correctly."
