# First Use Guide

## Minimal Prompt

Use this prompt when starting a business project from scratch:

```text
Use $using-jfoundry to create the initial architecture for a new Java business project.
Base package: PACKAGE_NAME
Project shape: multi-module Maven preferred, or single app for small projects
Runtime: none, Spring Boot, Spring Framework, Quarkus, Micronaut, Helidon, CLI/custom, or undecided
Persistence: none, MyBatis-Plus, JPA, or undecided
Messaging: none, Kafka, RabbitMQ, RocketMQ, or undecided
Architecture: default
```

## Agent Sequence

The agent should:

1. Confirm or infer the base package and project shape; prefer multi-module Maven for normal DDD projects.
2. For direct scaffolding, default architecture to Hexagonal unless the user requests Onion. For architecture analysis, ADR, domain modeling, or style-selection work, evaluate candidate architecture styles before selecting templates.
3. Read `version-selection.md` and select the exact jfoundry version before choosing dependency templates.
4. Copy Maven snippets by module or layer from `assets/templates/maven/`; never put runtime framework starters in domain or application modules.
5. Copy package structure from `assets/templates/structure/`.
6. Copy `HexagonalArchitectureTest.java` or `OnionSimpleArchitectureTest.java`.
7. Replace `JFOUNDRY_VERSION` only with the selected exact version, then replace the other placeholders.
8. Create package-level architecture annotations where package roles are stable.
9. Add only required optional starters.
10. Run Maven verification.

## Recommended Defaults

Use these defaults when the user asks for straightforward scaffolding and has no preference:

- a Java version compatible with the selected jfoundry release and runtime
- multi-module Maven for normal DDD projects
- Hexagonal Architecture
- no runtime framework binding yet
- `jfoundry-dependencies`
- `jfoundry-domain-starter` in the domain module
- `jfoundry-application-starter` in the application module
- no Outbox
- no Inbox
- no broker starter
- no MyBatis-Plus unless persistence is explicitly requested

If the user selects Spring Framework or Spring Boot, read `references/spring-runtime.md`. For other runtimes, use framework-neutral jfoundry dependencies unless an explicit runtime adapter exists.

These are scaffolding defaults, not architecture analysis conclusions. When the user asks to decide the architecture, first compare the relevant styles from the domain model and integration constraints.

## When To Ask Before Proceeding

Ask before continuing when:

- The base package is unknown.
- The project shape affects file creation substantially.
- The persistence choice is unknown and code generation depends on it.
- The user asks for external messaging but does not identify a broker.
- The project already has an architecture style and it conflicts with the defaults.
