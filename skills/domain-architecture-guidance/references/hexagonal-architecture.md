# Hexagonal Architecture / Ports And Adapters

Use this when the selected architecture is Hexagonal / Ports and Adapters, or when reviewing whether ports, adapters, application services, and dependency direction are modeled correctly.

## Core Flow

```text
Primary Adapter -> Primary Port / Application Service -> Domain
Application or Domain Core -> Secondary Port -> Secondary Adapter
```

Inside/core code should not depend on adapter implementations. Adapters depend on ports or application services, not the other way around.

## Roles

- Primary Adapter: inbound driver such as REST controller, RPC endpoint, CLI command, scheduler, or message listener.
- Primary Port: inbound contract exposed by the application core to primary adapters.
- Application Service: use-case implementation and orchestration boundary; loads aggregates, invokes domain behavior, coordinates transactions, and calls secondary ports.
- Secondary Port: outbound need expressed by application or domain core.
- Secondary Adapter: implementation of a secondary port, such as persistence, external API client, broker sender, cache, file storage, or SDK adapter.

## Primary Port And Application Service Naming

Primary ports are contracts. Application services usually implement those contracts and contain the use-case orchestration.

Java/Kotlin examples may use `*UseCase` for the primary-port contract and `*Service` for the application-service implementation:

```java
@PrimaryPort
public interface SubmitOrderUseCase {
    void submit(SubmitOrder command);
}

@Application
final class SubmitOrderService implements SubmitOrderUseCase {
    // Load aggregates, invoke domain behavior, call secondary ports, save results.
}
```

The primary adapter depends on the primary port, not on the concrete service:

```text
OrderController -> SubmitOrderUseCase -> SubmitOrderService
```

This differs from secondary ports:

```text
SubmitOrderService -> PaymentGateway -> PaymentGatewayAdapter
```

Do not treat every type named `UseCase` as the implementation. If a project uses `*UseCase` naming, document whether it means the inbound contract, the implementation, or a command handler.

## Secondary Port Ownership

Place a secondary port by consumer ownership:

- If an application service needs workflow context, command enrichment, a read model, or a query shape, put the port near the application service.
- If a domain service or policy needs an external fact to make a domain decision, put a narrow domain-facing port near that domain service or policy.
- Keep domain-facing ports small and named by domain meaning, such as `CreditLimitPolicy`, `CustomerCreditLimit`, or `EnvironmentAvailability`.
- Do not move broad page, reporting, or integration ports into the domain just because one domain service needs one method. Split a narrow domain-facing port instead.

Aggregate repositories are DDD repository contracts for aggregate lifecycle and command-side loading. In Hexagonal implementations they are outbound contracts implemented by infrastructure, but do not duplicate them as generic application `port.out` interfaces.

## Boundary Checks

- Primary adapters should not call secondary adapters or repositories directly.
- Application services should not depend on concrete persistence, HTTP, broker, cache, SDK, or framework adapter types.
- Secondary adapters should translate external DTOs, status codes, and persistence models before returning to core code.
- Ports should represent meaningful variability, external dependency, test boundary, or model protection. Avoid one-interface-per-class abstractions.
