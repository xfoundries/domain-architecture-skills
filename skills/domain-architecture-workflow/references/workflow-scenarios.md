# Workflow Scenarios

Use these compact scenarios to review routing and result protocols. Each scenario verifies the expected route, phase-local result status, evidence, and prohibited behavior; it is not a full answer key.

## Scenario 1: Simple CRUD

**Input:** Add basic create, read, update, and delete operations for a reference-data record with no stated business rules.

**Expected:** Route lightly. Domain Modeling may return `not-applicable`, and Architecture Guidance may return `not-applicable` when established conventions need no new decision. Each result records the affected concept, governing project evidence, and why richer modeling or stricter architecture is unnecessary.

**Prohibited:** Inventing aggregates, ports, CQRS, events, repositories, or other ceremony merely to fill a result.

## Scenario 2: Nontrivial Order Cancellation

**Input:** Design cancellation for orders whose eligibility depends on fulfillment state, payment state, and a cancellation deadline.

**Expected:** Route to Domain Modeling first. Return a `completed` Domain Modeling Result only after making the cancellation invariant, rejection behavior, and resulting state transition or outcome explicit with business evidence; include a domain event only when the evidence shows that the occurrence matters beyond the state change. Architecture Guidance consumes that result afterward.

**Prohibited:** Starting with framework packages, handlers, persistence, or transport mappings before the domain result.

## Scenario 3: Ambiguous Credit Subject And Invariant

**Input:** "A customer must never exceed their credit limit," but "customer" might mean an account, legal entity, or billing group and the limit's owner is unstated.

**Expected:** Domain Modeling returns `needs-input`, preserves known evidence, and asks the smallest blocking business question: which subject owns the limit and must be checked atomically?

**Prohibited:** Guessing an aggregate boundary or converting the ambiguity into an architecture-owned decision.

## Scenario 4: Hexagonal Controller Bypasses A Port

**Input:** Review an existing Hexagonal application where a controller calls a database adapter directly.

**Expected:** Architecture Guidance returns a focused `completed` result identifying a primary-to-secondary adapter bypass. Cite project evidence documenting the selected Hexagonal architecture, plus any applicable project policy, with the affected dependency direction and practical correction.

**Prohibited:** Presenting the finding as a universal DDD rule or remodeling the domain without domain evidence.

## Scenario 5: CQRS Without Event Sourcing

**Input:** A reporting-heavy use case needs a read shape different from the transactional write model.

**Expected:** Architecture Guidance may return `completed` with targeted CQRS when evidence justifies separate command and query models. Record Event Sourcing as a separate, unselected choice unless independent requirements justify it.

**Prohibited:** Treating CQRS as system-wide or implying that CQRS requires Event Sourcing.

## Scenario 6: Cross-Aggregate Transaction Ambiguity

**Input:** One business command appears to update two candidate aggregates, but the business-required consistency boundary is unknown.

**Expected:** Domain Modeling returns `needs-input` when that missing consistency fact blocks a usable model, preserves nonblocking findings, and asks the smallest question about what must succeed or fail atomically. Dependent architecture progression pauses in the interim handoff.

**Prohibited:** Silently choosing a saga, distributed transaction, or one-aggregate boundary.

## Scenario 7: Non-JFoundry .NET Project

**Input:** Produce an architecture handoff for a .NET service that does not use jfoundry and did not request it.

**Expected:** Run applicable modeling and architecture phases, skip `using-jfoundry`, and record in the composite handoff that no framework landing applies because the target is a non-jfoundry .NET project.

**Prohibited:** Supplying Java dependencies, jMolecules annotations, Maven layouts, or jfoundry templates.

## Scenario 8: JFoundry After Confirmed Onion Architecture

**Input:** Land a confirmed Onion Architecture decision in a jfoundry project.

**Expected:** JFoundry Implementation Guidance consumes the `Architecture Guidance Result` and returns a `completed` JFoundry Landing using Onion-aligned templates, package roles, dependency rules, and verification commands.

**Prohibited:** Replacing the confirmed Onion decision with a default Hexagonal layout or reopening it solely for framework convenience.

## Scenario 9: Explicit External Process Companion

**Input:** The user explicitly selects an external planning or review process for an end-to-end domain architecture task.

