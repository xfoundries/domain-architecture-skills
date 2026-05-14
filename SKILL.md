---
name: ddd-architecture-guidance
description: Authoritative architecture guidance for Domain-Driven Design, Layered Architecture, Onion Architecture, Hexagonal Architecture / Ports and Adapters, and CQRS. Use when Codex is asked to design, review, refactor, document, or implement business-domain systems, especially backend services in Java, Kotlin, C#, .NET, Go, or Python, using DDD concepts, jMolecules-style architectural annotations, package/project boundaries, repositories, aggregates, application services, ports/adapters, layered/onion/hexagonal structures, CQRS, or architecture tests. Also use for Dart/Flutter, Swift/iOS, or other client applications only when they contain substantial offline domain logic, sync workflows, complex business rules, or local persistence boundaries.
---

# DDD Architecture Guidance

## Purpose

Use this skill to guide AI agents and developers in correctly applying Domain-Driven Design, Layered Architecture, Onion Architecture, Hexagonal Architecture / Ports and Adapters, and CQRS in business-domain systems. It is backend-first, but can apply to client applications when the client owns meaningful business rules, offline behavior, sync logic, or local persistence boundaries. Treat jMolecules as a strong Java reference for expressing and validating DDD and architecture concepts in code, not as a universal framework that every language must copy.

Do not present DDD, Layered, Onion, Hexagonal, CQRS, or Event Sourcing as one canonical combined architecture. Distinguish foundational patterns from opinionated compositions.

DDD is a domain modeling methodology and language for business concepts, boundaries, and invariants. Layered, Onion, Hexagonal / Ports and Adapters, and CQRS are architecture styles or patterns that can host or support DDD in domain-heavy systems, but they are not owned by DDD and should not be reduced to DDD. When a project explicitly chooses one of those architectures, enforce its dependency, boundary, and responsibility rules consistently.

## Workflow

1. Identify the task type: architecture explanation, code review, implementation, refactoring, package/project layout, jMolecules annotation usage, architecture test, or documentation.
2. Determine the stack and runtime role: Java/Kotlin, C#/.NET, Go, Python, Dart/Flutter, Swift/iOS, or other. Prefer the host ecosystem's idioms before imposing examples from another language.
3. Identify the chosen architecture, if any: Layered, Onion, Hexagonal / Ports and Adapters, CQRS, mixed, or none. If one is chosen, apply its structural constraints before suggesting simplification.
4. Classify the domain complexity. Use rich DDD, aggregate modeling, CQRS, or strict ports/adapters only when the business rules and change pressure justify the ceremony.
5. Apply source hierarchy from `references/source-policy.md` before citing or enforcing a rule.
6. Apply architecture constraints from `references/architecture-constraints.md` before changing boundaries or dependency direction.
7. Apply backend implementation guidance from `references/backend-guidance.md` when changing code or recommending structure.
8. State uncertainty explicitly when a rule is context-dependent.

## Core Rules

- Prefer original and broadly recognized sources over blog-style synthesis.
- Do not weaken explicit architecture constraints. If the project has chosen strict Layered, Onion, Hexagonal / Ports and Adapters, or CQRS, controller-to-repository calls, infrastructure dependencies in the domain, bypassed application/use-case boundaries, and mixed command/query responsibilities are usually violations unless the project documents an exception.
- Use jMolecules primarily for Java/Kotlin code annotation, architecture expression, validation, and documentation.
- For C#/.NET, Go, Python, Dart, and Swift, translate concepts into idiomatic modules, packages, namespaces, protocols/interfaces, tests, and dependency rules.
- Apply this skill to mobile or frontend clients only when the client has real domain behavior. For thin UI clients, prefer simpler state management and API integration patterns.
- Keep the domain model free of framework persistence concerns when the domain is behavior-rich; allow simpler transaction script or CRUD designs for low-complexity areas.
- Treat CQRS as a targeted pattern for asymmetric read/write needs, complex queries, scalability differences, or task-based write models. Do not make every use case CQRS by default.
- Treat Event Sourcing as separate from CQRS. Do not introduce it unless auditability, temporal reconstruction, or event replay is a real requirement.
- Avoid saying "must" unless the rule follows from the chosen architecture and project context. Prefer "usually", "in this architecture", or "for this bounded context".

## References

- Read `references/source-policy.md` when deciding what sources are authoritative, how to handle Explicit Architecture, or how to cite architecture claims.
- Read `references/architecture-constraints.md` when deciding whether a dependency, call path, package reference, or use-case boundary is an architecture violation.
- Read `references/backend-guidance.md` when implementing or reviewing Java, Kotlin, C#, .NET, Go, Python, Dart, Swift, or similar business-domain code.
- Read `references/examples-java-kotlin.md` for jMolecules-oriented Java/Kotlin snippets.
- Read `references/examples-csharp-dotnet.md` for idiomatic C#/.NET examples.
- Read `references/examples-go.md` for Go package and port/adapters examples.
- Read `references/examples-python.md` for Python/FastAPI/Django guidance examples.
- Read `references/architecture-testing.md` for ArchUnit, ArchUnitNET, and lightweight architecture test examples.
