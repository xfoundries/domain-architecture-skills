# Persistence Data Mappers

Use this reference when a business project implements jfoundry aggregate repositories with `AbstractAggregateRepository`, MyBatis-Plus, Jakarta Persistence, `AggregateData`, `DataMapper`, or MapStruct.

## Choose The Repository Implementation Shape

The domain aggregate repository contract does not require a jfoundry persistence base class. Choose
one of these implementation shapes from the aggregate's actual persistence needs:

- Implement the repository contract directly when project-local code is clearer and jfoundry's
  lifecycle/event-registration support is not needed.
- Extend `MybatisPlusAggregateRepository<T, ID, D, K>` directly when one `AggregateData<K>`, one
  `DataMapper`, and one `BaseMapper` fully store and restore the aggregate.
- Extend the same MyBatis-Plus base and override complete `do*` operations when one root Data anchors
  a multi-table aggregate and its protected complete-operation helpers fit.
- Extend `AbstractAggregateRepository<T, ID>` when no single MyBatis-Plus root Data exists or a
  complete aggregate operation coordinates other stores or persistence representations.

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
- Do not make data mappers Spring Beans by default. Prefer MapStruct's default component model and expose `INSTANCE = Mappers.getMapper(...)`.

## Data Mapper Template

```java
@Mapper
public interface OrderDataMapper
        extends DataMapper<Order, OrderId, OrderData, String> {

    OrderDataMapper INSTANCE = Mappers.getMapper(OrderDataMapper.class);

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

Repository adapters should hold a static data mapper reference:

```java
private static final OrderDataMapper DATA_MAPPER = OrderDataMapper.INSTANCE;

public final class OrderRepositoryImpl
        extends MybatisPlusAggregateRepository<Order, OrderId, OrderData, String>
        implements OrderRepository {

    public OrderRepositoryImpl(OrderMapper mapper) {
        super(mapper, DATA_MAPPER);
    }
}
```

For Spring Boot applications, jfoundry Spring auto-configuration injects `DomainEventContext` into `AbstractAggregateRepository` internally. Business repository constructors should not expose `DomainEventContext`. For non-Spring or manual assembly, call `setDomainEventContext(...)` after constructing the repository.

## Multi-Table And Composite Persistence

An aggregate may be stored through a root record plus dependent records without turning those
records into separate aggregates. Keep the complete operation in one business infrastructure
adapter.

When the aggregate has one MyBatis-Plus root Data, keep root mapping local and pass its two
functions to the base. The root Data alone does not need to implement reverse aggregate mapping.

**Sketch:**

```java
public final class OrderRepositoryImpl
        extends MybatisPlusAggregateRepository<Order, OrderId, OrderData, String>
        implements OrderRepository {

    private static final OrderRootDataMapper ROOT_DATA_MAPPER = new OrderRootDataMapper();

    OrderRepositoryImpl(OrderMapper orderMapper, OrderLineMapper lineMapper) {
        super(orderMapper,
                ROOT_DATA_MAPPER::toData,
                ROOT_DATA_MAPPER::toDataId,
                OrderData.class);
        this.lineMapper = lineMapper;
    }

    @Override
    protected Order doFindById(OrderId id) {
        return loadAggregate(id, root -> restoreOrder(
                root, lineMapper.selectByOrderId(root.getId())));
    }

    @Override
    protected void doAdd(Order order) {
        insertAggregate(order, ROOT_DATA_MAPPER.toData(order), root -> insertLines(order));
    }

    @Override
    protected void doModify(Order order) {
        updateAggregate(order, ROOT_DATA_MAPPER.toData(order), root -> synchronizeLines(order));
    }

}
```

The default `doRemove` uses root-first deletion and therefore fits database cascades. The
`deleteAggregate` callback runs only after the root conflict check and deletion; use it for
post-root cleanup, not for child rows whose foreign keys require child-first deletion. When the
project requires explicit child-first removal, implement a project-specific atomic delete strategy
instead of forcing this helper.

The plugin must not choose full replacement, differential updates, or append-only writes merely
because an aggregate spans multiple tables. Derive that algorithm from whether members are
mutable, externally referenced, audited, ordered, large, or concurrently modified. If those facts
affect correctness and are unknown, keep them as an open question or return `needs-input`; if they
only affect low-risk optimization, record the selected assumption.

## Persistence-owned Optimistic Locking

Do not add a database version to a domain aggregate merely because MyBatis-Plus or JPA uses
optimistic locking. `AggregatePersistenceContext` tracks persistence state by aggregate object
identity for one runtime-managed transaction. Spring supplies a transaction-bound implementation;
business code does not manage a scope. A tracked operation without an active transaction, or a
modify/remove of an aggregate not loaded in the current transaction, fails fast. Detached aggregate
merge is not supported.

For MyBatis-Plus root Data:

- annotate the Data version with `@Version` and configure `OptimisticLockerInnerInterceptor`;
- pass the Data class to `MybatisPlusAggregateRepository` to enable metadata detection;
- let the base track loaded versions, restore them before `updateById`, translate zero-row conflicts,
  advance state after complete success, and delete with ID plus loaded version;
- do not inject `AggregatePersistenceContext`, expose a version accessor, or manually read/write the
  version in the business repository;
- omit the Data class when the adapter intentionally does not use `@Version` tracking.

For one JPA entity graph, `JpaAggregateRepository` and `JpaAggregateMapper` may be used. The mapper
creates new entities, restores aggregates, converts IDs, and applies aggregate state to the managed
entity loaded by `EntityManager.find`. The repository flushes add/modify/remove and translates
`OptimisticLockException` to `ConflictException`; it does not call `merge`. Runtime integration
injects the persistence context rather than business constructors. Multiple independent
entities, repositories, or persistence technologies still require a business-owned composite
adapter.

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
- Do not reintroduce persistence audit base classes into the domain model just to reduce mapper code.

## Maven Notes

jfoundry BOMs manage MapStruct versions, but business projects still own compiler annotation processor configuration. Put MapStruct and Lombok processors in the module that compiles the mapper, usually infrastructure.

Use `-Amapstruct.unmappedTargetPolicy=ERROR` when the project is ready to fail fast on unmapped data fields.

Use `infrastructure-jpa-dependencies.xml` for a framework-neutral JPA infrastructure module, or
`spring-boot-jpa-dependencies.xml` in a Spring Boot runtime assembly. Both capabilities are optional.
