# Java / Kotlin Examples

These examples show how to translate the skill into Java/Kotlin code. Treat them as sketches, not mandatory templates.

Use jMolecules when it makes DDD or architecture roles explicit and when tools such as ArchUnit, jQAssistant, Spring Modulith, or documentation generators can use those roles.

## jMolecules DDD Annotations

Use annotations when they clarify domain intent. Do not add them mechanically to every class.

```java
package com.example.orders.domain;

import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;
import org.jmolecules.ddd.annotation.ValueObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AggregateRoot
public class Order {

    @Identity
    private final OrderId id;

    private final List<OrderLine> lines = new ArrayList<>();
    private OrderStatus status = OrderStatus.DRAFT;

    public Order(OrderId id) {
        this.id = id;
    }

    public OrderId id() {
        return id;
    }

    public void addLine(String sku, Money price, int quantity) {
        if (status != OrderStatus.DRAFT) {
            throw new IllegalStateException("Only draft orders can be changed");
        }
        lines.add(new OrderLine(sku, price, quantity));
    }

    public void submit() {
        if (lines.isEmpty()) {
            throw new IllegalStateException("Cannot submit an empty order");
        }
        status = OrderStatus.SUBMITTED;
    }

    public Money total() {
        return lines.stream()
                .map(line -> line.price().multiply(line.quantity()))
                .reduce(new Money(BigDecimal.ZERO, "USD"), Money::add);
    }
}

public record OrderId(UUID value) {}

public record OrderLine(String sku, Money price, int quantity) {}

@ValueObject
public record Money(BigDecimal amount, String currency) {
    Money multiply(int quantity) {
        return new Money(amount.multiply(BigDecimal.valueOf(quantity)), currency);
    }

    Money add(Money other) {
        if (!currency.equals(other.currency)) {
            throw new IllegalArgumentException("Currency mismatch");
        }
        return new Money(amount.add(other.amount), currency);
    }
}
```

Use a repository abstraction when it protects application/domain code from persistence details. Keep persistence APIs out of the repository contract.

```java
package com.example.orders.domain;

import org.jmolecules.ddd.annotation.Repository;

import java.util.Optional;

@Repository
public interface Orders {
    Optional<Order> findById(OrderId id);
    void save(Order order);
}
```

## Package-Level Layer Markers

Use package-level annotations when all classes in a package have a stable role.

```java
@org.jmolecules.architecture.layered.DomainLayer
package com.example.orders.domain;
```

```java
@org.jmolecules.architecture.layered.ApplicationLayer
package com.example.orders.application;
```

```java
@org.jmolecules.architecture.layered.InfrastructureLayer
package com.example.orders.infrastructure;
```

Prefer class-level annotations only during migration or when packages intentionally mix roles.

## Onion Architecture Rings

Use Onion Architecture when the domain model should be the explicit center and dependencies should point inward. jMolecules provides both classical and simplified Onion annotations. This sketch uses the classical rings.

Package-level annotations are usually clearer than class-level annotations when packages map cleanly to rings:

```java
@org.jmolecules.architecture.onion.classical.DomainModelRing
package com.example.orders.domain.model;
```

```java
@org.jmolecules.architecture.onion.classical.DomainServiceRing
package com.example.orders.domain.service;
```

```java
@org.jmolecules.architecture.onion.classical.ApplicationServiceRing
package com.example.orders.application;
```

```java
@org.jmolecules.architecture.onion.classical.InfrastructureRing
package com.example.orders.infrastructure;
```

The domain model ring should not depend on application or infrastructure code:

```java
package com.example.orders.domain.model;

import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.ValueObject;

@AggregateRoot
public class Order {
    private final OrderId id;
    private OrderStatus status = OrderStatus.DRAFT;

    public Order(OrderId id) {
        this.id = id;
    }

    public void submit() {
        if (status != OrderStatus.DRAFT) {
            throw new IllegalStateException("Only draft orders can be submitted");
        }
        status = OrderStatus.SUBMITTED;
    }
}

@ValueObject
public record OrderId(UUID value) {}
```

Application services orchestrate use cases and depend inward on the domain model and domain service rings:

