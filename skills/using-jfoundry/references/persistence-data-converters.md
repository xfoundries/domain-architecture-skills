# Persistence Data Converters

Use this reference when a business project implements jfoundry aggregate repositories with `AbstractAggregateRepository`, `MybatisPlusAggregateRepository`, `AggregateData`, `DataConverter`, MyBatis-Plus data objects, or MapStruct.

## Choose The Repository Implementation Shape

The domain aggregate repository contract does not require a jfoundry persistence base class. Choose
one of these implementation shapes from the aggregate's actual persistence needs:

- Implement the repository contract directly when project-local code is clearer and jfoundry's
  lifecycle/event-registration support is not needed.
- Extend `AbstractAggregateRepository<T, ID>` when one complete aggregate operation coordinates
  multiple tables, mappers, stores, or persistence representations and the adapter should reuse
  jfoundry's validation, batch, and event-registration lifecycle.
- Extend `MybatisPlusAggregateRepository<T, ID, D, K>` only when one `AggregateData<K>` and one
  `BaseMapper` can fully store and restore the aggregate.

`AbstractAggregateRepository` exposes protected `doFindById(...)`, `doAdd(...)`, `doModify(...)`,
and `doRemove(...)` extension points. Treat its public lifecycle methods as framework-managed entry
points and customize persistence through the protected `do*` methods. They remain non-final so
runtime frameworks can apply class-based proxies. A composite adapter must report absence,
optimistic-lock conflicts, and persistence failures from its `do*` operation before returning
successfully.

## Single-Data Default Pattern

- Keep domain aggregates free of MyBatis, JPA, Spring, table annotations, type handlers, logical delete fields, and persistence data objects.
- Put table mapping, type handlers, fill strategies, nullable update strategies, and logical delete fields on the infrastructure data object.
- Let the data object extend `AggregateData<K>`, where `K` is a persistence-native ID type such as `String`, `Long`, or `UUID`.
- Keep the domain aggregate ID as a strong jMolecules `Identifier`.
- Convert the domain ID to the data ID in `toDataId(...)`.
- Prefer MapStruct for `toData(...)`.
- Keep `toEntity(...)` explicit and call the aggregate's `restore(...)` factory.
- Do not make converters Spring Beans by default. Prefer MapStruct's default component model and expose `INSTANCE = Mappers.getMapper(...)`.

## Converter Template

```java
@Mapper
public interface OrderDataConverter
        extends DataConverter<Order, OrderId, OrderData, String> {

    OrderDataConverter INSTANCE = Mappers.getMapper(OrderDataConverter.class);

    @Override
    @Mapping(target = "id", expression = "java(toDataId(entity.getId()))")
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deleterId", ignore = true)
    @Mapping(target = "deleterName", ignore = true)
    @Mapping(target = "deletedTime", ignore = true)
    OrderData toData(Order entity);

    @Override
    default Order toEntity(OrderData data) {
        if (data == null) {
            return null;
        }
        return Order.restore(
                OrderId.of(data.getId()),
                data.getStatus(),
                data.getAmount());
    }

    @Override
    default String toDataId(OrderId id) {
        return id == null ? null : id.value();
    }
}
```

Repository adapters should hold a static converter reference:

```java
private static final OrderDataConverter CONVERTER = OrderDataConverter.INSTANCE;

public final class OrderRepositoryImpl
        extends MybatisPlusAggregateRepository<Order, OrderId, OrderData, String>
        implements OrderRepository {

    public OrderRepositoryImpl(OrderMapper mapper) {
        super(mapper, CONVERTER);
    }
}
```

For Spring Boot applications, jfoundry Spring auto-configuration injects `DomainEventContext` into `AbstractAggregateRepository` internally. Business repository constructors should not expose `DomainEventContext`. For non-Spring or manual assembly, call `setDomainEventContext(...)` after constructing the repository.

## Multi-Table And Composite Persistence

An aggregate may be stored through a root record plus dependent records without turning those
records into separate aggregates. Keep the complete operation in one business infrastructure
adapter.

**Sketch:**

```java
public final class OrderRepositoryImpl
        extends AbstractAggregateRepository<Order, OrderId>
        implements OrderRepository {

    @Override
    protected Order doFindById(OrderId id) {
        // Load every persistence record required to restore one complete Order.
    }

    @Override
    protected void doAdd(Order order) {
        // Insert the complete aggregate before returning.
    }

    @Override
    protected void doModify(Order order) {
        // Check the root update result before synchronizing dependent records.
    }

    @Override
    protected void doRemove(Order order) {
        // Apply the project-selected cascade or explicit removal policy.
    }
}
```

The plugin must not choose full replacement, differential updates, or append-only writes merely
because an aggregate spans multiple tables. Derive that algorithm from whether members are
mutable, externally referenced, audited, ordered, large, or concurrently modified. If those facts
affect correctness and are unknown, keep them as an open question or return `needs-input`; if they
only affect low-risk optimization, record the selected assumption.

## Failure Translation

`AbstractAggregateRepository` applies the runtime-neutral `PersistenceFailureTranslator` around
its protected `do*` calls and defaults to preserving the original exception. In Spring Boot,
`jfoundry-persistence-spring` supplies a default translator for known availability and timeout
failures. It must not turn every Spring data-access failure into `ExternalAccessException`.

Keep business interpretation in the business adapter. For example, catch a duplicate-key failure
around the aggregate root insert and translate it to `ConflictException` only when that constraint
means the aggregate identifier already exists. Let unrelated child-table constraints, SQL defects,
mapper defects, locks, and unknown failures retain their original meaning. Read
`references/exception-handling.md` for the complete boundary rules.

## Wrapper Or Explicit SQL

- Prefer MyBatis-Plus Lambda Wrappers for ordinary single-table predicates, ordering, updates, and
  deletes when the Java API remains clear.
- Keep explicit SQL when one atomic statement or database-specific behavior is part of correctness,
  such as a compare-and-set optimistic-lock update.
- Do not infer a multi-table synchronization strategy from the persistence library. Full
  replacement, differential updates, append-only writes, and cascades remain project decisions.

## Audit Fields

Do not blindly ignore all audit fields.

- Ignore logical-delete fields and pure persistence fill fields when the domain aggregate does not own them.
- Map audit fields when the domain model intentionally carries an audit snapshot through `AuditableAggregateRoot` or `AuditableEntity`.
- Do not reintroduce persistence audit base classes into the domain model just to reduce converter code.

## Maven Notes

jfoundry BOMs manage MapStruct versions, but business projects still own compiler annotation processor configuration. Put MapStruct and Lombok processors in the module that compiles the converter, usually infrastructure.

Use `-Amapstruct.unmappedTargetPolicy=ERROR` when the project is ready to fail fast on unmapped data fields.
