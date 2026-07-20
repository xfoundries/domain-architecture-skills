# JFoundry Architecture Landing

Use this reference only after the project has selected an architecture style through existing evidence, an explicit decision, or `domain-architecture-guidance`. JFoundry templates express that decision; they do not make it.

## Choose The Matching Shape

| Selected style | Template and project rule |
|---|---|
| Hexagonal | Copy `assets/templates/structure/hexagonal-package-structure.txt`. Use one selected adapter vocabulary: `adapter.in/out` or `adapter.primary/secondary`. |
| Onion | Copy `assets/templates/structure/onion-simple-package-structure.txt`. Use inward rings and `infrastructure`; do not introduce Hexagonal direction packages. |
| Existing or simple CRUD | Preserve established boundaries. Do not add Hexagonal or Onion packages only to use jfoundry annotations. |

Package roles are not Maven-module requirements. A balanced project may use `domain`, `application`, one outer `adapter` or `infrastructure` module, and a runtime assembly module. Split an adapter module only for a real build, ownership, deployment, or dependency-isolation boundary.

## Hexagonal Landing

Use the following annotations only in a selected Hexagonal analysis scope:

| Role | JFoundry annotation | Placement |
|---|---|---|
| Application core | `@Application` | application services and use cases |
| Inbound contract | `@PrimaryPort` | application `port.in` or business-named use-case package |
| Inbound driver | `@PrimaryAdapter` | web, messaging, CLI, scheduler, or batch adapter |
| Outbound contract | `@SecondaryPort` | application or narrow domain-owned contract |
| Outbound implementation | `@SecondaryAdapter` | persistence, client, broker, cache, or file adapter |

Use package annotations only for homogeneous role packages; otherwise annotate the relevant type. Aggregate repositories remain domain contracts and may also be secondary ports. Do not duplicate an aggregate repository merely to create an application `port.out` interface.

## Project Constraints

- Primary adapters call a primary port or application service, never a repository, mapper, or secondary adapter directly.
- Keep HTTP request/response DTOs in primary adapters and application command/result models at the application boundary.
- Keep lookup facts for commands separate from caller-facing queries when their responsibility or result shape differs. Add projections only for event- or state-driven read-model materialization.
- Keep global runtime configuration and selected starters in the runtime assembly module/package.
- Do not mix Hexagonal and Onion annotations in one ArchUnit analysis scope.
- Do not introduce CQRS, ports, or separate Maven modules for symmetry.

## Helper And Support Code Placement

Do not add a global `utils` package or Maven module. Classify Java helpers by responsibility and
dependency direction, then place them in the owning business capability where practical.

| Responsibility | Hexagonal placement | Onion placement | Prefer names such as |
|---|---|---|---|
| Domain concept, invariant, or calculation | `domain` | `domain` | `Money`, `SettlementPeriod`, `PricingPolicy` |
| Repeated use-case orchestration | `application.<capability>.support` | `application.<capability>.support` | `OrderCommandOperations`, `ExpenseClaimCommandSupport` |
| HTTP or message input mapping | matching primary adapter | `infrastructure.web` or `infrastructure.messaging` | `CreateOrderRequestMapper`, `MessagePayloadParser` |
| Persistence mapping or external client protocol | matching secondary adapter | `infrastructure.persistence` or `infrastructure.client` | `OrderDataConverter`, `PaymentGatewaySigner` |
| Spring, ORM, or runtime assembly support | runtime assembly or owning outer adapter | runtime assembly or owning `infrastructure` concern | `JacksonSupport`, `JpaTransactionSupport` |
| Framework-neutral, business-free support | narrowly named shared package only when justified | narrowly named shared package only when justified | `Utf8`, `Checks` |

Keep the optional shared package independent of Spring, ORM, HTTP, broker, and SDK types. It is not
an escape hatch for code with unclear ownership. In particular, do not move a domain rule into it
because multiple aggregates use it; model that rule as a domain concept or domain service instead.

Read `references/upstream-documentation.md` for exact annotation semantics and architecture-rule entrypoints. Read `references/testing.md` after selecting the shape.
