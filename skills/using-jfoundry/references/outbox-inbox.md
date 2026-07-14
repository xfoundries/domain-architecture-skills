# Outbox And Inbox Guidance

## When To Use Outbox

Use Outbox only when domain events must be reliably published outside the process:

- Kafka, RabbitMQ, RocketMQ, or another broker.
- Cross-service notification.
- Eventually consistent integration.
- Failure retry and dead-letter handling.

If events only need in-process local listeners, use local event dispatch and do not configure Outbox.

## Event Flow

The intended flow is:

```text
@ApplicationService
  -> DomainEventContext
  -> DomainEventDispatcher
  -> DomainEventOutboxRecorder
  -> OutboxMessageStore
  -> jfoundry_outbox_event
  -> OutboxDispatcher
  -> MessageSender
  -> broker / external system
```

Mark only externally published events with `@Externalized`. Use `@MessageRouting` when topic or routing key must be explicit. `@MessageRouting` alone does not make an event externalized.

## Choose The External Contract

Use direct domain-event externalization only when that event is deliberately a stable public contract:

```java
@Externalized("orders.events.v1")
@MessageRouting(topic = "orders.events.v1", key = "orderId")
public final class OrderPlaced extends AbstractDomainEvent {
    // Stable public event fields.
}
```

When the internal domain event and public contract need different ownership or evolution, keep the domain event internal. Translate it at the application boundary and append the versioned integration event explicitly:

```java
OrderPlacedV1 integrationEvent = translator.translate(domainEvent);
outboxTemplate.append(new OutboxAppendRequest(
        eventId,
        "orders.events.v1",
        orderId,
        "OrderPlacedV1",
        integrationEvent,
        occurredAt,
        "Order",
        orderId,
        aggregateVersion));
```

`OutboxTemplate` uses the configured `PayloadSerializer` and `OutboxMessageStore` in the caller's transaction. It does not start a transaction, publish synchronously, or decide how a business event maps to an integration contract. Keep that translation in the business project, and do not make the domain model depend on an `integration-contracts` module.

Keep the wire payload independent of Java implementation types. The default Jackson serializer
writes ordinary JSON without Jackson default-typing metadata or Java class names. Use the envelope's
event type/version and the Outbox `payloadType` as stable contract identifiers; each consumer should
deserialize into its own versioned contract. Do not rely on `@class`, collection implementation
names, or values such as `java.math.BigDecimal` being embedded in JSON.

## Starter Selection

The Outbox/Inbox concepts are framework-neutral, but the runtime/starters documented here are currently Spring Boot-specific. For Quarkus, Micronaut, Helidon, CLI, or custom runtimes, do not copy these starter snippets unless jfoundry provides a matching runtime adapter.

For Outbox with MyBatis-Plus storage:

- Add `jfoundry-outbox-spring-boot-starter`.
- Add `jfoundry-outbox-mybatis-plus-spring-boot-starter`.
- Add one real broker starter, such as `jfoundry-messaging-kafka-spring-boot-starter`, when production dispatch is enabled.

The Outbox starter auto-configures `OutboxTemplate` when `OutboxMessageStore` and `PayloadSerializer` beans are available. Non-Spring runtimes can construct the framework-neutral template directly.

The messaging starter transitively supplies Spring Boot's JSON starter, so a non-web consumer or
batch application receives the default Jackson `ObjectMapper` and `PayloadSerializer` without
adding a WebMVC or WebFlux starter. Do not add a web runtime only to make Outbox serialization
work. A project-defined `ObjectMapper` or `PayloadSerializer` still takes precedence.

For JobRunr dispatching, add `jfoundry-outbox-jobrunr-spring-boot-starter` and set dispatcher mode accordingly.

## MessageSender Rule

Outbox does not imply a broker. The default logging sender is not a production publisher. If production Outbox dispatch is enabled, provide a real `MessageSender` through one broker starter or a custom adapter.

For Spring Boot broker starters, verify the selected runtime bean rather than only checking the
dependency graph. A context or smoke test should assert that `MessageSender` is the expected Kafka,
RabbitMQ, or RocketMQ adapter and not the logging fallback. For Kafka, configure String key/value
serializers because the jfoundry sender publishes the Outbox key and JSON body as strings. Keep
listeners and automatic Outbox dispatch disabled by default in tests or partial local startup, then
enable both explicitly in the integration profile that starts the broker.

## Inbox

Use Inbox when a consumer must be idempotent under duplicate delivery or retry:

```java
inboxTemplate.executeOnce(eventId, "order-projection", () -> {
    handler.handle(event);
});
```

For Spring Boot with MyBatis-Plus storage, add `jfoundry-inbox-spring-boot-starter` and `jfoundry-inbox-mybatis-plus-spring-boot-starter`.

## Operational Notes

- Outbox messages move through `PENDING`, `DISPATCHING`, `PUBLISHED`, `FAILED`, and `DEAD_LETTERED`.
- Dispatchers use atomic claim to avoid duplicate dispatch across instances.
- Recovery moves stuck `DISPATCHING` messages back to retryable state.
- Cleanup should only remove terminal records after the configured retention period.
- Consumers still need idempotency because brokers and dispatchers can retry.
