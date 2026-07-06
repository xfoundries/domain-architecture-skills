# Domain Architecture Plugin

English ｜ [中文](README_ZH.md)

---

A plugin-first architecture guidance package for business-domain software systems. It helps AI coding agents move from business requirements to domain models, architecture decisions, and framework-specific implementation guidance without treating DDD, Hexagonal Architecture, Onion Architecture, CQRS, or any framework convention as one mandatory combined model.

The distribution unit is the `domain-architecture` plugin. The `skills/` directory contains plugin-internal capabilities that are exposed by Codex, Claude Code, and other compatible agents after the plugin is installed.

## Plugin Capabilities

| Skill | Purpose |
|---|---|
| `domain-architecture-workflow` | Entry-point workflow for end-to-end business-domain architecture work. Coordinates modeling, architecture decisions, and optional framework-specific landing. |
| `domain-modeling` | Framework-neutral DDD/domain modeling workflow for ubiquitous language, business commands, events, bounded contexts, aggregates, invariants, value objects, domain services, repositories, and read needs/read models. |
| `domain-architecture-guidance` | Source-aware architecture guidance for DDD, Layered, Onion, Hexagonal / Ports and Adapters, CQRS, jMolecules-style annotations, and architecture tests. |
| `use-jfoundry` | JFoundry-specific application guidance for Java business projects using [jfoundry](https://github.com/xfoundries/jfoundry) dependencies, package layout, annotations, Repository/Port boundaries, persistence adapters, Outbox/Inbox, and ArchUnit rules. |

## Recommended Workflow

Use the plugin's `domain-architecture-workflow` capability as the general entry point:

1. Understand the business goal, actors, workflows, constraints, and uncertainty.
2. Use `domain-modeling` for non-trivial business behavior before implementation.
3. Use `domain-architecture-guidance` to decide whether DDD, Hexagonal, Onion, CQRS, ports/adapters, or simpler CRUD is appropriate.
4. Use framework-specific guidance only after the domain and architecture assumptions are clear. Use `use-jfoundry` only for [jfoundry](https://github.com/xfoundries/jfoundry) projects.
5. Verify the result with architecture tests, code review, or explicit risk notes when the implementation touches boundaries.

This plugin does not depend on any external workflow system. It can run alongside planning, TDD, code-review, or "superpowers"-style workflows: when another process workflow is active, use this plugin only for the domain and architecture decisions inside that process.

## Scope

Primary targets:

- Java / Kotlin backend systems
- C# / .NET backend systems
- Go backend systems
- Python backend systems

Conditional targets:

- Dart / Flutter
- Swift / iOS
- Other client applications with substantial offline domain logic, sync workflows, local persistence boundaries, or complex business rules

Do not use this plugin to force DDD, ports/adapters, CQRS, repositories, or layered projects into simple CRUD applications, thin mobile clients, or small scripts.

## Source Policy

The architecture guidance separates sources into three levels:

- Foundational sources: Eric Evans for DDD, Alistair Cockburn for Hexagonal Architecture, Martin Fowler for enterprise patterns and CQRS discussion, Greg Young for CQRS, Jeffrey Palermo for Onion Architecture, and Clean Architecture only as a cautious dependency-direction synthesis.
- Widely used implementation guidance: jMolecules, Microsoft .NET architecture guidance, Spring Modulith, ArchUnit, ArchUnitNET, and microservices.io.
- Opinionated synthesis and examples: useful for inspiration, but not canonical authority.

The plugin distinguishes DDD modeling concepts from architecture style constraints and framework conventions. It does not present DDD, Layered, Onion, Hexagonal, CQRS, and Event Sourcing as one canonical architecture.

## Installation

### Codex and `.agents/plugins` compatible agents

This repository includes a marketplace manifest at `.agents/plugins/marketplace.json`, so it can be added as a local or Git marketplace:

```bash
codex plugin marketplace add xfoundries/software-architecture-skills
codex plugin add domain-architecture@xfoundries
```

For local development from this checkout:

```bash
codex plugin marketplace add .
codex plugin add domain-architecture@xfoundries
```

The same shape works for compatible agents that read `.agents/plugins/marketplace.json`. The marketplace entry points at the repository root:

```json
{
  "name": "xfoundries",
  "interface": {
    "displayName": "Domain Architecture"
  },
  "plugins": [
    {
      "name": "domain-architecture",
      "source": {
        "source": "url",
        "url": "./"
      },
      "policy": {
        "installation": "AVAILABLE",
        "authentication": "ON_INSTALL"
      },
      "category": "Productivity"
    }
  ]
}
```

For a personal marketplace under `~/.agents/plugins`, you can also point `~/plugins/domain-architecture` at this repository and use a personal marketplace entry. The repo-local marketplace above is the preferred project-owned distribution shape.

### Claude Code

Claude Code can validate and install the same plugin source through its plugin system. The repository includes `.claude-plugin/plugin.json`, `.claude-plugin/marketplace.json`, and the same `skills/` capabilities used by other agents.

```bash
claude plugin validate .
claude plugin marketplace add xfoundries/software-architecture-skills
claude plugin install domain-architecture@xfoundries
```

For local development from this checkout:

```bash
claude plugin marketplace add .
claude plugin install domain-architecture@xfoundries
```

### Raw skill compatibility

Agents that do not support plugins can still consume the folders under `skills/` as plain `SKILL.md` skills. This is a compatibility fallback, not the preferred installation shape.

## Usage Examples

```text
Use $domain-architecture-workflow to guide this order-management project from requirements to architecture decisions.
```

```text
Use $domain-modeling to turn these billing requirements into commands, events, aggregates, invariants, and open questions.
```

```text
Use $domain-architecture-guidance to review this Java service and tell me whether the aggregate boundaries and Hexagonal dependencies are justified.
```

```text
Use $use-jfoundry to implement the confirmed model in a Java jfoundry project with Hexagonal Architecture and ArchUnit tests.
```

## Repository Layout

```text
.codex-plugin/
  plugin.json
.claude-plugin/
  marketplace.json
  plugin.json
.agents/plugins/
  marketplace.json
skills/
  domain-architecture-workflow/
  domain-modeling/
  domain-architecture-guidance/
  use-jfoundry/
```

## Updating

For local development, keep the marketplace source pointed at this repository. After changing plugin metadata, reinstall or update the plugin in the target agent so it refreshes cached metadata.

For Codex, update the `.codex-plugin/plugin.json` cachebuster when necessary and reinstall from `domain-architecture@xfoundries`.

## Design Principle

Use architecture patterns to protect business meaning and change boundaries. Do not use them as decorative structure.
