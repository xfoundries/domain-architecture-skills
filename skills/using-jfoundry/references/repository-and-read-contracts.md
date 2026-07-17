# Repository And Read Contract Decisions

Use aggregate repositories for aggregate lifecycle and command-side loading. Do not turn every query, lookup, or persistence utility into a repository.

## Select The Contract

| Need | Owner and shape |
|---|---|
| Load or persist an aggregate for business behavior | Domain aggregate repository contract |
| Read facts needed by a command or domain decision | Narrow application or domain-owned lookup contract when direct aggregate loading is not appropriate |
| Page, report, export, dashboard, or UI-shaped read | Application-owned query contract and view model |
| Event/state-driven read-model materialization | Application contract plus an outer projection adapter when justified |
| Administrative write outside aggregate lifecycle | A dedicated application contract, not a generic repository method |

In Hexagonal projects, use primary/secondary port terminology where it clarifies direction. In Onion, use responsibility-named inner-ring contracts; do not create `port.in` and `port.out` merely to mirror Hexagonal naming.

## Keep Ownership Clear

- Repository interfaces stay near their aggregate and do not expose ORM, mapper, page, wrapper, or transport types.
- Query contracts and views belong to the application capability, not the domain or infrastructure implementation.
- Split lookup, query, and maintenance contracts only when their consumers, result shapes, consistency expectations, or change reasons differ.
- Primary adapters invoke an application boundary. They do not call aggregate repositories or query implementations directly.
- Do not enable CQRS only because a method reads data; use it when command and read models genuinely diverge.

Read `references/persistence-data-mappers.md` when implementing the outer persistence adapter and `references/upstream-documentation.md` for exact repository base contracts.
