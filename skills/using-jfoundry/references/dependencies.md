# Dependency Guidance

This skill selects capabilities; the selected JFoundry release selects coordinates.

## Resolve Before Writing Maven

1. Read `version-selection.md` and determine the project's exact JFoundry version.
2. Resolve the matching release tag, published documentation, or source checkout. Do not use the `main` branch for an older release.
3. Consult that release's dependency-management POM and runtime implementation guide.
4. Add only the BOM and artifacts documented for the selected runtime and capabilities. Let the BOM manage their versions.
5. Run Maven dependency resolution before generating application code. If an artifact is absent or unmanaged, stop and report the version/capability mismatch.

For a new project, select the version before creating its first POM. For an existing project, inspect the effective POM and preserve its resolved version.

## Capability Decisions

Choose dependencies from the project decisions, not from a catalog:

| Decision | Dependency consequence |
|---|---|
| Architecture tests are required | Add the selected release's architecture-test artifact in test scope. |
| Aggregate persistence is required | Select one supported persistence adapter and its selected runtime assembly. |
| HTTP delivery is required | Select the runtime's supported web assembly and keep HTTP translation in the primary adapter. |
| External event delivery must be reliable | Select Outbox, then separately select one supported store and delivery transport. |
| Consumer idempotency is required | Select Inbox, then separately select one supported store. |
| Cross-instance coordination is required | Select a supported lock adapter only after deciding that database constraints, idempotency, or local synchronization are insufficient. |
| A runtime is selected | Use only that runtime's documented assembly; never copy a different runtime's dependencies. |

Keep domain code free of runtime, persistence, broker, HTTP, and client-SDK dependencies. Keep application code free of runtime assembly and broker-client dependencies. Put selected persistence, messaging, and web dependencies in outer adapters or runtime assembly.

## Verification

- Do not add optional reliability, broker, or lock capabilities by default.
- Do not infer support in one runtime from a capability available in another runtime.
- Do not depend directly on low-level implementation artifacts from business code unless the selected release documents that advanced assembly.
- Record the resolved release, the authoritative source used, and every selected capability in the JFoundry Implementation Guidance Result.
