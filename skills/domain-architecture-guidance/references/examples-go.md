# Go Examples

These examples translate DDD and ports/adapters into Go package boundaries. Prefer simple structs, functions, and small interfaces over Java-style layers.

## Package Sketch

```text
internal/ordering/
  domain/
  app/
  adapter/http/
  adapter/postgres/
```

This is a sketch, not a required layout. Go projects often work well with feature-oriented packages and minimal abstraction.

## Domain Package

```go
package domain

import "errors"

type OrderID string

type Order struct {
	id     OrderID
	lines  []OrderLine
	status OrderStatus
}

func NewOrder(id OrderID) *Order {
	return &Order{id: id, status: Draft}
}

func (o *Order) AddLine(sku string, price Money, quantity int) error {
	if o.status != Draft {
		return errors.New("only draft orders can be changed")
	}
	o.lines = append(o.lines, OrderLine{SKU: sku, Price: price, Quantity: quantity})
	return nil
}

func (o *Order) Submit() error {
	if len(o.lines) == 0 {
		return errors.New("cannot submit an empty order")
	}
	o.status = Submitted
	return nil
}
```

## Consumer-Side Port

Define interfaces where they are consumed when practical.

```go
package app

import (
	"context"

	"example.com/shop/internal/ordering/domain"
)

type OrderStore interface {
	FindByID(context.Context, domain.OrderID) (*domain.Order, error)
	Save(context.Context, *domain.Order) error
}

type SubmitOrder struct {
	Orders OrderStore
}

func (uc SubmitOrder) Handle(ctx context.Context, id domain.OrderID) error {
	order, err := uc.Orders.FindByID(ctx, id)
	if err != nil {
		return err
	}
	if err := order.Submit(); err != nil {
		return err
	}
	return uc.Orders.Save(ctx, order)
}
```

## Adapter Implements the Port

```go
package postgres

import (
	"context"
	"database/sql"

	"example.com/shop/internal/ordering/domain"
)

type OrderStore struct {
	db *sql.DB
}

func (s OrderStore) FindByID(ctx context.Context, id domain.OrderID) (*domain.Order, error) {
	// Map rows to domain objects here.
	return nil, nil
}

func (s OrderStore) Save(ctx context.Context, order *domain.Order) error {
	// Persist domain state here.
	return nil
}
```

Avoid creating an interface for every adapter if there is only one implementation and no meaningful boundary pressure.

## Lightweight Dependency Checks

For Go, use built-in tests or small scripts to protect critical imports. Keep them simple.

```go
func TestDomainDoesNotImportInfrastructure(t *testing.T) {
	// Use `go list -deps` or package graph tooling in real projects.
	// Assert that internal/ordering/domain does not import database/sql,
	// net/http, framework packages, or adapter packages.
}
```

