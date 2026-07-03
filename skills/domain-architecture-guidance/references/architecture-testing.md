# Architecture Testing Examples

Architecture tests validate rules chosen by the project. They do not define the architecture model.

Use tests for boundaries that matter and are likely to erode:

- domain should not depend on web, persistence, or framework packages
- application should not depend on concrete infrastructure adapters
- adapters should depend on ports/application contracts
- bounded contexts or modules should not form cycles
- jMolecules annotations should align with package or project structure

## Java / Kotlin with ArchUnit

ArchUnit official resources:

- https://www.archunit.org/
- https://github.com/TNG/ArchUnit

JUnit 5 sketch:

```java
package com.example.architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.example")
class ArchitectureTest {

    @ArchTest
    static final ArchRule domain_should_not_depend_on_infrastructure =
        noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage(
                "..infrastructure..",
                "..web..",
                "org.springframework..",
                "jakarta.persistence.."
            );

    @ArchTest
    static final ArchRule application_should_not_depend_on_adapters =
        noClasses()
            .that().resideInAPackage("..application..")
            .should().dependOnClassesThat().resideInAnyPackage(
                "..adapter..",
                "..infrastructure.."
            );
}
```

Annotation-oriented sketch:

```java
@ArchTest
static final ArchRule domain_layer_should_not_access_infrastructure =
    noClasses()
        .that().areAnnotatedWith(org.jmolecules.architecture.layered.DomainLayer.class)
        .should().dependOnClassesThat()
        .areAnnotatedWith(org.jmolecules.architecture.layered.InfrastructureLayer.class);
```

Prefer package rules when annotations are incomplete. Prefer annotation rules when jMolecules use is systematic.

## C# / .NET with ArchUnitNET

ArchUnitNET resources:

- https://github.com/TNG/ArchUnitNET
- https://archunitnet.readthedocs.io/

xUnit sketch:

```csharp
using ArchUnitNET.Domain;
using ArchUnitNET.Loader;
using ArchUnitNET.Fluent;
using Xunit;

using static ArchUnitNET.Fluent.ArchRuleDefinition;

public sealed class ArchitectureTests
{
    private static readonly Architecture Architecture =
        new ArchLoader()
            .LoadAssemblies(
                typeof(Ordering.Domain.Order).Assembly,
                typeof(Ordering.Application.SubmitOrderHandler).Assembly,
                typeof(Ordering.Infrastructure.Persistence.EfOrderRepository).Assembly,
                typeof(Ordering.Api.Program).Assembly)
            .Build();

    private readonly IObjectProvider<IType> Domain =
        Types().That().ResideInNamespace("Ordering.Domain").As("Domain");

    private readonly IObjectProvider<IType> Infrastructure =
        Types().That().ResideInNamespace("Ordering.Infrastructure").As("Infrastructure");

    private readonly IObjectProvider<IType> Api =
        Types().That().ResideInNamespace("Ordering.Api").As("API");

    [Fact]
    public void DomainShouldNotDependOnInfrastructureOrApi()
    {
        Types().That().Are(Domain).Should()
            .NotDependOnAny(Infrastructure)
            .AndShould().NotDependOnAny(Api)
            .Because("domain code should be independent of delivery and persistence")
            .Check(Architecture);
    }
}
```

If ArchUnitNET feels too heavy for a small project, use NetArchTest or a small reflection-based test. Keep the rule explicit.

## Go

Go has no direct standard equivalent to ArchUnit. Use import graph checks for the few boundaries that matter.

```bash
go list -deps ./internal/ordering/domain
```

Fail the test or CI step if domain packages import adapter, database, HTTP, or framework packages.

## Python

For Python, use import graph tooling only when the project is large enough to justify it. Candidate tools include `import-linter` or `grimp`.

Example rule intent:

```text
ordering.domain must not import ordering.adapters, fastapi, django, sqlalchemy, or celery
ordering.application may import ordering.domain
ordering.adapters may import ordering.application and ordering.domain
```

Do not add architecture tests that merely repeat folder names. Test boundaries that protect business rules or prevent costly coupling.