**Expected:** Treat the user's explicit selection as process evidence, honor that companion, and return each phase's shared-envelope result plus the composite handoff through its handoff points. Each specialist retains its phase-local `completed`, `needs-input`, or `not-applicable` status.

**Prohibited:** Depending on, reproducing, or assuming the companion's internal directory, artifact, or execution layout.

## Scenario 10: Direct Architecture Review

**Input:** Review an existing service's architecture boundaries using its selected Layered Architecture ADR and a current package-dependency report; no process companion has been selected.

**Expected:** Route directly to Architecture Guidance and return a `completed` phase-local result with project evidence, constraints, and next-step readiness.

**Prohibited:** Selecting a companion because it is installed or asking an irrelevant process-selection question.

## Scenario 11: JFoundry Exception Boundary

**Input:** A jfoundry Spring MVC service calls a remote adapter through an application-owned secondary port. The adapter can return expected absence or business rejection, throw a known timeout, or expose domain rule and lifecycle failures.

**Expected:** JFoundry Implementation Guidance returns a `completed` landing backed by the exception and Spring runtime references. Expected remote `404` or business rejection is represented in the port result and interpreted by the application or domain; a known timeout becomes `ExternalAccessException` with its cause preserved. Spring maps `DomainRuleViolationException` to `422` and `DomainStateException` to `409`, while client responses keep raw external details private.

**Prohibited:** Mapping every non-2xx response to `ExternalAccessException`, making domain code depend on application exceptions, mapping `DomainRuleViolationException` to `409`, or exposing raw external details.

## Scenario 12: First Use With A Specification Companion

**Input:** An order-cancellation specification is approved, SpecKit is explicitly selected, and the user asks how to apply domain architecture before planning and task generation.

**Expected:** Use the specification and available project evidence as inputs, route through Domain Modeling and Architecture Guidance, use jfoundry guidance only when applicable, and return the specialist results plus `Domain Architecture Handoff` to the companion-owned next activity. Keep SpecKit responsible for its files, commands, templates, approval gates, planning, and task generation. Return any blocking business or architecture question with an interim handoff to the companion-owned specification activity.

**Prohibited:** Selecting a different companion, assuming or reproducing SpecKit directories or commands, treating the handoff as a mandatory file format, skipping responsible modeling or architecture work, or generating dependent plans and tasks from guessed decisions.

## Scenario 13: Existing JFoundry Project Preserves Architecture

**Input:** Add a use case to an existing jfoundry project whose ADR and architecture tests establish Onion Architecture.

**Expected:** Preserve Onion Architecture and land the change using project evidence. Return `completed` when the affected boundaries are clear without reopening architecture selection.

**Prohibited:** Replacing Onion with Hexagonal because the request directly invoked `using-jfoundry` or because Hexagonal templates are bundled.

## Scenario 14: Simple JFoundry CRUD

**Input:** Add basic reference-data CRUD to a jfoundry application using established application and persistence conventions, with no new business rules or boundary decision.

**Expected:** Keep the established simple conventions. Domain Modeling and Architecture Guidance may be `not-applicable`; JFoundry Implementation Guidance records the project evidence and returns the smallest usable landing.

**Prohibited:** Introducing Hexagonal ports, aggregates, CQRS, or new Maven modules solely because jfoundry is present.

## Scenario 15: New JFoundry Project Without An Architecture Decision

**Input:** Scaffold a new domain-heavy jfoundry project, but no Architecture Guidance Result, project evidence, or explicit architecture choice is available.

**Expected:** Return `needs-input`, recommend Architecture Guidance, and ask the smallest question needed to resolve the architecture before selecting package and architecture-test templates.

**Prohibited:** Silently selecting Hexagonal Architecture as a scaffolding default.

## Scenario 16: JFoundry Undecided Does Not Block

**Input:** Produce Domain Modeling and Architecture Guidance for a business system whose framework choice is undecided and whose next activity is architecture review rather than framework-specific implementation.

**Expected:** Complete the applicable framework-neutral phases without invoking `using-jfoundry`. Record jfoundry as a pending optional landing in the `Domain Architecture Handoff`; do not ask for a framework choice until a framework-specific next activity materially requires it.

**Prohibited:** Marking Domain Modeling or Architecture Guidance `needs-input`, invoking `using-jfoundry`, or forcing a jfoundry decision solely because the framework choice is undecided.
