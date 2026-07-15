# Hexagonal Architecture / Ports And Adapters

Use this when the selected architecture is Hexagonal / Ports and Adapters, or when reviewing whether ports, adapters, application services, and dependency direction are modeled correctly.

## Core Flow

```text
Primary Adapter -> Primary Port / Application Service -> Domain
Application or Domain Core -> Secondary Port -> Secondary Adapter
```

Inside/core code should not depend on adapter implementations. Adapters depend on ports or application services, not the other way around.

Hexagonal Architecture defines port direction and inside/outside boundaries; it does not prescribe
one package tree. For non-trivial business applications, prefer business capability as the first
organization axis and place inbound/outbound role packages inside that capability. A small project
may keep global `application.port.in` and `application.port.out` packages when they remain easy to
navigate.

Example capability-oriented shape:

```text
application.claim.command.port.in
application.claim.query.port.in
application.claim.query.port.out
application.claim.query.view
```

The `port.in` / `port.out` package names are a project or framework convention for making direction
locatable, not a structure mandated by Cockburn.

## Roles

- Primary Adapter: inbound driver such as REST controller, RPC endpoint, CLI command, scheduler, or message listener.
- Primary Port: inbound contract exposed by the application core to primary adapters.
- Application Service: use-case implementation and orchestration boundary; loads aggregates, invokes domain behavior, coordinates transactions, and calls secondary ports.
- Secondary Port: outbound need expressed by application or domain core.
- Secondary Adapter: implementation of a secondary port, such as persistence, external API client, broker sender, cache, file storage, or SDK adapter.

## Adapter Package Direction

Adapter direction should be locatable in a Hexagonal project. Two equivalent project conventions are valid: `adapter.in` / `adapter.out`, or `adapter.primary` / `adapter.secondary`. The first names direction from the application's perspective; the second uses Cockburn's role terms. They carry the same Primary/Secondary Adapter semantics and must not be mixed within one project, except during an explicitly bounded migration.

Select the convention in the architecture decision and make the architecture test enforce it. This is a project or framework convention, not a package tree required by Hexagonal Architecture. Onion projects use ring terminology instead and do not adopt either direction pair.

## Adapter Internal Organization

Hexagonal Architecture defines adapter direction and dependency rules, not a single package taxonomy inside the adapter area. Organize adapter internals by the dominant technical reason to change:

- Primary adapters may group HTTP/API transport DTOs separately from controllers, for example `adapter.in.web.order.request` and `adapter.in.web.order.response`.
- Primary adapter request/response DTOs should stay adapter-local. They model transport shape, validation annotations, serialization names, and API compatibility.
- Primary ports should expose use-case contracts with application-owned input/output models, often named `*Command` / `*Result` or equivalent project terms.
- Secondary adapters may group by technical shape first, then business feature or external system: `persistence.<aggregate-or-feature>`, `lookup.<feature>`, `query.<feature>`, `projection.<feature>` when applicable, `client.<external-system>`, `messaging.<topic-or-system>`, `file.<feature>`, `cache.<feature>`.
- `lookup.<feature>` contains read-only facts needed by a command workflow or domain decision. It is not a page, report, or query-use-case adapter merely because it executes a database read.
- `query.<feature>` contains read-only query adapters. When an adapter consumes an event or state change to materialize or refresh a query-optimized read model, use an optional `projection.<feature>` category instead. A projection writer may update a read model, but it is not a query adapter or a business-command handler.
- `projection` is conditional CQRS vocabulary, not a universal package or suffix. It does not require Event Sourcing; a normal state-change notification can drive the update.
- Remote SDK/HTTP integrations should usually sit under a technical category such as `client.<external-system>` when several external systems share the same adapter style.

Example:

```text
adapter.in
  web
    order
      OrderController
      request
      response
application
  port.in
    SubmitOrderUseCase
    SubmitOrderCommand
    SubmitOrderResult
adapter.out
  persistence
    order
  lookup
    credit
  query
    order
  projection          # only for event/state-change-driven read-model updates
    payment
  client
    payment
    shipping
```

