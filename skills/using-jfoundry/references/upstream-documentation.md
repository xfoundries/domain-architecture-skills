# Upstream JFoundry Documentation Lookup

This skill owns project decisions and template selection. Before writing framework API calls, configuration properties, auto-configuration overrides, SQL, or persistence helpers, consult the documentation and source that match the project's resolved jfoundry version.

## Resolve The Version First

Read `references/version-selection.md`. For an existing project, inspect its effective Maven model instead of assuming that the newest jfoundry documentation applies. Prefer the matching release tag, published artifact documentation, or source checkout over the `main` branch when they differ.

## Lookup Map

| Project concern | Upstream documentation to consult |
|---|---|
| Aggregate repository contract and persistence boundary | `docs/i18n/en/capabilities/aggregate-persistence.md` |
| JPA entity graph, `@Version`, JPA Outbox, and JPA Inbox | `docs/i18n/en/implementations/jpa.md` |
| MyBatis-Plus data mapping, locking, Outbox, and Inbox | `docs/i18n/en/implementations/mybatis-plus.md` |
| Outbox/Inbox semantics and application-owned SQL templates | `docs/i18n/en/capabilities/reliable-messaging.md` |
| Spring runtime assembly and selected implementation choices | `docs/i18n/en/implementations/spring-boot.md` |
| Starters, properties, and auto-configuration conditions | `docs/i18n/en/reference/spring-boot-autoconfiguration.md` |
| Architecture annotations and ArchUnit rule semantics | `docs/i18n/en/framework/architecture-styles.md` and `docs/i18n/en/framework/archunit-rules.md` |

When the exact version's documentation is unavailable, state the version mismatch and treat configuration-sensitive behavior as an open question rather than copying an example from an unrelated release.
