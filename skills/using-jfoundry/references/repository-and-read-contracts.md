# Repository And Read-side Contract Guidance

This guide distinguishes aggregate repositories from non-aggregate read and maintenance contracts.
The responsibility categories are architecture-neutral; the `*Port` suffix is not. In Hexagonal
projects a read contract may be a secondary port, while Onion projects should normally name the same
inner-ring contract from domain language and its actual responsibility, such as `Finder`, `Reader`,
`Gateway`, `Resolver`, `Store`, or `Scanner`. These suffixes are project vocabulary, not mandatory
DDD, Onion, Hexagonal, or CQRS terms.

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

## Lookup Contracts

Consider a lookup-style read contract when core code needs a fact for command workflow context or a
domain decision but will not modify the loaded object. This is command-side supporting input, not a
CQRS query-side view merely because it is read-only:

- Tenant, environment, account, or application key lookup.
- Existence checks for related objects.
- Lightweight context for permission checks, command enrichment, or external SDK calls.

Return lightweight records or DTOs, not MyBatis/JPA data objects.

Place the port near the consumer:

- Application workflow/read context belongs in the application module.
- Domain-decision context may belong in the domain module, but keep it narrow and domain-named.
- Avoid placing broad list, page, reporting, or UI-shaped lookup ports in the domain module.
- Prefer names that describe the external fact or policy input over generic `*LookupPort` names, especially in the domain module and in Onion projects.

## Query And Read-Model Contracts

Consider an application-owned query contract for query use cases, page views, dashboards, reports,
list screens, and read shapes that differ from write aggregates. Name it by the business view and
responsibility, for example `ExpenseClaimViewReader`. Query contracts and their adapters are
read-only; do not put a read-model writer behind a query contract or in a `query` package.

CQRS is useful when commands and reads have different models, performance needs, or consistency expectations. Do not introduce CQRS just because a method is read-only.

These are application-owned read-side contracts by default. Do not place page, report, dashboard, or UI-shaped read contracts in the domain module.

## Projection Updates

Use `projection` only when a component consumes an event or another state change to materialize or
refresh a query-optimized read model. A projection update can write that read model, but it does not
execute a business command or modify the write aggregate. For example, a payment-result consumer
may update payment-status data that a separate `ExpenseClaimViewReader` later reads.

Keep the projection writer behind a separate application-owned contract when a boundary is useful;
name it for its responsibility instead of requiring a `*Projection` suffix. In Hexagonal it may be
a secondary port with its implementation under `adapter.out.projection.<feature>` (or the selected
secondary-direction equivalent). In Onion it is an inner-ring dependency contract implemented in
`infrastructure.projection.<feature>`; do not import Hexagonal Port or Adapter roles into Onion.

`projection.<feature>` is an available technical package shape for these flows, not a universal
CQRS package. It does not imply Event Sourcing: an ordinary event or state-change notification may
drive the update.

When technical grouping improves navigation, use `persistence.<aggregate>` for aggregate lifecycle
storage, `lookup.<feature>` for command-side fact reads, `query.<feature>` for caller-facing
read-only views, and `projection.<feature>` for event/state-change materialization. These are
optional project conventions, not architecture rules: do not create a category merely for symmetry.

## Maintenance Contracts

Consider a maintenance-style contract for technical scanning and background maintenance. Prefer a responsibility such as `Scanner`, `CleanupCandidates`, or `RetryCandidates` over a generic `MaintenancePort` suffix:

- Find timed-out processing records.
- Find expired IDs to clean up.
- Select retry, repair, or cleanup candidates.

If business invariants are involved, return IDs and let the application service load aggregates and invoke domain behavior.

## Migration Order

When replacing Active Record, MyBatis-Plus `IService`, generic `Wrapper`, or specification-style queries:

1. Ask whether the query exists to modify an aggregate.
2. If yes, load by aggregate ID or stable business identity when possible.
3. If the result prepares workflow context, prefer an application-owned lookup contract named for the fact it supplies.
4. If the result serves UI, reporting, list, or page reads, prefer a read-only query/read-side contract named for the business view.
5. If an event or state change materializes or refreshes a separate query-optimized read model, use a separate projection writer rather than a query contract.
6. If the result serves background scan, cleanup, or repair, prefer a responsibility such as `Scanner` or `CleanupCandidates`.
7. If a domain rule needs an external fact, create a narrow domain-facing contract instead of reusing a broad application query contract. In Hexagonal Architecture this contract is a secondary port; in Onion it is an inner-ring dependency contract.
8. If one old method serves commands and queries, split it.

## Gradual Adoption

Small projects may start with one read-side contract, such as `OrderViews` or `OrderViewReader`, alongside aggregate repositories. Split lookup, query/read-model, and maintenance responsibilities later when query purposes, result shapes, or change reasons diverge.

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
