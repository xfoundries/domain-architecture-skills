# Backend Guidance

## Scope

This skill targets backend business systems first. It can guide client applications when they own
substantial business rules, offline workflows, synchronization conflicts, or local persistence
boundaries. It does not provide platform-specific mobile or frontend implementation templates.

Primary backend targets:

- Java/Kotlin: Spring, Spring Boot, Spring Modulith, Jakarta EE, Quarkus, Micronaut, plain Java.
- C#/.NET: ASP.NET Core, Minimal APIs, controllers, application services, EF Core, MediatR-style command/query handlers, class library boundaries.
- Go: HTTP/RPC services, package boundaries, interfaces at consumer side, persistence adapters, event/message integrations.
- Python: FastAPI, Django, Flask, Celery, SQLAlchemy, Django ORM, service/application layers, domain modules.

Prefer existing codebase conventions over textbook layouts.

Use `architecture-constraints.md` for architecture-style dependency and call-path rules. Use
`hexagonal-architecture.md` for Hexagonal roles, port ownership, and adapter direction. This file
translates confirmed decisions into ecosystem idioms; it does not define those rules.

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

## Client Applications

Apply the same domain-modeling and architecture decision discipline only when the client owns
meaningful local business behavior. Keep UI state management, rendering, transport integration, and
platform framework concerns outside the domain model. Prefer the host platform's conventions and
retrieve current platform documentation when an implementation decision depends on a framework API.
