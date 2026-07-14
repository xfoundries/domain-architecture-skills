# Backend Guidance

## Scope

This skill targets backend business systems first, and client applications only when they contain substantial domain behavior.

Primary backend targets:

- Java/Kotlin: Spring, Spring Boot, Spring Modulith, Jakarta EE, Quarkus, Micronaut, plain Java.
- C#/.NET: ASP.NET Core, Minimal APIs, controllers, application services, EF Core, MediatR-style command/query handlers, class library boundaries.
- Go: HTTP/RPC services, package boundaries, interfaces at consumer side, persistence adapters, event/message integrations.
- Python: FastAPI, Django, Flask, Celery, SQLAlchemy, Django ORM, service/application layers, domain modules.

Conditional client targets:

- Dart/Flutter and Swift/iOS: use only when there are complex local rules, offline-first workflows, sync conflict handling, local persistence, payments, booking, medical/finance/business workflows, or other domain-heavy client behavior.

Prefer existing codebase conventions over textbook layouts.

Before judging a dependency or call path, identify whether the project has explicitly chosen Layered, Onion, Hexagonal / Ports and Adapters, CQRS, or a simpler CRUD style. If an architecture has been chosen, enforce that architecture's boundary rules consistently. Simplicity is not a reason to silently bypass an explicit architectural constraint.

## Java and jMolecules

Use jMolecules when the project wants architecture concepts to be explicit in Java/Kotlin code and validated by tools.

Appropriate uses:

- Annotate domain building blocks: aggregate root, entity, value object, repository, domain event.
- Mark package or type roles for Layered, Onion, or Hexagonal Architecture.
- Mark CQRS concepts such as command, command handler, query model, or dispatcher.
- Add ArchUnit, https://www.archunit.org/ and https://github.com/TNG/ArchUnit, or jQAssistant checks around declared concepts.
- Generate or improve documentation from code-level architecture metadata.

Do not add jMolecules annotations mechanically. Use them when they improve readability, validation, documentation, or integration.

Prefer package-level annotations where the whole package has a stable architectural role. Prefer class-level annotations when packages are mixed or migration is incremental.

Use ArchUnit, https://www.archunit.org/ and https://github.com/TNG/ArchUnit, for architecture unit tests when Java/Kotlin boundaries should be enforced continuously. Good targets include dependency direction, forbidden framework dependencies in the domain, package access rules, adapter isolation, and consistency between jMolecules annotations and package structure.

## C# and .NET

jMolecules is not the implementation model for C#. Translate concepts into idiomatic .NET structures:

- Use projects or namespaces to express dependency boundaries, for example `Domain`, `Application`, `Infrastructure`, and `Api` when the codebase already follows that shape.
- Keep domain objects free from ASP.NET Core, EF Core, messaging, serialization, and dependency injection dependencies when the domain is behavior-rich.
- Use EF Core mappings, configuration classes, or persistence models outside the domain when persistence concerns would pollute the model.
- Use application services or command/query handlers to orchestrate use cases, transactions, authorization checks, and calls to ports.
- Use architecture tests when available, especially ArchUnitNET, https://github.com/TNG/ArchUnitNET, or NetArchTest/custom assertions when that better fits the project.

Do not force MediatR, repository abstractions, or CQRS into simple CRUD applications. Use them when they clarify use cases, reduce coupling, or support different read/write models.

Use ArchUnitNET for architecture unit tests when C#/.NET boundaries should be enforced continuously. Good targets include assembly and namespace dependencies, forbidden ASP.NET Core or EF Core dependencies in the domain, layer access rules, adapter isolation, and consistency between project references and the chosen architecture.

## Go

Translate architecture concepts into package boundaries and dependency direction. Do not copy Java-style class hierarchies.

- Keep domain packages small and behavior-oriented when the domain is rich.
- Put interfaces near the consumer when practical, especially for outbound ports used by application or domain services.
- Use application/usecase packages for orchestration, transaction boundaries, and coordination with adapters.
- Keep database, HTTP, queue, and framework code in adapters or infrastructure packages.
- Avoid a repository interface for every table. Add interfaces where they protect domain/application code from volatile infrastructure or improve testing of business logic.
- Prefer explicit functions and structs over abstract factories or inheritance-style patterns.

