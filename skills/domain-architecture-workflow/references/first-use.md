# First Use

## Choose The Entry Point

Use `domain-architecture-workflow` when the request should move from business requirements through domain modeling, architecture decisions, and an implementation handoff. Call a specialist directly for focused work:

| Need | Entry point |
|---|---|
| End-to-end business-domain analysis and handoff | `domain-architecture-workflow` |
| Domain language, behavior, invariants, or boundaries only | `domain-modeling` |
| Architecture decision or boundary review only | `domain-architecture-guidance` |
| Confirmed model and architecture landing in jfoundry only | `using-jfoundry` |

## Useful Inputs

Start from available project evidence. Useful inputs include:

- business goal, actors, workflows, known rules, and rejection cases;
- requirements, specifications, ADRs, code, APIs, schemas, or event notes;
- existing architecture and constraints that must be preserved;
- language, runtime, persistence, messaging, and other technology constraints;
- whether jfoundry is used, not used, or undecided;
- the desired next activity, such as specification review, planning, task generation, implementation, or architecture review;
- an explicitly selected or already active process companion, if any.

Do not turn this list into a mandatory questionnaire. Inspect supplied evidence, record nonblocking gaps as assumptions or open questions, and ask only the smallest question that blocks a responsible result.

## Standalone Invocation

Use a prompt such as:

```text
Use $domain-architecture-workflow for this business project.

Business goal:
Known rules and evidence:
Existing project or artifacts:
Technology constraints:
JFoundry: yes | no | undecided
Process companion: none
Desired next activity:

Return the applicable specialist results and a Domain Architecture Handoff.
```

The specialists produce, and the coordinator consumes, `Domain Modeling Result`, `Architecture Guidance Result`, and, only when jfoundry applies, `JFoundry Implementation Guidance Result`. The coordinator then produces `Domain Architecture Handoff` for the next activity.

`JFoundry: undecided` does not block framework-neutral Domain Modeling or Architecture Guidance and does not by itself invoke `using-jfoundry`. Preserve the uncertainty as a pending optional landing. Ask about it only when the next activity requires framework-specific implementation guidance and the choice materially changes that activity.

`Domain Architecture Handoff` is the coordinator-owned composite interoperability result. It preserves phase states, specialist summaries or artifacts, confirmed decisions, constraints, open questions and blockers, framework landing or non-applicability, and the recommended next activity. It does not replace specialist payloads and does not require a fixed file format. When persisted, workflow artifacts use `docs/domain-architecture/`.

Before detailed planning, identify the smallest independently verifiable increment, its non-goals,
the phases it depends on, and its planning owner. Read
[implementation-planning.md](implementation-planning.md) for the depth-selection and handoff rules.

## Process Companion Contract

A process companion is optional. Honor one explicitly selected by the user or already active in the project; never choose one merely because it is installed.

| Owner | Responsibilities |
|---|---|
| Process companion | Its requirements/specification lifecycle, planning, task generation, implementation, review, approval gates, files, directories, commands, and templates |
| Domain Architecture plugin | Domain Modeling, Architecture Guidance, optional jfoundry landing, phase-local status, `Domain Architecture Handoff`, and its default `docs/domain-architecture/` artifacts |

Use the companion's own mechanisms to persist or consume results. Do not assume, create, or reproduce its internal paths, commands, templates, or artifact layout unless that active workflow explicitly requires them. A companion may help produce initial requirement material, but it must consume the Domain Architecture Handoff before finalizing a detailed implementation plan or dependent tasks.

The stable integration sequence is:

```text
requirements or specification
-> domain-architecture-workflow
-> Domain Modeling Result
-> Architecture Guidance Result
-> optional JFoundry Implementation Guidance Result
-> Domain Architecture Handoff
-> companion-owned planning, tasks, implementation, and review
```

## Superpowers Example

```text
Use Superpowers as the process companion for this task.
Use $domain-architecture-workflow for the domain and architecture decisions
after the business requirements are sufficiently understood and before the
implementation plan is finalized. Feed Domain Architecture Handoff into the
companion-owned planning, TDD, implementation, and review activities.
```

Superpowers owns its process. This plugin owns the specialist decisions and handoff inside that process; it does not require or reproduce Superpowers procedures.

## Specification Workflow Example

Use the same contract with SpecKit, OpenSpec, or another specification workflow:

```text
Use the active specification workflow as the process companion.
After an initial specification exists and before downstream planning or task
generation, use $domain-architecture-workflow for Domain Modeling, Architecture
Guidance, and optional jfoundry landing. Return Domain Architecture Handoff as
input to the companion's next activity. Keep the companion's files, commands,
templates, and approval gates under that companion's control.
```

When a domain or architecture blocker changes the specification, return the question and preserved interim handoff to the companion-owned requirements or specification activity. Do not generate dependent plans or tasks from guessed business meaning.

## Later Companion Adoption

If the plugin produced domain and architecture artifacts before a process companion was chosen,
do not repeat completed phases merely to adopt the companion. Give it the persisted specialist
results and handoff, including confirmed decisions, constraints, and open questions. It creates
its own specification and plan artifacts from that input. Revisit a specialist only when current
requirements contradict the existing evidence or the planned increment changes business meaning
or architecture boundaries.

## Consuming And Revisiting The Handoff

- When all required phases are `completed` or responsibly `not-applicable`, use the handoff as input to the selected next activity.
- When a phase is `needs-input`, preserve completed results, return the blocker to the activity that owns the missing fact, and pause only dependent progression.
- When no companion is selected, plugin-managed detailed planning is the next activity under
  `docs/domain-architecture/plans/`.
- When implementation exposes architecture drift, return to `domain-architecture-guidance` and revise affected downstream guidance.
- When implementation changes business meaning, return to `domain-modeling` before revising architecture or framework landing.

The coordinator records what changed and which downstream results must be refreshed; the process companion decides how its own artifacts and execution state are updated.
