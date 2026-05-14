# C# / .NET Examples

These examples translate the same ideas into idiomatic .NET. Do not copy Java annotations or jMolecules types into C#.

Use Microsoft Learn's ".NET Microservices Architecture" DDD guidance as a pragmatic reference for .NET implementation tradeoffs:

- https://learn.microsoft.com/en-us/dotnet/architecture/microservices/microservice-ddd-cqrs-patterns/ddd-oriented-microservice

Important Microsoft guidance reflected here:

- Apply DDD mainly to complex microservices with significant business rules.
- Simpler CRUD responsibilities can use simpler approaches.
- Keep bounded contexts cohesive and avoid chatty service boundaries.
- Keep the domain model independent from presentation, application, and infrastructure concerns.
- Use POCO domain entities where practical and avoid direct dependencies on EF Core, NHibernate, or other persistence frameworks.
- Keep application logic thin: it coordinates tasks and delegates business rules to domain objects.

## Project Boundary Sketch

Use projects or namespaces when compile-time boundaries help:

```text
src/
  Ordering.Domain/
  Ordering.Application/
  Ordering.Infrastructure/
  Ordering.Api/
tests/
  Ordering.ArchitectureTests/
```

Keep this simpler for CRUD-heavy services. A vertical slice can be better than four projects with no real boundary value.

## Aggregate and Value Object

```csharp
namespace Ordering.Domain;

public sealed class Order
{
    private readonly List<OrderLine> _lines = [];

    public OrderId Id { get; }
    public OrderStatus Status { get; private set; } = OrderStatus.Draft;
    public IReadOnlyCollection<OrderLine> Lines => _lines.AsReadOnly();

    public Order(OrderId id)
    {
        Id = id;
    }

    public void AddLine(string sku, Money price, int quantity)
    {
        if (Status != OrderStatus.Draft)
        {
            throw new InvalidOperationException("Only draft orders can be changed.");
        }

        _lines.Add(new OrderLine(sku, price, quantity));
    }

    public void Submit()
    {
        if (_lines.Count == 0)
        {
            throw new InvalidOperationException("Cannot submit an empty order.");
        }

        Status = OrderStatus.Submitted;
    }
}

public readonly record struct OrderId(Guid Value);
public readonly record struct Money(decimal Amount, string Currency);
```

## Application Service and Port

```csharp
namespace Ordering.Application;

using Ordering.Domain;

public interface IOrderRepository
{
    Task<Order?> FindById(OrderId id, CancellationToken cancellationToken);
    Task Save(Order order, CancellationToken cancellationToken);
}

public sealed record SubmitOrderCommand(OrderId OrderId);

public sealed class SubmitOrderHandler
{
    private readonly IOrderRepository _orders;

    public SubmitOrderHandler(IOrderRepository orders)
    {
        _orders = orders;
    }

    public async Task Handle(SubmitOrderCommand command, CancellationToken cancellationToken)
    {
        var order = await _orders.FindById(command.OrderId, cancellationToken)
            ?? throw new InvalidOperationException("Order not found.");

        order.Submit();
        await _orders.Save(order, cancellationToken);
    }
}
```

Use MediatR only when it is already a project convention or meaningfully reduces coupling. A plain application service is often enough.

## Infrastructure Adapter

```csharp
namespace Ordering.Infrastructure.Persistence;

using Microsoft.EntityFrameworkCore;
using Ordering.Application;
using Ordering.Domain;

public sealed class EfOrderRepository : IOrderRepository
{
    private readonly OrderingDbContext _db;

    public EfOrderRepository(OrderingDbContext db)
    {
        _db = db;
    }

    public Task<Order?> FindById(OrderId id, CancellationToken cancellationToken) =>
        _db.Orders.SingleOrDefaultAsync(order => order.Id == id, cancellationToken);

    public async Task Save(Order order, CancellationToken cancellationToken)
    {
        _db.Update(order);
        await _db.SaveChangesAsync(cancellationToken);
    }
}
```

If EF Core mapping attributes or base classes would pollute the domain model, move mapping details into `IEntityTypeConfiguration<T>` classes.

This follows the Microsoft guidance that domain entities should avoid direct dependencies on infrastructure frameworks when the domain model is behavior-rich. Still understand persistence constraints; persistence ignorance does not mean persistence ignorance by the developer.

## CQRS When Justified

```csharp
namespace Ordering.Application.Queries;

public sealed record OrderSummaryDto(Guid Id, string Status, string CustomerName);

public interface IOrderSummaryReadModel
{
    Task<OrderSummaryDto?> Find(Guid id, CancellationToken cancellationToken);
}
```

Use separate read models when the query shape, performance needs, or consistency requirements differ from the write model. Do not split reads and writes only because the handler names look symmetrical.