## Python

Use DDD and ports/adapters selectively because Python frameworks often encourage pragmatic vertical slices.

- For Django, avoid fighting the framework when the app is CRUD-heavy. Extract domain services or pure domain modules only where rules become complex.
- For FastAPI/Flask, use routers/controllers as delivery adapters, application services for use cases, and infrastructure modules for persistence and external systems.
- Keep pure domain logic independent from ORM models when persistence concerns would obscure invariants.
- Use protocols, abstract base classes, or duck typing only when they clarify a boundary.
- Avoid deep package trees for small services.

## Dart/Flutter and Swift/iOS

Use this skill carefully for mobile clients. Most mobile screens do not need backend-style DDD architecture.

Apply DDD, ports/adapters, or CQRS-inspired separation when:

- The app must work offline and later sync with conflict handling.
- Local decisions enforce business rules, not just UI validation.
- Local persistence is complex enough to need stable boundaries.
- The client has task-based workflows with commands and derived read models.
- The app is in a domain such as finance, healthcare, logistics, booking, field service, or enterprise operations.

Do not introduce aggregates, repositories, or CQRS just to organize view models. Prefer idiomatic platform patterns such as Flutter state management, SwiftUI state, coordinators, services, and persistence wrappers unless domain complexity justifies more.

## Pattern Selection

### Layered Architecture

Use when the system benefits from a conventional separation of interface/API, application/service, domain, and infrastructure/persistence concerns.

Be careful: layer names alone do not guarantee dependency direction. Verify actual imports/references.

In strict layered designs, controllers should usually call application services rather than repositories directly. This keeps use-case orchestration, transaction boundaries, authorization decisions, and domain interaction out of delivery adapters.

### Onion Architecture

Use when the team wants an explicit domain core surrounded by application and infrastructure rings. Dependencies should point inward.

Avoid treating every ring as a physical project unless the codebase benefits from stronger compile-time boundaries.

In Onion Architecture, infrastructure belongs outside the domain core. Domain model code should not depend on persistence frameworks, web frameworks, messaging frameworks, or adapter implementations.

Onion Architecture does not supply Primary/Secondary Port or Adapter roles or mandatory class-name
suffixes. Let DDD ubiquitous language name domain types, let application types describe business
actions and orchestration responsibilities, and let infrastructure types add technology names only
where they identify the implementation. An inner-ring interface implemented by infrastructure is a
dependency-inversion contract; `*Port` is optional project vocabulary, not an Onion requirement.

### Hexagonal Architecture / Ports and Adapters

Use when the domain/application needs to be isolated from delivery mechanisms and external systems. Model primary adapters as inbound drivers and secondary adapters as outbound integrations.

Prefer ports for real variability, testing value, or boundary protection. Avoid one-interface-per-class abstractions that only mirror a single implementation.

In strict Hexagonal Architecture, primary adapters call primary ports or application services, not secondary adapters or repositories directly. Core code expresses outbound needs as secondary ports; place each port near the application or domain code that consumes it. Secondary adapters implement those ports.

### CQRS

Use when separating writes and reads reduces complexity or enables independent optimization. Common signals:

- Read models differ substantially from write aggregates.
- Query performance or data shape requirements conflict with the write model.
- Commands represent business tasks with validation and invariants.
- Different consistency or scaling needs exist for reads and writes.

Do not equate CQRS with Event Sourcing. Do not split models just because the code uses methods named `Handle`.

## Review Checklist

When reviewing code:

- Identify the chosen architectural style before judging violations.
- Check dependency direction with imports, project references, package references, and framework dependencies.
- Check whether aggregates protect invariants or merely wrap database rows.
- Check whether repositories expose persistence-shaped queries that leak infrastructure concerns into the domain/application.
- Check whether application services coordinate use cases without accumulating domain rules that belong in the model.
- Check whether CQRS read models are justified by query shape or scaling needs.
- Check whether added abstractions reduce coupling or only add ceremony.
