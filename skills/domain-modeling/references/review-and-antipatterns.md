# Review And Anti-Patterns

Use this to review a proposed model or to check work before implementation.

## Review Checklist

- Does each aggregate protect a real invariant?
- Are commands named as business tasks rather than CRUD operations?
- Are events meaningful business facts in past tense?
- Are value objects carrying validation and meaning, not just wrapping primitives mechanically?
- Are domain services reserved for domain decisions that do not belong to one aggregate?
- Are application services coordinating use cases instead of owning core business rules?
- Are repositories focused on aggregate lifecycle and command-side loading?
- Are read models separate from write aggregates when their shape differs?
- Are open questions visible before code hardens the model?

## Anti-Patterns

### Table-Driven Aggregate

Symptom: one aggregate per table, mostly getters/setters.

Fix: start from commands and invariants. Keep CRUD simple when there is no domain behavior.

### Anemic Domain Model

Symptom: application services contain all rules, domain objects only store data.

Fix: move lifecycle decisions and invariant checks into aggregate methods or value objects.

### Oversized Aggregate

Symptom: one aggregate changes for many unrelated workflows or loads a large object graph.

Fix: split around invariants and immediate consistency. Use events or policies for eventual reactions.

### Fake Domain Service

Symptom: a service is called domain service but handles application workflow, transaction demarcation, concrete persistence APIs, HTTP calls, security, or logging.

Fix: move technical workflow to application services or adapters. Keep only domain decisions that do not fit an entity, value object, or aggregate. Depending on a domain repository contract can be acceptable when the domain decision requires it; depending on ORM, mapper, query wrapper, HTTP client, or transaction APIs is not.

### Event As Command

Symptom: an event named in imperative form or used as something that can fail.

Fix: model the request as a command and the successful fact as a past-tense event.

### Over-Modeled CRUD

Symptom: many aggregates, repositories, factories, and services for simple data maintenance.

Fix: use a simpler transaction script or CRUD model until real invariants appear.
