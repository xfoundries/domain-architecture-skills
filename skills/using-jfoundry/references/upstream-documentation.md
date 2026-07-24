# Upstream JFoundry Documentation Lookup

This skill owns project decisions. JFoundry's selected release owns Maven coordinates, APIs, configuration, supported runtime composition, and implementation details. Before writing any of those facts, consult the documentation and source that match the project's resolved JFoundry version.

## Resolve The Version First

Read `references/version-selection.md`. For an existing project, inspect its effective Maven model instead of assuming that the newest jfoundry documentation applies. Prefer the matching release tag, published artifact documentation, or source checkout over the `main` branch when they differ.

## Lookup Map

| Project concern | Upstream documentation to consult |
|---|---|
| Maven BOM and artifacts | Release dependency-management POMs and the matching runtime assembly guide |
| Aggregate repository contract and persistence boundary | Aggregate-persistence and selected persistence implementation guides |
| Reliable publication and consumption | Reliable-messaging guide and selected store/transport implementation guide |
| Spring, Quarkus, or Helidon assembly | Matching runtime implementation and configuration guide |
| Properties, auto-configuration, APIs, and Native Image behavior | Matching release documentation and source |
| Architecture annotations and ArchUnit rule semantics | Architecture-styles and architecture-rule guides |

When the exact version's documentation is unavailable, inspect the tagged source POMs and source code for that release. If neither is available, state the version mismatch and treat configuration-sensitive behavior as an open question rather than copying an example from an unrelated release.
