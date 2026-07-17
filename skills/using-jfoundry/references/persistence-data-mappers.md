# Aggregate Persistence Decisions

Use this reference when a business project selects JPA or MyBatis-Plus aggregate persistence. Persistence is an outer adapter concern; domain aggregates remain free of ORM annotations, mapper types, table fields, and persistence versions.

## Choose The Adapter Shape

| Situation | Recommended shape |
|---|---|
| One MyBatis-Plus root record fully stores one aggregate | `MybatisPlusAggregateRepository` with one `AggregateData`, `DataMapper`, and `BaseMapper` |
| MyBatis-Plus root plus dependent records | Keep complete load/write synchronization in one business persistence adapter; use the base helpers only when they fit |
| One JPA-managed entity graph per aggregate | `JpaAggregateRepository` and `JpaAggregateMapper` |
| Multiple independent entity graphs, stores, or persistence technologies | A business-owned composite adapter or direct repository implementation |
| Framework base obscures project behavior | Implement the aggregate repository contract directly |

Do not override public lifecycle methods on jfoundry repository bases. Use their supported extension points, or choose a direct implementation when the operation does not fit the base.

## Mapping And Transactions

- Keep domain IDs as strong jMolecules identifiers and convert them at the adapter boundary.
- Keep persistence mapping infrastructure-local. A MapStruct mapper may be used, but aggregate restoration must remain explicit.
- Keep aggregate load, domain behavior, and modify/remove in one transaction. Do not assume detached aggregate merge support.
- A JPA aggregate is one managed entity graph. For JPA optimistic locking, put `@Version` on the graph root and ensure child-only changes participate according to the selected provider's rules.
- For MyBatis-Plus optimistic locking, put `@Version` on the root data object, configure the interceptor, and select tracked persistence only when the repository shape supports it.
- Do not expose persistence versions or `AggregatePersistenceContext` through domain or business constructors.

## Business-Owned Decisions

The project, not the persistence library, chooses full replacement, differential updates, append-only writes, child-delete ordering, audit mapping, and duplicate-key interpretation. Translate a database constraint to `ConflictException` only when it represents the intended business conflict.

Use `infrastructure-jpa-dependencies.xml` or `infrastructure-mybatis-plus-dependencies.xml` for a framework-neutral outer adapter. For Spring Boot runtime assembly, use the matching Spring Boot template. Read `references/upstream-documentation.md` for mapper signatures, helper methods, transaction integration, and provider-specific details.
