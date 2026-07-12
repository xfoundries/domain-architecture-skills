# Domain Architecture Plugin

English ｜ [中文](README_ZH.md)

---

A plugin-first architecture guidance package for business-domain software systems. It helps AI coding agents move from business requirements to domain models, architecture decisions, and framework-specific implementation guidance without treating DDD, Hexagonal Architecture, Onion Architecture, CQRS, or any framework convention as one mandatory combined model.

The distribution unit is the `domain-architecture` plugin. The `skills/` directory contains plugin-internal capabilities that are exposed by Codex, Claude Code, and other compatible agents after the plugin is installed.

## Core Capabilities

| Skill | Core role | Scope |
|---|---|---|
| `domain-architecture-workflow` | Default end-to-end coordinator; routes phases and produces `Domain Architecture Handoff`. | Language and framework neutral. |
| `domain-modeling` | Produces `Domain Modeling Result` for business language, behavior, boundaries, and invariants. | Language and framework neutral. |
| `domain-architecture-guidance` | Produces `Architecture Guidance Result` for architecture choices, dependency rules, CQRS, and validation. | Principles are language neutral; Java/Kotlin guidance is the most concrete. |
| `using-jfoundry` | Produces `JFoundry Implementation Guidance Result` for dependencies, layout, Ports, persistence, transactions, exception boundaries, messaging, and tests. | Java and jfoundry only. |

## How To Use

For end-to-end work, start with `domain-architecture-workflow`:

```text
requirements
-> domain-architecture-workflow
-> Domain Modeling Result
-> Architecture Guidance Result
-> optional JFoundry Implementation Guidance Result
-> Domain Architecture Handoff
```

`Domain Architecture Handoff` is the coordinator's composite result for the next planning, implementation, or review activity. It preserves specialist results and phase status without replacing them or requiring a fixed file format.

### Standalone

```text
Use $domain-architecture-workflow for this business project.
Business goal and known rules:
Existing artifacts and constraints:
JFoundry: yes | no | undecided
Desired next activity:
```

For focused work, invoke `domain-modeling`, `domain-architecture-guidance`, or `using-jfoundry` directly.

`using-jfoundry` translates a confirmed architecture into framework-specific guidance. It preserves Architecture Guidance results, existing project evidence, established conventions sufficient for simple changes, or an explicit user choice; it does not default an undecided project to Hexagonal Architecture.

If jfoundry use is undecided, framework-neutral Domain Modeling and Architecture Guidance continue without invoking `using-jfoundry`. The workflow asks for that choice only when a framework-specific next activity materially depends on it.

### With A Process Companion

Superpowers, SpecKit, OpenSpec, and similar workflows are optional companions. They own their specification, planning, tasks, implementation, review, files, and commands; this plugin owns the domain and architecture results, optional jfoundry landing, and handoff.

```text
Use <process companion> for the development process.
Use $domain-architecture-workflow for domain and architecture decisions after
requirements are understood and before dependent planning or tasks. Feed
Domain Architecture Handoff into the companion's next activity.
```

If a blocker changes business meaning or architecture, return it to the companion-owned requirements or specification activity instead of guessing. See the [first-use guide](skills/domain-architecture-workflow/references/first-use.md) for the complete input, ownership, status, and return rules.

## Scope And Limits

- The core modeling and architecture methods are language and framework neutral, but implementation guidance is deepest for Java/Kotlin. C#/.NET, Go, and Python have mappings and examples with fewer integrations and templates; `using-jfoundry` is Java-only.
- The primary target is business backend software. Client applications are a conditional fit when they contain substantial offline domain logic, synchronization, persistence boundaries, or complex rules.
- Do not force DDD, Ports and Adapters, CQRS, repositories, or layered structures into simple CRUD applications, thin clients, or small scripts.

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
codex plugin marketplace add xfoundries/domain-architecture-skills
codex plugin add domain-architecture@xfoundries
```

For local development from this checkout:

```bash
codex plugin marketplace add .
codex plugin add domain-architecture@xfoundries
```

Compatible agents can use the same repo-owned [marketplace manifest](.agents/plugins/marketplace.json), which points at the plugin root.

### Claude Code

Claude Code can validate and install the same plugin source through its plugin system. The repository includes `.claude-plugin/plugin.json`, `.claude-plugin/marketplace.json`, and the same `skills/` capabilities used by other agents.

```bash
claude plugin validate .
claude plugin marketplace add xfoundries/domain-architecture-skills
claude plugin install domain-architecture@xfoundries
```

For local development from this checkout:

```bash
claude plugin marketplace add .
claude plugin install domain-architecture@xfoundries
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
  using-jfoundry/
```

## Updating

For local development, keep the marketplace source pointed at this repository. After changing plugin metadata, reinstall or update the plugin in the target agent so it refreshes cached metadata.

For Codex, update the `.codex-plugin/plugin.json` cachebuster when necessary and reinstall from `domain-architecture@xfoundries`.

## Design Principle

Use architecture patterns to protect business meaning and change boundaries. Do not use them as decorative structure.
