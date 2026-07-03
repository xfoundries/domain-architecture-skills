# Software Architecture Skills

English ｜ [中文](README_ZH.md)

---

A multi-skill methodology pack for business-domain software architecture. It helps AI coding agents move from business requirements to domain models, architecture decisions, and framework-specific implementation guidance without treating DDD, Hexagonal Architecture, Onion Architecture, CQRS, or any framework convention as one mandatory combined model.

This repository is designed for agents that support `SKILL.md`-based skills, including Codex-style skill environments and Claude Code-style local skills.

## Skills

| Skill | Purpose |
|---|---|
| `domain-architecture-workflow` | Entry-point workflow for end-to-end business-domain architecture work. Coordinates modeling, architecture decisions, and optional framework-specific landing. |
| `domain-modeling` | Framework-neutral DDD/domain modeling workflow for ubiquitous language, commands, events, bounded contexts, aggregates, invariants, value objects, domain services, repositories, and read models. |
| `domain-architecture-guidance` | Source-aware architecture guidance for DDD, Layered, Onion, Hexagonal / Ports and Adapters, CQRS, jMolecules-style annotations, and architecture tests. |
| `use-jfoundry` | JFoundry-specific application guidance for Java business projects using [jfoundry](https://github.com/xfoundries/jfoundry) dependencies, package layout, annotations, Repository/Port boundaries, persistence adapters, Outbox/Inbox, and ArchUnit rules. |

## Recommended Workflow

Use `domain-architecture-workflow` as the general entry point:

1. Understand the business goal, actors, workflows, constraints, and uncertainty.
2. Use `domain-modeling` for non-trivial business behavior before implementation.
3. Use `domain-architecture-guidance` to decide whether DDD, Hexagonal, Onion, CQRS, ports/adapters, or simpler CRUD is appropriate.
4. Use framework-specific guidance only after the domain and architecture assumptions are clear. Use `use-jfoundry` only for [jfoundry](https://github.com/xfoundries/jfoundry) projects.
5. Verify the result with architecture tests, code review, or explicit risk notes when the implementation touches boundaries.

This pack does not depend on any external workflow system. It can run alongside planning, TDD, code-review, or "superpowers"-style workflows: when another process workflow is active, use these skills only for the domain and architecture decisions inside that process.

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

Do not use these skills to force DDD, ports/adapters, CQRS, repositories, or layered projects into simple CRUD applications, thin mobile clients, or small scripts.

## Source Policy

The architecture guidance separates sources into three levels:

- Foundational sources: Eric Evans for DDD, Alistair Cockburn for Hexagonal Architecture, Martin Fowler for enterprise patterns and CQRS discussion, Greg Young for CQRS, Jeffrey Palermo for Onion Architecture, and Clean Architecture only as a cautious dependency-direction synthesis.
- Widely used implementation guidance: jMolecules, Microsoft .NET architecture guidance, Spring Modulith, ArchUnit, ArchUnitNET, and microservices.io.
- Opinionated synthesis and examples: useful for inspiration, but not canonical authority.

The skills distinguish DDD modeling concepts from architecture style constraints and framework conventions. They do not present DDD, Layered, Onion, Hexagonal, CQRS, and Event Sourcing as one canonical architecture.

## Installation

### skills.sh / skills CLI

Install the repository:

```bash
npx skills add youngledo/software-architecture-skills
```

Install for a specific agent:

```bash
npx skills add youngledo/software-architecture-skills -a claude-code
npx skills add youngledo/software-architecture-skills -a codex
npx skills add youngledo/software-architecture-skills -a cursor
npx skills add youngledo/software-architecture-skills -a opencode
```

Install one skill explicitly:

```bash
npx skills add youngledo/software-architecture-skills --skill domain-architecture-workflow
npx skills add youngledo/software-architecture-skills --skill domain-modeling
npx skills add youngledo/software-architecture-skills --skill domain-architecture-guidance
npx skills add youngledo/software-architecture-skills --skill use-jfoundry
```

List available skills:

```bash
npx skills add youngledo/software-architecture-skills --list
```

### Codex-style skill directory

Copy all skills:

```bash
mkdir -p ~/.agents/skills
cp -R skills/* ~/.agents/skills/
```

If your environment uses `~/.codex/skills`, use that path instead:

```bash
mkdir -p ~/.codex/skills
cp -R skills/* ~/.codex/skills/
```

### Claude Code-style skill directory

For user-level skills:

```bash
mkdir -p ~/.claude/skills
cp -R skills/* ~/.claude/skills/
```

For project-level skills:

```bash
mkdir -p .claude/skills
cp -R skills/* .claude/skills/
```

Claude Code does not need `agents/openai.yaml`; it can be left in place.

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
Use $use-jfoundry to implement the confirmed model in a Java 21 jfoundry project with Hexagonal Architecture and ArchUnit tests.
```

## Repository Layout

```text
skills/
  domain-architecture-workflow/
  domain-modeling/
  domain-architecture-guidance/
  use-jfoundry/
```

## Updating

Skills installed through the `skills` CLI are not updated automatically by Claude Code, Codex, Cursor, or OpenCode. After this repository is updated, users should run:

```bash
npx skills update domain-architecture-workflow
npx skills update domain-modeling
npx skills update domain-architecture-guidance
npx skills update use-jfoundry
```

## Design Principle

Use architecture patterns to protect business meaning and change boundaries. Do not use them as decorative structure.
