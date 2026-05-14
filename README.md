# DDD Architecture Guidance

中文 ｜ [English](README.md)

---

Source-aware architecture guidance for business-domain systems using Domain-Driven Design, Layered Architecture, Onion Architecture, Hexagonal Architecture / Ports and Adapters, and CQRS.

This skill is designed for AI coding agents that support `SKILL.md`-based skills, including Codex-style skill environments and Claude Code-style local skills.

## Purpose

This skill helps an AI agent design, review, refactor, document, or implement architecture decisions without treating every DDD-related pattern as one canonical "Clean + DDD + Hexagonal + CQRS" model.

The main practical reference for this skill is [jMolecules](https://github.com/xmolecules/jmolecules), an established Java project for expressing DDD building blocks and architectural styles in code. This skill uses jMolecules as a strong reference for Java/Kotlin and as a conceptual reference for other ecosystems, while still asking the agent to translate concepts into the target language instead of copying one framework's structure everywhere.

It also prioritizes original or broadly recognized sources when making architecture claims.

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

This skill should not be used to force DDD, ports/adapters, CQRS, repositories, or layered projects into simple CRUD applications, thin mobile clients, or small scripts.

## What It Covers

- DDD building blocks: bounded contexts, aggregates, entities, value objects, repositories, domain services, domain events
- Layered Architecture
- Onion Architecture
- Hexagonal Architecture / Ports and Adapters
- CQRS, without assuming Event Sourcing
- jMolecules-style architectural expression for Java/Kotlin
- Language-specific translation for C#/.NET, Go, Python, Dart, and Swift
- Source hierarchy and citation discipline for architecture claims

## Reference Organization

This skill intentionally organizes practical guidance by language/ecosystem and validation tooling, not by architecture labels such as `DDD.md`, `HEXAGONAL.md`, or `CQRS.md`.

The reason is that the goal is not to restate architecture concepts as fixed templates. The goal is to help agents make source-aware backend decisions and translate those decisions into idiomatic Java/Kotlin, C#/.NET, Go, Python, or client-side code when justified.

Architecture patterns overlap in practice. Organizing examples by ecosystem keeps the guidance closer to real implementation choices and reduces the risk of treating DDD, Layered, Onion, Hexagonal, Clean, CQRS, or Event Sourcing as one mandatory combined structure.

## Source Policy

The skill separates sources into three levels.

Foundational sources:

- Eric Evans on Domain-Driven Design
- Alistair Cockburn on Hexagonal Architecture / Ports and Adapters
- Martin Fowler on enterprise application architecture and CQRS
- Greg Young on CQRS
- Jeffrey Palermo on Onion Architecture
- Robert C. Martin on Clean Architecture, used cautiously for dependency direction and independence principles

Widely used implementation guidance:

- [jMolecules](https://github.com/xmolecules/jmolecules)
- [Microsoft Learn .NET DDD-oriented microservice guidance](https://learn.microsoft.com/en-us/dotnet/architecture/microservices/microservice-ddd-cqrs-patterns/ddd-oriented-microservice)
- Microsoft Learn / Azure Architecture Center / .NET architecture guides
- Spring Modulith
- [ArchUnit](https://www.archunit.org/) / [ArchUnit Java repository](https://github.com/TNG/ArchUnit)
- [ArchUnitNET](https://github.com/TNG/ArchUnitNET)
- microservices.io

Opinionated synthesis and examples:

- Herberto Graca's Explicit Architecture articles
- Example repositories that combine multiple architecture styles
- Blog posts that prescribe exact folder structures or universal rules

Opinionated synthesis can be useful, but it should not be treated as canonical authority.

Microsoft guidance is treated as a pragmatic implementation reference, especially for .NET microservices. It is useful for tradeoffs such as when to apply DDD, when simpler CRUD is enough, how to keep domain entities independent from infrastructure, and how to structure .NET layers or projects. It is not treated as the original definition of DDD, Hexagonal Architecture, Onion Architecture, or CQRS.

## Important Cautions

### Explicit Architecture

This skill does not treat "Explicit Architecture" as an authoritative architecture model.

The reason is that it is mainly a personal synthesis: it combines DDD, Hexagonal Architecture, Onion Architecture, Clean Architecture, CQRS, and related patterns into one named model. That can be useful as a practitioner reference, but it is not a broadly recognized foundation comparable to DDD, Hexagonal Architecture / Ports and Adapters, Onion Architecture, or CQRS.

Using it as a primary source can make a local composition appear to be an industry standard.

### Clean Architecture

This skill uses Clean Architecture cautiously.

The reason is that Clean Architecture intentionally synthesizes ideas already present in Hexagonal Architecture, Onion Architecture, BCE, and related boundary-focused approaches. Its dependency rule and independence goals are valuable, but it should not be treated as a wholly new, standalone architecture or as a mandatory four-ring folder structure.

When a concept belongs more directly to DDD, Hexagonal Architecture, or Onion Architecture, this skill asks the agent to prefer the original source.

## Installation

### skills.sh / skills CLI

After publishing this repository to GitHub, users can install it with the `skills` CLI:

```bash
npx skills add youngledo/ddd-architecture-guidance
```

Install for a specific agent:

```bash
npx skills add youngledo/ddd-architecture-guidance -a claude-code
npx skills add youngledo/ddd-architecture-guidance -a codex
npx skills add youngledo/ddd-architecture-guidance -a cursor
npx skills add youngledo/ddd-architecture-guidance -a opencode
```

List available skills from the repository:

```bash
npx skills add youngledo/ddd-architecture-guidance --list
```

### Codex-style skill directory

Copy the skill folder into your local skills directory:

```bash
mkdir -p ~/.agents/skills
cp -R ddd-architecture-guidance ~/.agents/skills/
```

If your environment uses `~/.codex/skills`, use that path instead:

```bash
mkdir -p ~/.codex/skills
cp -R ddd-architecture-guidance ~/.codex/skills/
```

### Claude Code-style skill directory

For a user-level skill:

```bash
mkdir -p ~/.claude/skills
cp -R ddd-architecture-guidance ~/.claude/skills/
```

For a project-level skill:

```bash
mkdir -p .claude/skills
cp -R ddd-architecture-guidance .claude/skills/
```

Claude Code does not need `agents/openai.yaml`; it can be left in place or removed.

## Publishing Notes

This repository is intended to work as a single-skill GitHub repository because `SKILL.md` lives at the repository root.

The skill can be installed directly from GitHub with `npx skills add youngledo/ddd-architecture-guidance`. The skills.sh detail page and badge may not appear until the repository has been indexed by skills.sh.

Optional badge after skills.sh indexing:

```md
[![skills.sh](https://skills.sh/b/youngledo/ddd-architecture-guidance)](https://skills.sh/youngledo/ddd-architecture-guidance)
```

Recommended GitHub topics:

```text
agent-skills
skill-md
skills-sh
claude-code
codex
cursor
opencode
ddd
domain-driven-design
hexagonal-architecture
cqrs
architecture-testing
jmolecules
archunit
```

## Usage Examples

Ask your agent:

```text
Use $ddd-architecture-guidance to review this Java service and tell me whether the aggregate boundaries are justified.
```

```text
Use $ddd-architecture-guidance to evaluate whether this .NET API should use CQRS or a simpler layered design.
```

```text
Use $ddd-architecture-guidance to propose an idiomatic Go package structure for this domain-heavy backend service.
```

```text
Use $ddd-architecture-guidance to decide whether DDD concepts are appropriate in this Flutter app with offline sync.
```

## Files

- `SKILL.md`: trigger description and core workflow
- `references/source-policy.md`: authority levels, citation policy, Explicit Architecture and Clean Architecture cautions
- `references/backend-guidance.md`: language and architecture guidance for backend-first business systems
- `references/examples-java-kotlin.md`: jMolecules-oriented Java/Kotlin examples
- `references/examples-csharp-dotnet.md`: C#/.NET examples
- `references/examples-go.md`: Go examples
- `references/examples-python.md`: Python examples
- `references/architecture-testing.md`: ArchUnit, ArchUnitNET, and lightweight architecture tests
- `agents/openai.yaml`: optional UI metadata for Codex/OpenAI-style skill environments

## Architecture Tests

For Java/Kotlin projects, this skill treats [ArchUnit](https://www.archunit.org/) and its [Java repository](https://github.com/TNG/ArchUnit) as preferred tools for architecture unit tests. Use it to verify dependency direction, package boundaries, layer rules, adapter isolation, and jMolecules-based architectural annotations.

For C#/.NET projects, this skill treats [ArchUnitNET](https://github.com/TNG/ArchUnitNET) as the corresponding architecture unit testing tool.

ArchUnit and ArchUnitNET are validation tools, not the source of the architecture model itself. The model should still come from the chosen architecture and source policy.

## Design Principle

Use architecture patterns to protect business meaning and change boundaries. Do not use them as decorative structure.
