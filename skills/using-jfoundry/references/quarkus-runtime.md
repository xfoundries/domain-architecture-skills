# Quarkus Runtime Assembly

Use this reference only when a downstream project explicitly selects Quarkus. Quarkus uses explicit
runtime extension composition, not Spring Boot starters. Add runtime dependencies in the runtime
assembly module/package; keep domain and application code independent of CDI, Jakarta REST,
Hibernate, broker-client, and Quarkus APIs.

## Version-Aware Assembly

Resolve the selected release's Quarkus BOM and extension artifacts from its Quarkus implementation guide. Quarkus extensions may include paired build-time artifacts; follow that release's assembly instructions rather than adding implementation artifacts by name from memory.

Use the release documentation to confirm every selected capability, including transaction integration, local event dispatch, persistence, web exception mapping, Outbox, Inbox, delivery transport, and Native Image support. A capability documented for Spring or a framework-neutral adapter is not evidence that it is available in Quarkus.

Automatic externalization and delivery are separate choices. Enable externalization only for a stable integration contract, and select a documented store and sender implementation before expecting delivery behavior.

Never reuse Spring Boot starters in a Quarkus project. If the selected release does not document a requested composition, report it as unsupported rather than assembling it from transitive dependencies.

Use the selected release's Quarkus guide for exact property names, Jakarta Transaction behavior, REST mapping, broker configuration, and Native Image verification commands.
