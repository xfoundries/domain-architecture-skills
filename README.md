# Domain Architecture Plugin

English ｜ [中文](README_ZH.md)

---

A plugin-first architecture guidance package for business-domain software systems. It helps AI coding agents turn business requirements into domain models, architecture decisions, and, when applicable, framework-specific implementation guidance. It does not treat DDD, Hexagonal Architecture, Onion Architecture, CQRS, or a framework convention as one mandatory combined model.

## Quick Start

### Codex

```bash
codex plugin marketplace add xfoundries/domain-architecture-skills
codex plugin add domain-architecture@xfoundries
```

Confirm the plugin appears in `codex plugin list`, then start with this prompt:

```text
Use $domain-architecture-workflow for this business project.

Business goal and known rules:
Existing project or artifacts:
Technology constraints:
JFoundry: yes | no | undecided
Desired next activity:
```

The workflow uses the evidence provided, asks only for facts that block a responsible decision, and returns the applicable specialist results plus a `Domain Architecture Handoff` for planning, implementation, or review.

For local development from this checkout, use the local source instead:

```bash
codex plugin marketplace add .
codex plugin add domain-architecture@xfoundries
```

Use one source for the `xfoundries` marketplace name. To switch between a local checkout and the Git source, remove the existing marketplace first:

```bash
codex plugin marketplace remove xfoundries
```

### Claude Code And Compatible Agents

Claude Code can validate and install the same plugin source through its plugin system:

```bash
claude plugin validate .
claude plugin marketplace add xfoundries/domain-architecture-skills
claude plugin install domain-architecture@xfoundries
```

The repository also includes an [`.agents/plugins` marketplace manifest](.agents/plugins/marketplace.json) for compatible agents. Its `skills/` directory is plugin-internal; install the `domain-architecture` plugin rather than copying individual skills.

## What It Does

For end-to-end work, start with `domain-architecture-workflow`:

```text
requirements
-> domain modeling
-> architecture guidance
-> optional jfoundry landing
-> Domain Architecture Handoff
-> detailed planning or the selected process companion
```

The handoff preserves specialist results, decisions, constraints, open questions, and blockers. It identifies the smallest planning-ready increment and its next owner; it is planning input, not a detailed implementation plan. Persisted workflow artifacts use `docs/domain-architecture/`, and standalone detailed plans use its `plans/` child directory.

| Need | Entry point |
|---|---|
| End-to-end business-domain analysis and handoff | `domain-architecture-workflow` |
| Business language, behavior, invariants, or boundaries | `domain-modeling` |
| Architecture decision or boundary review | `domain-architecture-guidance` |
| Confirmed jfoundry implementation landing | `using-jfoundry` |

## Scope And Limits

- The core modeling and architecture methods are language and framework neutral, but implementation guidance is deepest for Java/Kotlin. C#/.NET, Go, and Python receive ecosystem mapping rather than code templates; `using-jfoundry` is Java-only.
- The primary target is business backend software. Client applications are a conditional fit when they own substantial domain behavior, offline workflows, synchronization conflicts, or local persistence boundaries; this plugin does not provide platform-specific mobile or frontend implementation templates.
- Do not force DDD, Ports and Adapters, CQRS, repositories, or layered structures into simple CRUD applications, thin clients, or small scripts.

## Advanced Use

- `using-jfoundry` applies only after jfoundry is confirmed or explicitly requested. An undecided framework does not block framework-neutral modeling and architecture guidance. Its [architecture landing](skills/using-jfoundry/references/architecture.md) preserves the selected style rather than choosing one.
- A process companion such as Superpowers, SpecKit, or OpenSpec is optional and user-selected. It owns its own specifications, plans, tasks, implementation, review, files, and commands; this plugin owns the specialist results and handoff. The [first-use guide](skills/domain-architecture-workflow/references/first-use.md) defines the input, ownership, status, and return rules.
- Selected architecture styles retain their own constraints. Aggregate repositories, adapter vocabulary, integration contracts, and reliable messaging are governed by the [architecture constraints](skills/domain-architecture-guidance/references/architecture-constraints.md) and the applicable specialist references; the plugin does not infer those choices from package names or available framework features.

## Source Policy

The architecture guidance separates sources into three levels:

- Foundational sources: Eric Evans for DDD, Alistair Cockburn for Hexagonal Architecture, Martin Fowler for enterprise patterns and CQRS discussion, Greg Young for CQRS, Jeffrey Palermo for Onion Architecture, and Clean Architecture only as a cautious dependency-direction synthesis.
- Widely used implementation guidance: jMolecules, Microsoft .NET architecture guidance, Spring Modulith, ArchUnit, ArchUnitNET, and microservices.io.
- Opinionated synthesis and examples: useful for inspiration, but not canonical authority.

The plugin distinguishes DDD modeling concepts from architecture style constraints and framework conventions. It does not present DDD, Layered, Onion, Hexagonal, CQRS, and Event Sourcing as one canonical architecture.

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
