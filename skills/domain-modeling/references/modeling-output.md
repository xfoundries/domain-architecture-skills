# Modeling Output Protocol

Use this before writing production code for a new bounded context, non-trivial use case, aggregate, domain event, or cross-aggregate workflow.

Keep the note concise. The point is to expose assumptions for review, not to create a large design document.

## Required Shape

```text
Bounded Context:

Ubiquitous Language:
- Term:

Commands:
- Command:
  Actor:
  Preconditions:
  Rejection rules:

Aggregates:
- Aggregate:
  Identity:
  Commands handled:
  Invariants protected:

Entities:
- Entity:

Value Objects:
- Value Object:
  Validation/meaning:

Domain Events:
- Event:
  Emitted by:
  Payload facts:

Domain Services / Policies:
- Name:
  Reason it does not belong to one aggregate:

Application Services:
- Use case:
  Coordination responsibilities:

Repositories:
- Aggregate repository:
  Load/save intent:

Read Needs / Read Models:
- Read need:
  Shape/purpose:

Open Questions:
- Question:
```

## Boundary Between Modeling And Architecture Mapping

Commands in this note mean business intentions that can be accepted or rejected. They do not imply
that implementation must create command classes, command handlers, or CQRS.

Read needs and read models identify query, screen, report, notification, or decision-support
shapes that should not distort write aggregates. They do not imply an implementation term such as
`QueryPort`, `ReadModelPort`, `LookupPort`, repository, controller, or API endpoint.

Map commands and read needs to application services, CQRS handlers, ports/adapters, or framework
types only after the domain assumptions and chosen architecture are clear.

## When To Ask For Review

Ask for review before coding when:

- A term has multiple possible meanings.
- An invariant is inferred, not stated.
- A command appears to modify multiple aggregates.
- A domain event might trigger external effects.
- A table/API shape is driving the model.
- The model would introduce a new bounded context.

## When To Keep It Lightweight

For simple CRUD, small field additions, or pure read-model changes, summarize only the affected concept, rule, and reason no richer model is needed.
