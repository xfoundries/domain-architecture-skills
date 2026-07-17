# Reliable Messaging Decision Guidance

## When To Use Outbox

Use Outbox only when domain events must be reliably published outside the process:

- Kafka, RabbitMQ, RocketMQ, or another broker.
- Cross-service notification.
- Eventually consistent integration.
- Failure retry and dead-letter handling.

If events only need in-process local listeners, use local event dispatch and do not configure Outbox.

## Keep The External Contract At The Boundary

External publication is a business integration decision, not an automatic consequence of recording a
domain event. Externalize a domain event directly only when it is deliberately a stable public
contract. Otherwise translate it at the application boundary into a separately versioned integration
contract. Keep wire payloads independent of Java implementation types.

Read the reliable-messaging capability documentation for the selected jfoundry version before using
`@Externalized`, `@MessageRouting`, or `OutboxTemplate`; this skill does not duplicate those API and
dispatch semantics.

## Select A Store

The Outbox/Inbox concepts are framework-neutral, but the runtime/starters documented here are currently Spring Boot-specific. For Quarkus, Micronaut, Helidon, CLI, or custom runtimes, do not copy these starter snippets unless jfoundry provides a matching runtime adapter.

For Spring Boot, select the common capability starter and exactly one store implementation for each
capability that is needed:

| Capability | MyBatis-Plus | JPA |
|---|---|---|
| Outbox | `jfoundry-outbox-spring-boot-starter` + `jfoundry-outbox-mybatis-plus-spring-boot-starter` | `jfoundry-outbox-spring-boot-starter` + `jfoundry-outbox-jpa-spring-boot-starter` |
| Inbox | `jfoundry-inbox-spring-boot-starter` + `jfoundry-inbox-mybatis-plus-spring-boot-starter` | `jfoundry-inbox-spring-boot-starter` + `jfoundry-inbox-jpa-spring-boot-starter` |

The persistence starter for aggregate repositories (`jfoundry-mybatis-plus-spring-boot-starter` or
`jfoundry-jpa-spring-boot-starter`) does not add Outbox or Inbox. Select the messaging capability
explicitly. The specialized store starters already include their corresponding common starter, so
projects may use the store template alone when it is the only required snippet.

For a non-Spring runtime, assemble the framework-neutral contracts and adapters manually; do not copy
Spring Boot templates unless jfoundry provides an explicit runtime adapter.

## MessageSender Rule

Outbox does not imply a broker. The default logging sender is not a production publisher. If production Outbox dispatch is enabled, provide a real `MessageSender` through one broker starter or a custom adapter.

For Spring Boot broker starters, verify the selected runtime bean rather than only checking the
dependency graph. A context or smoke test should assert that `MessageSender` is the expected Kafka,
RabbitMQ, or RocketMQ adapter and not the logging fallback. For Kafka, configure String key/value
serializers because the jfoundry sender publishes the Outbox key and JSON body as strings. Keep
listeners and automatic Outbox dispatch disabled by default in tests or partial local startup, then
enable both explicitly in the integration profile that starts the broker.

## JPA Inbox Database Decision

JPA Inbox has built-in atomic claim support for PostgreSQL and MySQL. For another database product,
the project must provide a `JpaInboxClaimStrategy`. When the database product is unknown, do not
select the JPA Inbox store yet; record the database choice as `needs-input` instead of assuming a
portable claim algorithm.

## Runtime And Operational Ownership

Outbox does not imply a production broker. Provide a real `MessageSender` through one broker starter
or a custom adapter, and verify the selected runtime bean in a context or smoke test. Use Inbox at
the consumer boundary whenever duplicate delivery or retry must be idempotent.

With Spring transaction integration, `InboxTemplate` claims, processes, and records a handler failure
in separate transactions. A failed handler leaves a durable `FAILED` Inbox row for a later retry;
integration tests should verify that retry path rather than expect the row to disappear.

Copy the supported Outbox or Inbox SQL template into the application's migration process; jfoundry
does not create business tables automatically. Read the selected jfoundry version's capability and
implementation documentation for dispatcher configuration, retries, cleanup, and store-specific
operational behavior.
