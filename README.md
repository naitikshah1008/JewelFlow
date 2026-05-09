# JewelFlow

JewelFlow is a cloud-ready Spring Boot backend for small and mid-sized jewelry stores to manage inventory, pricing, customers, sales, and invoices in one system.

## What it solves
Traditional jewelry software is often desktop-first and difficult to scale. JewelFlow provides REST APIs that support:
- Jewelry inventory lifecycle tracking
- Gold/purity-based pricing
- Customer records
- Sales and invoice creation with pricing snapshots
- Dashboard summaries for store operations

## Tech stack
- Java 17
- Spring Boot
- Spring Data JPA + Hibernate
- PostgreSQL
- Maven (`./mvnw`)
- Bean Validation (`jakarta.validation`)

## Current modules
- `inventory` - item CRUD and pricing integration
- `pricing` - reusable calculator logic
- `customer` - customer CRUD with duplicate phone protection
- `sales` - sale flow and status transition to `SOLD`
- `invoice` - invoice/order management
- `goldrate` - latest and historical rate APIs
- `dashboard` - summary reporting endpoint
- `exception` - consistent API error responses

## Run locally
1. Start PostgreSQL (or use Docker Compose if configured).
2. Configure environment variables / application config.
3. Run:

```bash
./mvnw spring-boot:run
```

Backend default URL:
- `http://localhost:8080`

## Test
```bash
./mvnw test
```

## Example endpoints
- `POST /api/items`
- `POST /api/pricing/calculate`
- `POST /api/customers`
- `POST /api/sales`
- `POST /api/invoices`
- `GET /api/dashboard/summary`

For detailed project state, see `PROJECT_STATUS.md`.
