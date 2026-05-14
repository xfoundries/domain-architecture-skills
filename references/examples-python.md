# Python Examples

Use DDD and ports/adapters selectively in Python. Framework conventions often matter more than a textbook package layout.

## FastAPI / Flask Style

```text
ordering/
  domain.py
  application.py
  adapters/
    http.py
    sqlalchemy_orders.py
```

## Domain Model

```python
from dataclasses import dataclass, field
from decimal import Decimal
from enum import Enum


class OrderStatus(Enum):
    DRAFT = "draft"
    SUBMITTED = "submitted"


@dataclass(frozen=True)
class Money:
    amount: Decimal
    currency: str


@dataclass
class Order:
    id: str
    lines: list[str] = field(default_factory=list)
    status: OrderStatus = OrderStatus.DRAFT

    def add_line(self, sku: str) -> None:
        if self.status != OrderStatus.DRAFT:
            raise ValueError("only draft orders can be changed")
        self.lines.append(sku)

    def submit(self) -> None:
        if not self.lines:
            raise ValueError("cannot submit an empty order")
        self.status = OrderStatus.SUBMITTED
```

## Application Service with Protocol Port

```python
from typing import Protocol


class OrderRepository(Protocol):
    def get(self, order_id: str) -> Order | None: ...
    def save(self, order: Order) -> None: ...


class SubmitOrder:
    def __init__(self, orders: OrderRepository) -> None:
        self.orders = orders

    def __call__(self, order_id: str) -> None:
        order = self.orders.get(order_id)
        if order is None:
            raise ValueError("order not found")
        order.submit()
        self.orders.save(order)
```

Use `Protocol` when the boundary is useful for testing or adapter isolation. Duck typing can be enough for small services.

## Django Caution

For CRUD-heavy Django apps, do not fight the framework. A pragmatic service around Django models can be enough:

```python
def submit_order(order_id: int) -> None:
    order = Order.objects.select_for_update().get(id=order_id)
    if not order.lines.exists():
        raise ValueError("cannot submit an empty order")
    order.status = Order.Status.SUBMITTED
    order.save(update_fields=["status"])
```

Extract a pure domain model only when business rules become hard to test, reuse, or reason about inside ORM models.

## Lightweight Import Boundary Test

```python
def test_domain_does_not_import_fastapi_or_sqlalchemy():
    # Implement with import graph tooling such as grimp/import-linter
    # when the project is large enough to justify it.
    pass
```

