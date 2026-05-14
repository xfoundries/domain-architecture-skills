# Agent Instructions

This repository contains software architecture skills. The current skill is `ddd-architecture-guidance` under `skills/ddd-architecture-guidance/`.

Follow these instructions when editing, reviewing, or publishing this skill.

## Scope

This skill provides source-aware architecture guidance for business-domain systems using:

- Domain-Driven Design
- Layered Architecture
- Onion Architecture
- Hexagonal Architecture / Ports and Adapters
- CQRS
- Architecture unit tests such as ArchUnit and ArchUnitNET

It is backend-first, with primary attention to Java/Kotlin, C#/.NET, Go, and Python. Apply it to Dart/Flutter, Swift/iOS, or other client applications only when the client owns substantial domain behavior such as offline workflows, sync conflict handling, local persistence boundaries, or complex business rules.

The core purpose is to help agents and developers apply DDD and the listed architecture styles correctly. DDD is a domain modeling methodology; Layered, Onion, Hexagonal / Ports and Adapters, and CQRS are architecture styles or patterns with their own constraints. Do not collapse them into a single DDD-centric model.

## Source Policy

Keep `skills/ddd-architecture-guidance/references/source-policy.md` authoritative for source hierarchy.

Prefer foundational and broadly recognized sources for architecture claims:

- Eric Evans for DDD concepts
- Alistair Cockburn for Hexagonal Architecture / Ports and Adapters
- Martin Fowler for enterprise application patterns and CQRS discussion
- Greg Young for CQRS-specific material
- Jeffrey Palermo for Onion Architecture
- Robert C. Martin's Clean Architecture article only for dependency direction and independence principles
- jMolecules as the main practical Java/Kotlin reference
- Microsoft architecture guidance, especially https://learn.microsoft.com/en-us/dotnet/architecture/microservices/microservice-ddd-cqrs-patterns/ddd-oriented-microservice, for pragmatic .NET/backend implementation
- ArchUnit and ArchUnitNET as architecture validation tools

Do not treat Herberto Graca's "Explicit Architecture" as an authoritative model. It may be mentioned only as an opinionated synthesis.

Do not treat Clean Architecture as a wholly new, standalone architecture. Use it cautiously as a synthesis and terminology bridge for dependency direction and independence principles.

## Editing Rules

- Keep each skill's `SKILL.md` concise. Put detailed guidance in that skill's `references/`.
- Organize practical examples by language/ecosystem or validation tool, not by architecture label. Prefer `examples-java-kotlin.md`, `examples-csharp-dotnet.md`, `examples-go.md`, `examples-python.md`, and `architecture-testing.md` over `DDD.md`, `HEXAGONAL.md`, or `CQRS.md`.
- Keep examples short and labeled as sketches. They should demonstrate translation choices, not prescribe a full project template.
- Preserve the distinction between foundational sources, implementation guidance, and opinionated synthesis.
- Preserve architecture constraints when a project explicitly chooses Layered, Onion, Hexagonal / Ports and Adapters, or CQRS. Do not soften a real boundary violation by saying the rule is "not DDD"; attribute it to the correct architecture.
- Do not introduce universal rules such as mandatory CQRS, mandatory Event Sourcing, mandatory repository abstractions, or mandatory folder structures.
- Use "usually", "when justified", or "in this architecture" for context-dependent guidance.
- Keep guidance language-neutral where possible, then translate into ecosystem-specific advice.
- Do not make jMolecules a cross-language implementation mandate. It is a strong Java/Kotlin reference and a conceptual reference elsewhere.
- Do not make ArchUnit or ArchUnitNET sources of architecture rules. They validate rules chosen from the architecture and codebase context.

## Documentation Rules

- Update both `README.md` and `README_ZH.md` for user-facing changes.
- Keep English and Chinese READMEs aligned in meaning, even if not line-by-line translations.
- Keep installation instructions compatible with both Codex-style and Claude Code-style skill directories.
- Avoid marketing claims. State scope and limits clearly.

## Validation

After editing skill metadata or `SKILL.md`, validate the checked-in skill path:

```bash
python3 /Users/huangxiao/.codex/skills/.system/skill-creator/scripts/quick_validate.py /Users/huangxiao/Workspace/mine/software-architecture-skills/skills/ddd-architecture-guidance
```

Before publishing, check:

- `skills/ddd-architecture-guidance/SKILL.md` has valid YAML frontmatter with `name` and `description`.
- `skills/ddd-architecture-guidance/references/source-policy.md` explains why Explicit Architecture and Clean Architecture require caution.
- `skills/ddd-architecture-guidance/references/architecture-constraints.md` exists and clearly separates DDD modeling rules from Layered, Onion, Hexagonal / Ports and Adapters, and CQRS structural rules.
- `skills/ddd-architecture-guidance/references/backend-guidance.md` covers Java/Kotlin, C#/.NET, Go, Python, and conditional mobile/client usage.
- Example files exist for Java/Kotlin, C#/.NET, Go, Python, and architecture testing when the README mentions them.
- `README.md` and `README_ZH.md` mention jMolecules, ArchUnit, and ArchUnitNET.
- The skill does not imply that DDD, Layered, Onion, Hexagonal, CQRS, and Event Sourcing form one canonical architecture.
