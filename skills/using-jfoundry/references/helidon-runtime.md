# Helidon MP Runtime Guidance

Use this reference only after the project has explicitly selected Helidon MP and its JFoundry release.
Read that release's Helidon implementation guide and BOM before adding artifacts; capability names,
Helidon versions, and application configuration are versioned facts.

## Selection Rules

- Treat Helidon as a peer runtime integration, not as Spring Boot or Quarkus with renamed dependencies.
- Select the base runtime only for CDI transactions and local domain events. Add JPA, web, Outbox,
  Outbox store, and Inbox store capabilities separately when the project needs them.
- Keep Helidon, CDI, Jakarta Transactions, JAX-RS, and persistence APIs in application-owned outer
  adapters or runtime assembly. Domain and application code continue to depend on JFoundry contracts.
- Supply database migrations, datasource and persistence-unit configuration, and any broker client in
  the application. A JFoundry runtime module does not make these decisions implicitly.
- When a selected release provides Helidon Outbox externalization defaults as enabled CDI
  alternatives, replace one using an enabled application `@Alternative` with a higher priority; a
  plain CDI bean does not override an enabled alternative.
- Do not reuse Spring or Quarkus messaging, locking, or job adapters in a Helidon project. Confirm a
  documented Helidon capability for the selected release, or record an application-owned adapter as
  a deliberate project decision.

## Native Image

Use the selected release's documented GraalVM and Maven command. Native support is capability-specific:
record separately whether the release verifies startup, HTTP behavior, persistence, and transaction
execution. Do not claim Native transaction support from a successful binary build or web smoke test.
If the selected Helidon release documents JTA Native support as experimental or the consumer fails at
transaction execution, preserve JVM JTA guidance and mark Native JTA as unresolved rather than
copying a transaction-manager implementation into the business application.
