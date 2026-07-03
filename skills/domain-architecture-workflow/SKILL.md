---
name: domain-architecture-workflow
description: "Use when a business software project needs an end-to-end domain and architecture workflow: understanding requirements, modeling domains, deciding DDD/Hexagonal/Onion/CQRS boundaries, planning implementation, or handing off to framework-specific guidance such as jfoundry. Does not require any external workflow skill."
---

# Domain Architecture Workflow

## Purpose

Use this skill as the entry point for business-domain software architecture work. It coordinates domain modeling, architecture guidance, and optional framework-specific landing without depending on any external workflow skill.

If another planning, TDD, or review workflow is already active, use this skill only for the domain and architecture decisions inside that workflow.

## Workflow

1. Understand the business goal, actors, workflows, constraints, and uncertainty.
2. Use domain modeling for non-trivial business behavior before implementation.
3. Use domain architecture guidance to decide whether DDD, Hexagonal, Onion, CQRS, ports/adapters, or simpler CRUD is appropriate.
4. Choose framework-specific landing guidance only after the domain and architecture assumptions are clear.
5. Verify the result with architecture tests, code review, or explicit risk notes when the implementation touches boundaries.

## Skill Routing

- Use `domain-modeling` for requirements-to-model work: ubiquitous language, commands, events, bounded contexts, aggregates, invariants, value objects, domain services, repositories, and read models.
- Use `domain-architecture-guidance` for architecture decisions and reviews: dependency direction, Layered/Onion/Hexagonal/CQRS boundaries, jMolecules-style annotations, architecture tests, and source-aware rule interpretation.
- Use `use-jfoundry` only when the target project uses jfoundry or the user explicitly asks for jfoundry-specific dependencies, package layout, annotations, Repository/Port guidance, persistence adapters, Outbox/Inbox, or ArchUnit setup.

## Handoff Rules

- Do not let framework choices drive the domain model.
- Do not force DDD ceremony into low-complexity CRUD.
- Keep open business questions visible before coding.
- Attribute rules to the right source: DDD concepts, architecture style constraints, framework conventions, or project-local decisions.
- Keep this workflow independent. It can run alongside other process skills, but it must not require them.
