# Testing Guidance

## Architecture Tests

Add architecture tests when creating the project skeleton. Do not wait until after application code has grown.

For new Hexagonal projects, copy `assets/templates/java/HexagonalArchitectureTest.java` and replace `PACKAGE_NAME`.

For Onion Simple projects, copy `assets/templates/java/OnionSimpleArchitectureTest.java` and replace `PACKAGE_NAME`.

Use:

```java
@ArchTest
static final ArchTests jfoundryRules = JFoundryRules.hexagonalStrict();

@ArchTest
static final ArchRule adapterPackages = JFoundryRules.hexagonalAdapterPackageConvention(
        HexagonalAdapterPackageConvention.IN_OUT);

@ArchTest
static final ArchTests jmoleculesDddRules = JFoundryRules.jmoleculesDdd();

@ArchTest
static final ArchTests aggregateRepositoryRules = JFoundryRules.aggregateRepositoryConventions();
```

Choose `IN_OUT` for `adapter.in` / `adapter.out`, or `PRIMARY_SECONDARY` for
`adapter.primary` / `adapter.secondary`. This is a selected Hexagonal project convention and does
not apply to Onion projects.

For Onion:

```java
@ArchTest
static final ArchTests jfoundryRules = JFoundryRules.onionSimple();
```

Primary architecture entrypoints fail when the analyzed scope contains no matching architecture
annotation. Declare the selected Hexagonal roles or Onion rings before relying on the dependency
rules; otherwise the test is intentionally rejected instead of passing as an empty no-op. This
guard requires at least one selected-style marker. A complete application should still express all
roles or rings justified by its chosen structure, while a deliberately partial analysis scope does
not need to manufacture absent roles.

These entrypoints return ArchUnit `ArchTests`. Do not declare `@ArchTest ArchRule[]` fields;
ArchUnit's JUnit 5 engine does not treat an array as a rule collection.

## Optional Rules

Add these only when the project uses the corresponding style:

- `JFoundryRules.cqrs()` when using jfoundry CQRS annotations.

## Migration Exceptions

New jfoundry projects should keep `JFoundryRules.aggregateRepositoryConventions()` enabled from the initial skeleton. Migration projects may temporarily disable or baseline this rule only when existing repositories still leak wrappers, read models, pages, or persistence APIs. Record the exception and re-enable the rule after repository/read-port cleanup.

## Test Scope

Use `jfoundry-architecture-test` with `test` scope. Keep architecture tests in the module or test aggregation that can see the packages being checked.

## Verification

For a single-module project, run:

```bash
mvn test
```

For a multi-module Maven project, run the narrow module first:

```bash
mvn -pl <module> test
```

Use `-am` when dependencies in the same reactor must be built:

```bash
mvn -pl <module> -am test
```
