# JFoundry Implementation Guidance Result Protocol

Use this protocol for jfoundry project setup, modification, and framework-landing work. Consume confirmed Domain Modeling and Architecture Guidance results when present. Translate those decisions into jfoundry choices; never override an earlier architecture decision.

## Required Shape

Return the shared envelope followed by the sibling `JFoundry Landing` payload. Complete every envelope field, but include only applicable landing fields.

```text
Phase: JFoundry Implementation Guidance
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

JFoundry Landing:
  Java version:
  JFoundry version:
  Runtime choice:
  Project / Maven module shape:
  Primary architecture style:
  BOM / starters:
  Other optional integrations (excluding Outbox, Inbox, locks, and brokers):
  Package roles / dependency direction:
  Architecture annotations:
  Port ownership:
  Aggregate repositories / read-side ports:
  Persistence converters / data mapping:
  Exception handling / boundary mapping:
  Outbox / Inbox / lock / broker decisions:
  Templates / placeholders:
  Verification commands:
  Readiness for implementation planning:
```

The specialist owns the `JFoundry Landing` payload. Select its fields from the confirmed domain and architecture results, project evidence, and the relevant jfoundry references. Omit fields that do not apply rather than filling them speculatively. This result guides the next implementation activity; it does not claim that production code was written or verified.

## Status And Applicability

- Use `not-applicable` when the target does not use jfoundry and the user did not request it. Record why no framework landing is needed.
- Use `needs-input` only when an exact Java or jfoundry version, runtime, primary architecture, module shape, persistence choice, or messaging requirement prevents a responsible landing. Ask the smallest blocking question rather than choosing an unsafe exact value.
- Keep nonblocking uncertainty in **Open Questions** or **Handoff Notes** and return `completed` when the landing is usable for its recommended next step.
- Return to architecture guidance when a jfoundry layout or convention conflicts with the confirmed architecture. Preserve the architecture result while the conflict is resolved.

Optional integrations are absent unless the use case justifies them. Do not add distributed locks, Outbox, Inbox, broker integration, or external messaging merely to complete the payload. Use the detailed dependency, architecture, persistence, repository/port, runtime, and integration references for selection rules instead of duplicating them here.