Avoid treating HTTP `Request`/`Response` classes as domain entities or as primary-port models. Avoid flattening every external system directly under `adapter.out` when a `client.<system>` grouping would make the adapter type clearer.

## Command Use Cases And Dispatchers

When CQRS commands are exposed through a Hexagonal primary boundary, use a business-named
`*UseCase` primary port for each application operation and let its command handler implement that
port. The command is the input model; the use case is the inbound contract; the handler is the
application implementation. A controller, message listener, CLI, or scheduler calls the relevant
use case directly.

```java
@PrimaryPort
public interface SubmitOrderUseCase {
    void submit(SubmitOrderCommand command);
}

@Application
@CommandHandler
final class SubmitOrderCommandHandler implements SubmitOrderUseCase {
    @Override
    public void submit(SubmitOrderCommand command) {
        // Orchestrate the command and invoke domain behavior.
    }
}
```

Do not add a fixed `OrderCommandDispatcher` whose overloaded `dispatch(...)` methods only delegate
to statically known handlers. That layer has neither generic routing nor business orchestration. A
real `CommandDispatcher` or command bus is appropriate only when commands must be routed at runtime
through a shared pipeline, for example from multiple generic message transports, through pluggable
handler registration, or through a common dispatch concern.

## Primary Port And Application Service Naming

Primary ports are contracts. Application services usually implement those contracts and contain the use-case orchestration.

Java/Kotlin examples may use `*UseCase` for the primary-port contract and `*Service` for the application-service implementation:

```java
@PrimaryPort
public interface SubmitOrderUseCase {
    void submit(SubmitOrder command);
}

@Application
final class SubmitOrderService implements SubmitOrderUseCase {
    // Load aggregates, invoke domain behavior, call secondary ports, save results.
}
```

The primary adapter depends on the primary port, not on the concrete service:

```text
OrderController -> SubmitOrderUseCase -> SubmitOrderService
```

This differs from secondary ports:

```text
SubmitOrderService -> PaymentGateway -> PaymentGatewayAdapter
```

Do not treat every type named `UseCase` as the implementation. In this command-oriented convention,
`*UseCase` is the inbound contract and the `*CommandHandler` is its implementation. Other projects
may use `*Service` implementations, but must document the meaning consistently.

## Secondary Port Ownership

Place a secondary port by consumer ownership:

- If an application service needs workflow context, command enrichment, a read model, or a query shape, put the port near the application service.
- If a domain service or policy needs an external fact to make a domain decision, put a narrow domain-facing port near that domain service or policy.
- Keep domain-facing ports small and named by domain meaning, such as `CreditLimitPolicy`, `CustomerCreditLimit`, or `EnvironmentAvailability`.
- Do not move broad page, reporting, or integration ports into the domain just because one domain service needs one method. Split a narrow domain-facing port instead.

Aggregate repositories are DDD repository contracts for aggregate lifecycle and command-side loading. In Hexagonal implementations they are outbound contracts implemented by infrastructure, but do not duplicate them as generic application `port.out` interfaces.

## Port Model Ownership

A Primary Port should not import models owned by a Secondary Port package, and a Secondary Port
should not import models owned by a Primary Port package. That creates a hidden dependency between
two independently directed boundaries and makes whichever port owns the model accidentally more
fundamental than the other.

If both contracts share a query or view model, place it in a neutral application capability package,
such as `application.claim.query.view`. Keep transport request/response DTOs in the primary adapter,
and keep persistence/remote-system data models in the secondary adapter. Do not move application
views into the domain merely to make them reusable.

## Boundary Checks

- Primary adapters should not call secondary adapters or repositories directly.
- Application services should not depend on concrete persistence, HTTP, broker, cache, SDK, or framework adapter types.
- Secondary adapters should translate external DTOs, status codes, and persistence models before returning to core code.
- Ports should represent meaningful variability, external dependency, test boundary, or model protection. Avoid one-interface-per-class abstractions.
- Primary and Secondary Ports should depend on application- or domain-owned models, not on models owned by the opposite port direction.
