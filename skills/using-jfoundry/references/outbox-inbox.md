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

Read the reliable-messaging capability documentation for the selected JFoundry version before using its externalization annotations, routing APIs, or dispatch services; this skill does not duplicate those API and dispatch semantics.

## Select A Store

The Outbox/Inbox concepts are framework-neutral. Resolve the selected release's supported runtime assembly, common capability, and exactly one store implementation for each capability that is needed. Aggregate persistence does not imply Outbox or Inbox; select reliable messaging explicitly.

For a non-Spring runtime, use only the assembly documented for that runtime. Do not infer support from Spring or a framework-neutral library.

## MessageSender Rule

Outbox does not imply a broker. If production Outbox dispatch is enabled, provide a real sender through one selected broker assembly or a custom adapter.

Verify the selected runtime sender rather than only checking the dependency graph. A context or smoke test should assert that the runtime uses the intended broker adapter. Follow the selected release's broker serialization and test-startup guidance; enable listeners and automatic dispatch explicitly only in a profile that starts the broker.

## JPA Inbox Database Decision

Confirm the selected release's atomic-claim database support before choosing a JPA Inbox store. When the database product is unknown, do not select the store yet; record the database choice as `needs-input` instead of assuming a portable claim algorithm.

## Runtime And Operational Ownership

Outbox does not imply a production broker. Provide a real sender through one broker assembly or a custom adapter, and verify the selected runtime component in a context or smoke test. Use Inbox at
the consumer boundary whenever duplicate delivery or retry must be idempotent.

Verify the selected release's transaction and retry behavior for Inbox processing. Integration tests should prove the documented retry path rather than assume a failed record disappears.

Copy the supported Outbox or Inbox SQL template into the application's migration process; jfoundry
does not create business tables automatically. Read the selected jfoundry version's capability and
implementation documentation for dispatcher configuration, retries, cleanup, and store-specific
operational behavior.
