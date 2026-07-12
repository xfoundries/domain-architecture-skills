# Domain Modeling Result Protocol

Use this protocol before writing production code for a new bounded context, non-trivial use case,
aggregate, domain event, or cross-aggregate workflow. The result exposes business meaning,
assumptions, and readiness for architecture analysis; it is not a large design document.

Return the phase-specific **Domain Model** owned by `domain-modeling` inside the shared result
envelope. Keep the result concise enough for review and downstream architecture mapping.

## Required Shape

```text
Phase: Domain Modeling
Status: completed | needs-input | not-applicable
Inputs:
Summary:
Assumptions:
Decisions:
Constraints:
Evidence:
Open Questions:
Artifacts:
Recommended Next Step:
Handoff Notes:

Domain Model:
  Bounded Context Scope:
    Primary context:
    Candidate / related contexts:
    Meaning / ownership boundary:

  Ubiquitous Language:
  - Term:
    Meaning:
    Context/source:

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

  Domain Services:
  - Name:
    Business rule:
    Reason rule does not belong to one aggregate:

  Policies:
  - Name:
    Trigger:
    Decision/reaction:
    Resulting command or outcome:

  Application Coordination Needs (later mapped to application services when justified):
  - Use case:
    Coordination needed:
    Business reason:

  Aggregate Lifecycle / Access Needs (later mapped to repositories when justified):
  - Aggregate:
    Load/save intent:
    Business reason:

  Read Needs / Read Models:
  - Read need:
    Shape/purpose:
```

## Status Semantics

- Use `completed` when the domain model is ready for architecture analysis. Preserve reasonable
  inferred details as explicit assumptions or open questions when responsible progression remains
  possible.
- Use `needs-input` only when missing or ambiguous business information about a material term,
  invariant, cross-aggregate interaction, external effect, or bounded-context boundary prevents a
  usable Domain Model. Ask the smallest question that resolves the blocker.
- Use `not-applicable` for simple CRUD or a pure read-model change that does not require richer
  domain modeling. Record the affected concept, governing constraints, and reason modeling is not
  applicable.

Do not treat every inferred invariant or multi-aggregate command as automatically blocking. The
modeling specialist owns the readiness judgment and records non-blocking uncertainty in
**Assumptions** or **Open Questions**.

Record architecture, integration, or dependency uncertainty that does not block a usable Domain
Model in **Open Questions** or **Handoff Notes** for the downstream specialist. Do not use that
uncertainty alone to mark Domain Modeling `needs-input`.

## Boundary Between Modeling And Architecture Mapping

Commands in this note mean business intentions that can be accepted or rejected. They do not imply
that implementation must create command classes, command handlers, or CQRS.

Read needs and read models identify query, screen, report, notification, or decision-support
shapes that should not distort write aggregates. They do not imply an implementation term such as
`QueryPort`, `ReadModelPort`, `LookupPort`, repository, controller, or API endpoint.

Application coordination needs describe business use cases that require coordination. Aggregate
lifecycle and access needs describe business-required load/save intent. Neither selects service
classes, interfaces, repository abstractions, ports, or framework types.

Map commands, read needs, coordination needs, and lifecycle/access needs to architecture and
implementation constructs only after the domain assumptions and chosen architecture are clear.

## When To Ask For Review

Ask for review before coding when any of these conditions could affect business meaning. A review
need does not by itself require `needs-input`; apply the status semantics above.

- A term has multiple possible meanings.
- An invariant is inferred, not stated.
- A command appears to modify multiple aggregates.
- A domain event might trigger external effects.
- A table/API shape is driving the model.
- The model would introduce a new bounded context.

## When To Keep It Lightweight

For simple CRUD, small field additions, or pure read-model changes, summarize only the affected
concept, governing constraints, and reason no richer model is needed. Use `not-applicable` when the
phase adds no responsible modeling decision; otherwise return the smallest useful completed model.
