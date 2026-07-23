# Distributed Lock Guidance

Use a distributed lock only when multiple processes or application instances can concurrently act on the same resource. Before adding one, explicitly evaluate whether database transactions or row locks, uniqueness constraints, or optimistic concurrency with retry already satisfy the requirement. Also consider local synchronization and idempotency. Add a distributed lock only when those controls are insufficient.

A distributed lock coordinates application instances; it is distinct from a database row lock and does not replace domain invariants, idempotency, uniqueness constraints, or optimistic concurrency.

## API Choice

- Prefer the selected release's explicit application-layer lock API when the protected scope should be visible in orchestration code.
- Use its client contract when implementing a custom infrastructure adapter or advanced integration.
- Use a declarative lock boundary only on runtime-managed application-service methods when it is appropriate. It is runtime-specific and not mandatory.
- Resolve the selected release's lock contract artifact and configuration before compiling lock APIs. Do not assume a general application dependency includes it.
- Verify proxy and self-invocation constraints in the selected runtime guide; use the explicit API when interception cannot be guaranteed.

Keep lock keys resource-specific and stable, so unrelated resources do not serialize. Keep the protected block no broader than the application workflow requires.

## Redisson Adapter

When a Spring Boot runtime selects Redisson as the distributed-lock adapter, resolve the selected release's supported runtime assembly from its BOM and implementation guide. Do not add a dependency version or add the optional adapter by default.

Verify lock and transaction ordering in the selected release before combining declarative lock and transaction boundaries. Avoid opening a database transaction while waiting for a lock when the documented advisor ordering supports that arrangement.

Advisor ordering does not guarantee mutual exclusion for the transaction's full lifetime. Verify lease and backend lifetime behavior for the selected adapter. A finite lease can expire before the protected transaction completes, so set it longer than the worst-case protected duration with operational margin or avoid a finite lease when that bound is not reliable.

## Acquisition Failure

Verify the selected release's acquisition-failure behavior. Use a skip result only on void or explicitly nullable boundaries where it is unambiguous; never overload a primitive or non-null return with skipped acquisition.

When the caller requires an explicit outcome, map lock contention to an application-level result at the application boundary.
