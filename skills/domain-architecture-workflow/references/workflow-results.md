# Workflow Results

## Common Result Envelope

Return each phase result with these fields:

- **Phase**
- **Status**: `completed`, `needs-input`, or `not-applicable`
- **Inputs**
- **Summary**
- **Assumptions**
- **Decisions**
- **Constraints**
- **Evidence**
- **Open Questions**
- **Artifacts**
- **Recommended Next Step**
- **Handoff Notes**

Treat this envelope as a shared interoperability and content contract, not a mandatory file format. Each specialist owns and returns its phase-specific payload inside the envelope. The coordinator checks status and combines results; it does not rewrite or replace specialist guidance. Use **Artifacts** to reference persisted specialist results when available, but do not treat it as the only payload carrier.

Mark a phase `completed` when its usable result is ready. Mark it `needs-input` rather than guess when missing information blocks a responsible decision. Pause only dependent phase progression, preserve completed results, emit an interim `Domain Architecture Handoff` containing the blockers, and ask the smallest blocking question. Mark a phase `not-applicable` and record why it is unnecessary.

## Phase Order And Transitions

Run phases in this order:

1. Establish business context.
2. Produce and consume the `Domain Modeling Result` from `domain-modeling`.
3. Produce and consume the `Architecture Guidance Result` from `domain-architecture-guidance`.
4. Produce and consume the `using-jfoundry` result only when jfoundry applies.
5. Produce the `Domain Architecture Handoff`.

Move backward when later work invalidates earlier assumptions:

- Return from architecture to modeling for ambiguous domain facts.
- Return from jfoundry guidance to architecture when framework conventions conflict with chosen boundaries.
- Return from implementation or review to architecture when code drifts from the chosen architecture.
- Return from implementation to modeling when business meaning changes.

Preserve prior confirmed results. State what changed, why it changed, and which downstream results need revision.

## Blocking Rules

Use `needs-input` for each example below only when the unresolved information prevents a responsible current or downstream decision. Otherwise record it as an assumption or open question and let the responsible specialist classify the detail:

- conflicting meanings for the same domain term;
- an invariant that would otherwise be inferred rather than confirmed;
- an unresolved transaction spanning aggregates;
- unknown delivery, ordering, retry, or idempotency semantics at an external boundary;
- an unknown that could change the architecture choice or dependency direction;
- a framework convention that conflicts with the chosen architecture;
- an exact dependency or runtime version that cannot be recommended safely from verified evidence.

## Process Companion Selection

Honor an explicit process choice and cooperate with an already active workflow. Do not ask the user to select a process companion for standalone modeling, architecture guidance, review, or jfoundry guidance. For an end-to-end request, ask only when the execution choice materially affects the handoff. Never select a companion merely because it is installed.

Treat Superpowers, OpenSpec, and SpecKit only as optional examples of process companions. Do not depend on them or reproduce their procedures.

## Domain Architecture Handoff

Produce one composite handoff containing:

- the requested outcome;
- the selected process companion, if any;
- each phase state;
- result summaries and artifacts;
- confirmed decisions;
- governing constraints;
- open questions, including blockers;
- the framework landing, or why none applies;
- the recommended next activity.

Keep distinctions explicit among domain-modeling decisions, architecture-style constraints, framework conventions, heuristics, and project policies.
