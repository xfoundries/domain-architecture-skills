---
name: domain-architecture-guidance
description: Authoritative architecture guidance for business-domain systems using Domain-Driven Design, Layered Architecture, Onion Architecture, Hexagonal Architecture / Ports and Adapters, and CQRS. Use when designing, reviewing, refactoring, documenting, or implementing domain-heavy systems with package/project boundaries, repositories, aggregates, application services, ports/adapters, CQRS, jMolecules-style annotations, or architecture tests.
---

# Domain Architecture Guidance

## Purpose

Use this skill to guide AI agents and developers in correctly applying Domain-Driven Design, Layered Architecture, Onion Architecture, Hexagonal Architecture / Ports and Adapters, and CQRS in backend business-domain systems. It can also guide client applications when they own substantial business rules, offline workflows, synchronization conflicts, or local persistence boundaries. It does not provide platform-specific mobile or frontend implementation templates. Treat jMolecules as a strong Java reference for expressing and validating DDD and architecture concepts in code, not as a universal framework that every language must copy.

Do not present DDD, Layered, Onion, Hexagonal, CQRS, or Event Sourcing as one canonical combined architecture. Distinguish foundational patterns from opinionated compositions.

DDD is a domain modeling methodology and language for business concepts, boundaries, and invariants. Layered, Onion, Hexagonal / Ports and Adapters, and CQRS are architecture styles or patterns that can host or support DDD in domain-heavy systems, but they are not owned by DDD and should not be reduced to DDD. When a project explicitly chooses one of those architectures, enforce its dependency, boundary, and responsibility rules consistently.

## Workflow

1. Identify the task type: architecture explanation, code review, implementation, refactoring, package/project layout, jMolecules annotation usage, architecture test, or documentation.
2. Determine the stack and runtime role: Java/Kotlin, C#/.NET, Go, Python, or another ecosystem. For a client application, confirm that it owns substantial domain behavior before applying backend-style patterns. Prefer host-ecosystem idioms over imported templates.
3. Identify the chosen architecture, if any: Layered, Onion, Hexagonal / Ports and Adapters, CQRS, mixed, or none. If one is chosen, apply its structural constraints before suggesting simplification.
4. Classify the domain complexity. Use rich DDD, aggregate modeling, CQRS, or strict ports/adapters only when the business rules and change pressure justify the ceremony.
5. Apply source hierarchy from `references/source-policy.md` before citing or enforcing a rule.
6. Apply architecture constraints from `references/architecture-constraints.md` before changing boundaries or dependency direction.
7. Apply backend implementation guidance from `references/backend-guidance.md` when changing code or recommending structure.
8. State uncertainty explicitly when a rule is context-dependent.
9. For architecture decisions, reviews, and boundary-affecting implementation recommendations, produce an `Architecture Guidance Result` from `references/architecture-result.md` with rule sources, constraints, evidence, open questions, and readiness for the recommended next step. Simple conceptual explanations and documentation answers may remain concise unless the user asks for a structured result.

## Core Rules

- Prefer original and broadly recognized sources over blog-style synthesis.
- When a project chooses DDD, enforce its core modeling discipline: ubiquitous language, explicit bounded-context meaning, invariant-protecting aggregates, identity/value distinction, and behavior placed where it best protects rules. Do not treat DDD as optional decoration after that choice is made.
- Do not weaken explicit architecture constraints. Apply only the constraints supplied by the selected style or an explicit project policy. For example, strict Layered and Hexagonal projects usually route controllers through application/use-case boundaries, while foundational Onion Architecture requires inward dependencies but does not itself require every controller call to pass through an application service. If the project has chosen CQRS, enforce command/query responsibility separation, query non-mutation, and read/write model boundaries without importing unrelated layer rules.
- Do not upgrade implementation preferences into universal DDD rules. Repository naming, read-port suffixes, CQRS, Event Sourcing, package layout, jMolecules annotations, and ArchUnit tests become strict only when selected by the architecture, framework, or project policy.
- Use jMolecules primarily for Java/Kotlin code annotation, architecture expression, validation, and documentation.
- For C#/.NET, Go, and Python, translate concepts into idiomatic modules, packages, namespaces, protocols/interfaces, tests, and dependency rules.
- Apply this skill to client applications only when they own substantial business behavior, offline workflows, synchronization conflicts, or local persistence boundaries. Do not imply platform-specific mobile or frontend implementation guidance.
- Keep the domain model free of framework persistence concerns when the domain is behavior-rich; allow simpler transaction script or CRUD designs for low-complexity areas.
- Treat CQRS as a targeted pattern for asymmetric read/write needs, complex queries, scalability differences, or task-based write models. Do not make every use case CQRS by default.
- Treat Event Sourcing as separate from CQRS. Do not introduce it unless auditability, temporal reconstruction, or event replay is a real requirement.
- Avoid saying "must" unless the rule follows from the chosen architecture and project context. Prefer "usually", "in this architecture", or "for this bounded context".

## Reference Routing

Read only the references required by the task.

- For an architecture decision, review, or boundary-affecting recommendation, read
  `references/architecture-result.md` before returning the result and
  `references/architecture-constraints.md` before judging a dependency or call path.
- Read `references/source-policy.md` when making a normative claim, choosing a source, or handling
  Explicit Architecture or Clean Architecture terminology.
- Read `references/hexagonal-architecture.md` only for selected or proposed Hexagonal / Ports and
  Adapters work, including port roles, adapter direction, or application-service naming.
- For implementation or code review, read `references/backend-guidance.md`. Retrieve current
  upstream documentation before using exact framework or library APIs.
- Read `references/architecture-testing.md` only when selecting, implementing, or reviewing
  architecture validation.
- For Java projects that use jfoundry, route confirmed architecture decisions to `using-jfoundry`
  for framework-specific landing rather than treating the Java example as a jfoundry template.
