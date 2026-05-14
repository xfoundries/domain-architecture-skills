# Source Policy

## Authority Levels

Use this hierarchy when explaining, reviewing, or implementing architecture guidance.

### Foundational sources

Prefer these for definitions and first-principles claims:

- Eric Evans, *Domain-Driven Design* and DDD reference material: ubiquitous language, bounded context, entity, value object, aggregate, repository, domain service.
- Alistair Cockburn, Hexagonal Architecture / Ports and Adapters: inside/outside separation, ports, adapters, technology independence.
- Martin Fowler: enterprise application patterns, CQRS discussion, repository/unit of work context, anemic domain model critique.
- Bertrand Meyer command-query separation, as the predecessor idea for CQRS.
- Greg Young's CQRS materials where CQRS-specific claims are needed.
- Robert C. Martin's Clean Architecture article only for dependency direction and independence principles. Treat it as a synthesis and popularization of related ideas such as Hexagonal Architecture, Onion Architecture, BCE, and similar boundary-focused approaches, not as a wholly new or independently authoritative architecture.
- Jeffrey Palermo's Onion Architecture for onion-specific dependency and ring terminology.

### Widely used implementation and guidance sources

Use these for pragmatic backend implementation guidance, especially Java and .NET:

- jMolecules project, https://github.com/xmolecules/jmolecules: the main practical reference for this skill. Use it for expressing DDD building blocks and Layered, Onion, Hexagonal, CQRS concepts in Java/Kotlin code; architecture verification and documentation with tools such as ArchUnit and jQAssistant. Treat it as a strong Java/Kotlin reference and a conceptual reference for other languages, not as a cross-language implementation mandate.
- Microsoft Learn / Azure Architecture Center / .NET architecture guides: DDD-oriented microservices, tactical DDD, CQRS, bounded contexts, aggregates, and .NET implementation tradeoffs.
- Spring Modulith documentation when the codebase uses Spring and application modules.
- ArchUnit documentation, https://www.archunit.org/, and Java repository, https://github.com/TNG/ArchUnit: use for Java/Kotlin architecture unit tests that verify dependency direction, package boundaries, layer rules, adapter isolation, and jMolecules-based annotations. Treat it as a validation tool, not as the source of the architecture model.
- ArchUnitNET, https://github.com/TNG/ArchUnitNET: use as the corresponding C#/.NET architecture unit testing tool for dependency direction, namespace/assembly boundaries, layer rules, and framework dependency constraints. Treat it as a validation tool, not as the source of the architecture model.
- microservices.io for microservice data patterns such as CQRS, saga, transactional outbox, and API composition.

### Opinionated synthesis and examples

Use these only as examples or inspiration, not as canonical authority:

- Herberto Graca's "Explicit Architecture" articles.
- Example repositories that combine DDD, Clean, Onion, Hexagonal, CQRS, Event Sourcing, or microservices into one layout.
- Blog posts that prescribe exact folder structures or universal rules.

When using these sources, label them as opinionated practice. Do not make their folder structure, naming, dependency graph, or pattern combination mandatory.

## Explicit Architecture Policy

Do not base this skill on "Explicit Architecture" as an authoritative architecture model. It is a personal integration of DDD, Hexagonal, Onion, Clean, CQRS, and related patterns.

Be cautious because its value is mainly synthetic and opinionated: it combines several distinct patterns into one named model, but that name is not a broadly recognized foundation comparable to DDD, Hexagonal Architecture / Ports and Adapters, Onion Architecture, or CQRS. Using it as a primary source can make a local composition appear to be an industry standard.

It may be mentioned only as:

- An example of one practitioner's synthesis.
- A comparison point when explaining why combining patterns increases complexity.
- A source of candidate heuristics after validating them against foundational sources and the target codebase.

Do not cite it to justify a universal rule such as:

- Every backend must use all layers/rings/ports.
- CQRS and Event Sourcing belong in every DDD architecture.
- A particular package layout is the standard form of Clean, Onion, or Hexagonal Architecture.

## Clean Architecture Policy

Use Clean Architecture cautiously. It is a useful vocabulary for dependency direction, framework independence, and separating business rules from delivery and persistence details, but it should not be treated as a completely new or standalone architecture.

Be cautious because Clean Architecture intentionally synthesizes ideas already present in Hexagonal Architecture, Onion Architecture, BCE, and related approaches. Its diagram and terminology are influential, but they can obscure the original source of a concept and are often overinterpreted as a mandatory four-ring folder structure or universal implementation recipe.

When Clean Architecture overlaps with Hexagonal Architecture, Onion Architecture, or Layered Architecture:

- Prefer the original source for the concept being discussed.
- Use Hexagonal / Ports and Adapters for port, adapter, driver, driven-side, and inside/outside boundary discussions.
- Use Onion Architecture for ring-oriented dependency direction and domain-centered layering.
- Use DDD sources for bounded contexts, aggregates, entities, value objects, repositories, domain services, and domain events.
- Use Clean Architecture mainly as a cross-cutting dependency rule and terminology bridge.

Do not cite Clean Architecture to justify a rigid folder structure, mandatory use-case class for every endpoint, mandatory repository abstraction, or a universal "entities/use cases/interface adapters/frameworks" layout. Treat those as implementation choices that must be justified by the target codebase.

## Citation Discipline

When answering users:

- Cite foundational or project documentation for definitions.
- Cite Microsoft or ecosystem docs for implementation guidance in Java/C# backend systems.
- Cite jMolecules for Java/Kotlin annotation and validation mechanics.
- Avoid citing personal blogs as the sole authority for a normative architecture claim.
- Separate "the source says" from "I infer" when adapting a concept to a specific codebase.
