# First Use Guide

## Minimal Prompt

Use this prompt when starting a business project from scratch:

```text
Use $using-jfoundry to create the initial architecture for a new Java business project.
Base package: PACKAGE_NAME
Project shape: single app, Hexagonal domain/application/adapter/boot, a justified Hexagonal adapter split, or Onion domain/application/infrastructure/boot
Runtime: none, Spring Boot, Spring Framework, Quarkus, Micronaut, Helidon, CLI/custom, or undecided
Persistence: none, MyBatis-Plus, JPA, or undecided
Messaging: none, Kafka, RabbitMQ, RocketMQ, or undecided
Architecture: confirmed result, existing project style, explicit choice, or undecided
```

## Agent Sequence

The agent should:

1. Confirm or infer the base package and whether physical modules are justified. After resolving the architecture, for a new non-trivial Hexagonal project evaluate `domain` / `application` / `adapter` / an optional composition root such as `boot`, where one `adapter` module contains `adapter.in` and `adapter.out`; split it into `interface` for all inbound transports and `infrastructure` for `adapter.out` only when a real build, ownership, deployment, or dependency-isolation boundary justifies doing so. For Onion, evaluate `domain` / `application` / `infrastructure` / an optional composition root and retain the rings' inward dependency direction. `boot` is a template convention, not a mandatory package; follow the confirmed Application Runtime Integration Policy.
2. Resolve the architecture from a confirmed `Architecture Guidance Result`, project evidence, established conventions sufficient for the requested change, or an explicit user choice. Preserve an existing style. For simple CRUD, continue with established or explicitly simple conventions when no new architecture decision is required. Return `needs-input` only when a missing choice blocks responsible landing.
3. Read `version-selection.md`, then resolve the selected release's dependency-management POM and runtime guide before adding dependencies.
4. Add only the selected release's documented runtime and capability artifacts; never put runtime framework starters in domain or application modules.
5. Copy a package structure from `assets/templates/structure/` only when it matches the resolved architecture; otherwise preserve the established project structure.
6. Resolve the selected release's architecture-test rule entrypoints only when the corresponding architecture is selected.
7. Pin only the selected exact version, then replace the other placeholders.
8. Create package-level architecture annotations where package roles are stable.
9. Add only required optional capabilities.
10. Run Maven verification.

## Architecture-Neutral Defaults

Use these defaults when the user asks for scaffolding and the choice is independent of architecture:

- a Java version compatible with the selected jfoundry release and runtime
- Maven
- no runtime framework binding yet
- the selected release's framework-neutral dependency management
- no Outbox
- no Inbox
- no broker starter
- no MyBatis-Plus unless persistence is explicitly requested

If the user selects Spring Framework or Spring Boot, read `references/spring-runtime.md`. If the user selects Quarkus, read `references/quarkus-runtime.md`. For other runtimes, use the selected release's framework-neutral dependency management unless that release documents an explicit runtime adapter.

Select project modules, dependency capabilities, package sketches, annotations, and architecture tests only after the architecture and project roles are clear. A simple CRUD change may preserve established conventions without richer modeling or a new architecture decision. A new domain-heavy project should obtain Domain Modeling and Architecture Guidance before dependency selection.

## When To Ask Before Proceeding

Ask before continuing when:

- The base package is unknown.
- The project shape affects file creation substantially.
- The persistence choice is unknown and code generation depends on it.
- The user asks for external messaging but does not identify a broker.
- An explicit architecture choice conflicts with the existing project evidence.
- An unresolved architecture choice changes dependency direction, package or module placement, architecture sketches, or architecture tests.
