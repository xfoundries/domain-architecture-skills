# Repository And Port Guidance

This guide uses `LookupPort`, `QueryPort`, and `MaintenancePort` as neutral read-side categories and suffixes. They are not mandatory DDD, Hexagonal Architecture, or CQRS terms. `ReadModelPort` is reasonable when the project explicitly uses read-model terminology or CQRS-style read models, but it should not be the default name for ordinary query adapters. A business project may use established names such as `ReadPort`, `Finder`, `Gateway`, `Resolver`, or `Scanner` when the responsibility is clear.

## Aggregate Repository

Use an aggregate repository for aggregate lifecycle and command-side aggregate loading:

- Load by aggregate ID.
- Load by stable business identity.
- Save or remove aggregates.
- Load an aggregate because the current command will immediately invoke aggregate behavior.

Name methods by domain intent, not SQL shape. Prefer `findCurrentOperation(...)` over condition-list method names.

An aggregate repository is a DDD repository contract whose identity does not depend on an
architecture style. In a Hexagonal/JFoundry application it may simultaneously be a
`@SecondaryPort`, while remaining under `domain.repository`; its infrastructure implementation may
be a `@SecondaryAdapter`. In Onion it is an inner-ring contract implemented by the infrastructure
ring. Do not duplicate the repository as an application `port.out` interface merely to satisfy a
package convention.

## LookupPort

Consider a lookup-style read port when core code needs context for a workflow or domain decision but will not modify the loaded object. `LookupPort` is the recommended suffix when the project has no existing name:

- Tenant, environment, account, or application key lookup.
- Existence checks for related objects.
- Lightweight context for permission checks, command enrichment, or external SDK calls.

Return lightweight records or DTOs, not MyBatis/JPA data objects.

Place the port near the consumer:

- Application workflow/read context belongs in the application module.
- Domain-decision context may belong in the domain module, but keep it narrow and domain-named.
- Avoid placing broad list, page, reporting, or UI-shaped lookup ports in the domain module.
- In domain modules, prefer names that describe the external fact or policy input over generic `*LookupPort` names.

## QueryPort / ReadModelPort

Consider a query port for query use cases, page views, dashboards, reports, list screens, projections, and read shapes that differ from write aggregates. `QueryPort` is the neutral default suffix for ordinary business systems. `ReadModelPort` is appropriate when the project intentionally calls those outputs read models, especially when query shapes are explicitly separate from write aggregates.

CQRS is useful when commands and reads have different models, performance needs, or consistency expectations. Do not introduce CQRS just because a method is read-only.

`QueryPort` and `ReadModelPort` are application-owned read-side contracts by default. Do not place page, report, dashboard, or UI-shaped read ports in the domain module.

## MaintenancePort

Consider a maintenance-style port for technical scanning and background maintenance. `MaintenancePort` is a JFoundry recommendation, not a widely standardized suffix:

- Find timed-out processing records.
- Find expired IDs to clean up.
- Select retry, repair, or cleanup candidates.

If business invariants are involved, return IDs and let the application service load aggregates and invoke domain behavior.

## Migration Order

When replacing Active Record, MyBatis-Plus `IService`, generic `Wrapper`, or specification-style queries:

1. Ask whether the query exists to modify an aggregate.
2. If yes, load by aggregate ID or stable business identity when possible.
3. If the result prepares workflow context, prefer an application-owned lookup-style read port such as `LookupPort`.
4. If the result serves UI, reporting, list, or page reads, prefer a query/read-side port such as `QueryPort`; use `ReadModelPort` only when read-model terminology is intentional.
5. If the result serves background scan, cleanup, or repair, prefer a maintenance-style port such as `MaintenancePort` or `Scanner`.
6. If a domain rule needs an external fact, create a narrow domain-facing secondary port instead of reusing a broad application lookup port.
7. If one old method serves commands and queries, split it.

## Gradual Adoption

Small projects may start with one read-side port, such as `OrderQueryPort` or `OrderReadPort`, alongside aggregate repositories. Split lookup, query/read-model, and maintenance responsibilities later when query purposes, result shapes, or change reasons diverge.

Do not split ports for naming symmetry. Split when it clarifies use-case responsibility, protects aggregate repositories from generic queries, or isolates infrastructure-specific read capabilities.

## Forbidden Leaks

Aggregate repository interfaces should not expose:

- MyBatis-Plus `Wrapper`, `IPage`, `Page`, `BaseMapper`, or `IService`.
- Spring Data `Page`, `Pageable`, `Repository`, or JPA `Specification`.
- Persistence data objects or mapper types.
- Page DTOs or reporting projections that are not aggregates.

Add `JFoundryRules.aggregateRepositoryConventions()` when the project is ready to enforce these
repository leak conventions. It recognizes both jMolecules `Repository` and jfoundry
`AggregateRepository` interfaces. The rule group does not force `LookupPort`, `QueryPort`,
`ReadModelPort`, or `MaintenancePort` suffixes.
