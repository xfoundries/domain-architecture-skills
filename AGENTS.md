# Agent Instructions

This repository contains the `domain-architecture` plugin for business-domain software architecture.

The plugin exposes these internal skills:

- `domain-architecture-workflow`: entry-point workflow and routing.
- `domain-modeling`: framework-neutral domain modeling.
- `domain-architecture-guidance`: source-aware architecture guidance.
- `use-jfoundry`: jfoundry-specific application guidance.

## Scope

The repository helps agents and developers move from business requirements to domain models, architecture decisions, and optional framework-specific implementation guidance.

The plugin covers:

- Domain-Driven Design concepts and modeling workflow.
- Layered Architecture.
- Onion Architecture.
- Hexagonal Architecture / Ports and Adapters.
- CQRS, without assuming Event Sourcing.
- Architecture unit tests such as ArchUnit and ArchUnitNET.
- jMolecules-style expression for Java/Kotlin.
- jfoundry-specific business project guidance.

Do not collapse DDD, Layered, Onion, Hexagonal / Ports and Adapters, CQRS, Event Sourcing, jMolecules, or jfoundry into one canonical model. Attribute every recommendation to the right level: domain modeling, architecture style, implementation guidance, framework convention, or project-local decision.

## Skill Boundaries

- Treat the plugin as the installation and distribution unit. The `skills/` directories are plugin-internal capabilities and compatibility assets, not independent products.
- Keep `.agents/plugins/marketplace.json` as the repo-owned marketplace entry for Codex and other compatible agents. It should point at the repository root plugin with `source.url: "./"`.
- Keep `.claude-plugin/marketplace.json` as the repo-owned marketplace entry for Claude Code. Do not force Claude Code to consume the Codex marketplace schema.
- Keep `domain-architecture-workflow` as a coordinator. It should route to other skills and define phase order, not duplicate their detailed references.
- Keep `domain-modeling` framework-neutral. It should not assume jfoundry, Spring, .NET, Go, Python, or a specific architecture style.
- Keep `domain-architecture-guidance` source-aware. `references/source-policy.md` remains authoritative for source hierarchy.
- Keep `use-jfoundry` jfoundry-specific. Do not move general DDD methodology into it.
- Do not make this repository depend on superpowers or any other external process framework. It may mention that this plugin can be used alongside planning, TDD, or review workflows.

## Source Policy

Prefer foundational and broadly recognized sources for architecture claims:

- Eric Evans for DDD concepts.
- Alistair Cockburn for Hexagonal Architecture / Ports and Adapters.
- Martin Fowler for enterprise application patterns and CQRS discussion.
- Greg Young for CQRS-specific material.
- Jeffrey Palermo for Onion Architecture.
- Robert C. Martin's Clean Architecture article only for dependency direction and independence principles.
- jMolecules as the main practical Java/Kotlin reference.
- Microsoft architecture guidance for pragmatic .NET/backend implementation.
- ArchUnit and ArchUnitNET as architecture validation tools.

Do not treat Herberto Graca's "Explicit Architecture" as an authoritative model. It may be mentioned only as an opinionated synthesis.

Do not treat Clean Architecture as a wholly new, standalone architecture. Use it cautiously as a synthesis and terminology bridge for dependency direction and independence principles.

## Editing Rules

- Keep each skill's `SKILL.md` concise. Put detailed guidance in that skill's `references/`.
- Keep references one level below the skill directory and link them directly from `SKILL.md`.
- Keep examples short and labeled as sketches. They should demonstrate translation choices, not prescribe a universal project template.
- Preserve the distinction between foundational sources, implementation guidance, opinionated synthesis, and framework conventions.
- Preserve architecture constraints when a project explicitly chooses Layered, Onion, Hexagonal / Ports and Adapters, or CQRS.
- Do not introduce universal rules such as mandatory CQRS, mandatory Event Sourcing, mandatory repository abstractions, mandatory folder structures, or mandatory jfoundry adoption.
- Use "usually", "when justified", or "in this architecture" for context-dependent guidance.
- Keep guidance language-neutral where possible, then translate into ecosystem-specific advice.
- Do not make jMolecules a cross-language implementation mandate.
- Do not make ArchUnit or ArchUnitNET sources of architecture rules; they validate rules chosen from architecture and codebase context.

## Documentation Rules

- Update both `README.md` and `README_ZH.md` for user-facing changes.
- Keep English and Chinese READMEs aligned in meaning, even if not line-by-line translations.
- Keep installation instructions plugin-first and compatible with Codex `.agents/plugins` and Claude Code plugin workflows.
- Prefer the repo-local marketplace workflow over loose user-level skill copying.
- Mention raw `skills/` installation only as a fallback for agents without plugin support.
- Avoid marketing claims. State scope and limits clearly.

## Validation

After editing skill metadata or `SKILL.md`, validate every checked-in skill:

```bash
for skill in skills/*; do
  python3 /Users/huangxiao/.codex/skills/.system/skill-creator/scripts/quick_validate.py "$skill"
done
```

Before publishing, check:

- `.codex-plugin/plugin.json` validates with the Codex plugin validator.
- `.claude-plugin/plugin.json` validates with `claude plugin validate`.
- `.agents/plugins/marketplace.json` remains present and points at the repository root plugin.
- `.claude-plugin/marketplace.json` remains present and validates with `claude plugin validate --strict`.
- Every `skills/*/SKILL.md` has valid YAML frontmatter with `name` and `description`.
- `domain-architecture-workflow` does not hard-depend on superpowers or any other external workflow skill.
- `domain-modeling` contains modeling workflow and output protocol guidance without framework assumptions.
- `domain-architecture-guidance/references/source-policy.md` explains source hierarchy and cautions around Explicit Architecture and Clean Architecture.
- `domain-architecture-guidance/references/architecture-constraints.md` separates DDD modeling concepts from Layered, Onion, Hexagonal / Ports and Adapters, and CQRS structural rules.
- `use-jfoundry` remains a downstream business project skill, not a framework-maintenance skill.
- `README.md` and `README_ZH.md` mention all shipped skills.
