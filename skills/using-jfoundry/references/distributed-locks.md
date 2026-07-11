# Distributed Lock Guidance

Use a distributed lock only when multiple processes or application instances can concurrently act on the same resource. Before adding one, explicitly evaluate whether database transactions or row locks, uniqueness constraints, or optimistic concurrency with retry already satisfy the requirement. Also consider local synchronization and idempotency. Add a distributed lock only when those controls are insufficient.

A distributed lock coordinates application instances; it is distinct from a database row lock and does not replace domain invariants, idempotency, uniqueness constraints, or optimistic concurrency.

## API Choice

- Use `LockTemplate` in the application layer when a use case needs an explicit locked block and the lock scope should be visible in orchestration code.
- Use `DistributedLockClient` when implementing a custom infrastructure adapter or advanced lock integration behind the application-facing mechanism.
- Use `@DistributedLock` only on Spring-managed application-service methods when a declarative whole-method boundary is appropriate. It is Spring-specific and is not mandatory.
- `jfoundry.lock.annotation.enabled` defaults to `true`; set it to `false` to disable the distributed-lock annotation advisor.
- Add the BOM-managed `jfoundry-lock-core` dependency to every application module that compiles `LockTemplate`, `LockOptions`, `DistributedLockClient`, or `@DistributedLock`; copy `assets/templates/maven/lock-core-dependencies.xml`. `jfoundry-application-starter` does not include lock-core.
- `@DistributedLock` applies only when a call enters a Spring-managed instance through its proxy. Self-invocation and unproxied instances bypass the advisor; use `LockTemplate` when proxy interception cannot be guaranteed.

Keep lock keys resource-specific and stable, so unrelated resources do not serialize. Keep the protected block no broader than the application workflow requires.

## Redisson Adapter

When a Spring Boot runtime selects Redisson as the distributed-lock adapter, import `jfoundry-spring-dependencies` and copy `assets/templates/maven/lock-redisson-dependencies.xml` into the runtime assembly module. The BOM manages `jfoundry-lock-redisson-spring-boot-starter`; do not add a dependency version and do not add this optional starter to the default Spring Boot template.

For the Redisson Spring advisor composition, the distributed-lock advisor wraps the transaction advisor, so lock acquisition occurs before transaction start. Preserve this ordering when combining `@DistributedLock` with `@ApplicationTransactional`; it avoids opening a database transaction while waiting for the lock.

Advisor ordering does not guarantee mutual exclusion for the transaction's full lifetime. An empty lease delegates lifetime management to the backend default/watchdog. A finite lease can expire before the protected transaction completes, so set it longer than the worst-case protected duration with operational margin or avoid a finite lease when that bound is not reliable.

## Acquisition Failure

The default acquisition-failure behavior throws. With `LockFailureMode.SKIP`, `LockTemplate` returns `null` and the annotation propagates that result. Use annotation-based `SKIP` only on `void` or explicitly nullable boundaries where `null` unambiguously means skipped; never use it on primitive or non-null return methods.

When the caller requires an explicit outcome, keep the default throwing mode and map lock contention to an application-level result at the application boundary.
