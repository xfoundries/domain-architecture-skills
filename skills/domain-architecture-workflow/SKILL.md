---
name: domain-architecture-workflow
description: "Use when a business software project needs an end-to-end domain and architecture workflow: understanding requirements, modeling domains, deciding DDD/Hexagonal/Onion/CQRS boundaries, planning implementation, or handing off to framework-specific guidance such as jfoundry. Does not require any external workflow skill."
---

# Domain Architecture Workflow

## Purpose

Use this skill as the entry point for business-domain software architecture work. It coordinates domain modeling, architecture guidance, and optional framework-specific landing without depending on any external workflow skill.

If another planning, TDD, or review workflow is already active, use this skill only for the domain and architecture decisions inside that workflow.

## Workflow

1. Understand the business goal, acceptance expectations, actors, workflows, constraints, and uncertainty; classify how much domain and architecture work the requested increment needs.
2. Use `domain-modeling` for non-trivial business behavior and consume its `Domain Modeling Result`.
3. Use `domain-architecture-guidance` to choose appropriate boundaries and consume its `Architecture Guidance Result`.
4. Use `using-jfoundry` only when applicable and after domain and architecture assumptions are clear. If jfoundry use is undecided, continue framework-neutral phases without invoking it and defer the choice until a framework-specific next activity materially requires it.
5. Produce a composite `Domain Architecture Handoff` that makes planning readiness, dependent blockers, and the next owner explicit.
6. Persist workflow-owned artifacts under the default documentation directory, then route detailed planning to its `plans` subdirectory or to the user-selected process companion. The companion alone owns its plan files, tasks, and execution state.
7. During implementation or review, revisit the modeling or architecture phase when changed business meaning, drift, or conflicts invalidate assumptions.

## Skill Routing

- Use `domain-modeling` for requirements-to-model work: ubiquitous language, business commands, events, bounded contexts, aggregates, invariants, value objects, domain services, repositories, and read needs/read models.
- Use `domain-architecture-guidance` for architecture decisions and reviews: dependency direction, Layered/Onion/Hexagonal/CQRS boundaries, jMolecules-style annotations, architecture tests, and source-aware rule interpretation.
- Use `using-jfoundry` only when the target project uses jfoundry or the user explicitly asks for jfoundry-specific dependencies, package layout, annotations, Repository/Port guidance, persistence adapters, Outbox/Inbox, or ArchUnit setup. `Undecided` alone does not make this specialist applicable.

## Result And Handoff Routing

Read [references/first-use.md](references/first-use.md) when the user asks how to start an end-to-end project or combine this workflow with an optional process companion.

Read [references/implementation-planning.md](references/implementation-planning.md) when handing a completed analysis to detailed planning, selecting the smallest independently verifiable increment, or later introducing a process companion to an existing handoff.

Read [references/workflow-results.md](references/workflow-results.md) before coordinating phases. Require each specialist to return its own phase-specific payload in the shared result envelope. Check phase status and combine results without rewriting specialist guidance. When a phase reports `needs-input`, pause only dependent progression, preserve completed results, produce an interim handoff with blockers, and ask the smallest blocking question. Treat process companions as optional and user-selected; never make this workflow depend on one.

- Read [workflow scenarios](references/workflow-scenarios.md) when reviewing or validating routing behavior.

## Handoff Rules

- Do not let framework choices drive the domain model.
- Do not force DDD ceremony into low-complexity CRUD.
- Keep open business questions visible before coding.
- Attribute guidance to the right source: DDD concepts, architecture style constraints, framework conventions, heuristics, or project policies.
- Keep this workflow independent. It can run alongside other process skills, but it must not require them.
- Treat a `Domain Architecture Handoff` as planning input, not as a detailed implementation plan. Use the plugin's default documentation directory for workflow-owned artifacts and never reproduce a selected companion's process artifacts.
