---
name: using-jfoundry
description: Guide AI agents and developers when starting or modifying Java business projects that use the jfoundry framework. Use for Maven dependency selection, JFoundry architecture landing, aggregate persistence, reliable messaging, Spring runtime assembly, and ArchUnit verification. Do not use for maintaining jfoundry internals.
---

# Using JFoundry

Use this skill for downstream business projects, not for changing jfoundry itself. It turns an already selected architecture into jfoundry dependencies, package roles, templates, and verification.

## Decision Flow

1. Read `references/implementation-guidance-result.md` for setup, modification, or framework-landing work.
2. Preserve the project's selected jfoundry version, runtime, architecture style, and conventions. Ask only when an unknown choice changes a required dependency, package role, or test.
3. Read `references/dependencies.md`, then select the runtime before copying only its BOM and capability templates.
4. Read `references/architecture.md` for JFoundry's package and annotation landing after Hexagonal or Onion has already been selected.
5. Read a specialized reference only when its concern applies: persistence, repository/read contracts, Spring runtime, Quarkus runtime, reliable messaging, locks, exceptions, or tests.
6. Read `references/upstream-documentation.md` before using exact framework APIs, properties, auto-configuration behavior, or implementation-specific algorithms.
7. Run the narrowest relevant Maven verification and return the JFoundry Implementation Guidance Result.

## Non-Negotiable Boundaries

- Keep domain code free of runtime, ORM, mapper, broker, HTTP, and client-SDK types.
- Keep business orchestration in application code; primary adapters enter through an application boundary rather than calling persistence or clients directly.
- Keep technology-specific persistence and messaging in outer adapters or infrastructure.
- Do not select Hexagonal, Onion, CQRS, Spring Boot, Outbox, Inbox, locks, or a persistence provider merely because a template exists.
- Keep aggregate repositories for aggregate lifecycle. Add read contracts or ports only when a read responsibility needs one.
- Treat persistence base classes as optional support. A project-local adapter may implement its aggregate repository contract directly.

## Reference Routing

| Need | Read |
|---|---|
| Result format, assumptions, and unresolved choices | `references/implementation-guidance-result.md` |
| Version, BOM, starters, and Maven templates | `references/version-selection.md`, `references/dependencies.md` |
| Architecture landing and package roles | `references/architecture.md` |
| Aggregate repositories and read-side contracts | `references/repository-and-read-contracts.md` |
| JPA, MyBatis-Plus, mappers, and optimistic locking | `references/persistence-data-mappers.md` |
| Spring Framework or Spring Boot | `references/spring-runtime.md` |
| Quarkus CDI, JTA, REST, JPA, Outbox, Inbox, or broker assembly | `references/quarkus-runtime.md` |
| Outbox, Inbox, broker selection, and JPA Inbox database support | `references/outbox-inbox.md` |
| Cross-instance locking | `references/distributed-locks.md` |
| Exception ownership and boundary translation | `references/exception-handling.md` |
| Architecture tests and Maven verification | `references/testing.md` |
| Exact framework behavior for the selected version | `references/upstream-documentation.md` |

Copy templates from `assets/templates/` instead of recreating Maven dependencies, package trees, or ArchUnit skeletons from memory. Optional snippets remain optional.