```java
package com.example.orders.application;

import org.jmolecules.architecture.onion.classical.ApplicationServiceRing;

@ApplicationServiceRing
public final class SubmitOrderService {

    private final Orders orders;

    public SubmitOrderService(Orders orders) {
        this.orders = orders;
    }

    public void submit(OrderId id) {
        var order = orders.findById(id).orElseThrow();
        order.submit();
        orders.save(order);
    }
}
```

Infrastructure implements persistence, messaging, or framework integration. It depends on inner rings, not the other way around:

```java
package com.example.orders.infrastructure.jpa;

import org.jmolecules.architecture.onion.classical.InfrastructureRing;

@InfrastructureRing
final class JpaOrders implements Orders {
    // Translate between JPA persistence details and the domain model here.
}
```

Use the simplified Onion annotations (`DomainRing`, `ApplicationRing`, `InfrastructureRing`) when the extra domain service ring does not add clarity.

## Hexagonal / Ports and Adapters

Use primary ports for operations exposed to driving adapters. Use secondary ports for dependencies inside/core code needs from the outside. Place each secondary port near the consumer: application workflow ports near application services, and narrow domain-decision ports near the domain service or policy that consumes them.

```java
package com.example.orders.application;

import org.jmolecules.architecture.hexagonal.Application;
import org.jmolecules.architecture.hexagonal.PrimaryPort;
import org.jmolecules.architecture.hexagonal.SecondaryPort;

@PrimaryPort
public interface SubmitOrderUseCase {
    void submit(SubmitOrder command);
}

public record SubmitOrder(OrderId orderId) {}

@SecondaryPort
public interface PaymentGateway {
    PaymentResult authorize(OrderId orderId, Money amount);
}

public record PaymentResult(String authorizationId) {}

@Application
final class SubmitOrderService implements SubmitOrderUseCase {

    private final Orders orders;
    private final PaymentGateway payments;

    SubmitOrderService(Orders orders, PaymentGateway payments) {
        this.orders = orders;
        this.payments = payments;
    }

    @Override
    public void submit(SubmitOrder command) {
        var order = orders.findById(command.orderId()).orElseThrow();
        payments.authorize(order.id(), order.total());
        order.submit();
        orders.save(order);
    }
}
```

`Orders` is an aggregate repository contract from the domain model. `PaymentGateway` is an application-owned secondary port because the submit use case needs it to coordinate payment authorization. If a domain policy needs an external fact to make a domain decision, define a narrow domain-facing secondary port instead of reusing a broad query/read port:

```java
package com.example.orders.domain;

import org.jmolecules.architecture.hexagonal.SecondaryPort;

@SecondaryPort
public interface CustomerCreditLimit {
    boolean canReserve(CustomerId customerId, Money amount);
}
```

Adapters should depend on ports, not the other way around.

```java
package com.example.orders.web;

import org.jmolecules.architecture.hexagonal.PrimaryAdapter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@PrimaryAdapter
@RestController
class OrderController {

    private final SubmitOrderUseCase submitOrder;

    OrderController(SubmitOrderUseCase submitOrder) {
        this.submitOrder = submitOrder;
    }

    @PostMapping("/orders/{id}/submit")
    void submit(String id) {
        submitOrder.submit(new SubmitOrder(new OrderId(UUID.fromString(id))));
    }
}
```

## CQRS Without Event Sourcing by Default

Use current jMolecules CQRS annotations from `org.jmolecules.architecture.cqrs`, not deprecated `org.jmolecules.architecture.cqrs.annotation` types.

```java
package com.example.orders.application;

import org.jmolecules.architecture.cqrs.Command;
import org.jmolecules.architecture.cqrs.CommandHandler;
import org.jmolecules.architecture.cqrs.QueryModel;

@Command
public record SubmitOrderCommand(OrderId orderId) {}

final class SubmitOrderHandler {

    private final SubmitOrderUseCase useCase;

    SubmitOrderHandler(SubmitOrderUseCase useCase) {
        this.useCase = useCase;
    }

    @CommandHandler
    void handle(SubmitOrderCommand command) {
        useCase.submit(new SubmitOrder(command.orderId()));
    }
}

@QueryModel
public record OrderSummary(String id, String status, String customerName) {}
```

Do not introduce CQRS if commands and queries use the same data shape and the extra split adds only ceremony.
