# Quarkus Runtime Assembly

Use this reference only when a downstream project explicitly selects Quarkus. Quarkus uses explicit
runtime extension composition, not Spring Boot starters. Add runtime dependencies in the runtime
assembly module/package; keep domain and application code independent of CDI, Jakarta REST,
Hibernate, broker-client, and Quarkus APIs.

## Base Assembly

Import `jfoundry-quarkus-dependencies` and add `jfoundry-quarkus-runtime`. Quarkus discovers
the paired deployment artifact from the extension descriptor, so applications must not add any
`*-deployment` artifact directly.

The base extension exposes `TransactionRunner` as a CDI bean and supplies application-service
domain-event orchestration. It supports the six jfoundry transaction propagation modes through
Jakarta Transactions. `TransactionOptions.name` and `readOnly` are not portable JTA settings and
are rejected rather than ignored.

## Capability Composition

| Need | Add | Scope |
|---|---|---|
| Aggregate persistence with JPA | `jfoundry-persistence-jpa`, `jfoundry-persistence-jpa-quarkus-runtime`, `quarkus-hibernate-orm`, and the application-selected Quarkus JDBC extension | Business aggregate persistence and Hibernate failure translation. |
| REST problem responses | `jfoundry-web-quarkus-runtime` | Shared RFC 9457 mapping for jfoundry and supported Jakarta REST failures. |
| Local CDI domain-event observers | `jfoundry-quarkus-runtime` | Observers fire after successful JTA commit and do not fire on rollback. |
| JPA Outbox store | `jfoundry-outbox-jpa-quarkus-runtime` | Store only; applications own the table migration. |
| Outbox dispatch and maintenance | `jfoundry-outbox-quarkus-runtime` | Add an Outbox store and real `MessageSender` separately. |
| JPA Inbox store | `jfoundry-inbox-jpa-quarkus-runtime` | PostgreSQL and MySQL have built-in claim strategies. |
| Kafka or RabbitMQ delivery | `jfoundry-messaging-kafka-quarkus-runtime` or `jfoundry-messaging-rabbitmq-quarkus-runtime` | Explicit transport selection. |

Automatic Outbox externalization is disabled by default. Enable it only when an explicitly
externalized domain event is a stable integration contract. Quarkus does not register a fallback
`MessageSender`; delivery requires a selected broker extension or an application implementation.

## Current Limits

Do not reuse Spring Boot starters in a Quarkus project. MyBatis-Plus aggregate persistence,
RocketMQ delivery, Redisson distributed locking, and JobRunr assembly do not currently have
supported jfoundry Quarkus runtime integrations. A framework-neutral library's presence does not
make it a supported Quarkus composition.

Use the selected jfoundry release's Quarkus implementation guide for exact property names, broker
configuration, REST behavior, and Native Image verification commands.
