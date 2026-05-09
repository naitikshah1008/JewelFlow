# JewelFlow Project Status

## Project Purpose
JewelFlow is a Spring Boot backend for a jewelry inventory, customer, pricing, and billing management system. The goal is to help small and mid-sized jewelry stores replace manual or outdated jewelry software with cleaner APIs for tracking jewelry items, customers, gold-rate based pricing, sales invoices, and store dashboard metrics.

## Current Tech Stack
- Backend framework: Spring Boot 4.0.6
- Language: Java 17 target, using Lombok for boilerplate reduction
- API style: REST controllers under `/api/**`
- Persistence: Spring Data JPA with PostgreSQL
- Database local setup: PostgreSQL 16 via Docker Compose
- Build tool: Maven Wrapper (`backend/mvnw`, `backend/mvnw.cmd`)
- Testing: JUnit 5 / Spring Boot Test through Maven Surefire
- Current test coverage: one `contextLoads` Spring Boot application test
- API testing approach: manual Postman testing against `http://localhost:8080`

## Current Folder Structure
- `backend/src/main/java/com/jewelflow/backend`: Spring Boot application entry point.
- `backend/src/main/java/com/jewelflow/backend/config`: local Spring Security configuration.
- `backend/src/main/java/com/jewelflow/backend/customer`: customer entity, request DTO, repository, service, and controller.
- `backend/src/main/java/com/jewelflow/backend/dashboard`: dashboard summary DTOs, service, and controller.
- `backend/src/main/java/com/jewelflow/backend/exception`: shared exception type and global REST exception handler.
- `backend/src/main/java/com/jewelflow/backend/goldrate`: gold rate entity, request/response DTOs, repository, service, and controller.
- `backend/src/main/java/com/jewelflow/backend/invoice`: invoice/order entity, request/response DTOs, repository, service, and controller.
- `backend/src/main/java/com/jewelflow/backend/inventory`: jewelry item entity, request DTO, repository, service, and controller.
- `backend/src/main/java/com/jewelflow/backend/pricing`: pricing request/response DTOs, pricing service, and controller.
- `backend/src/main/java/com/jewelflow/backend/sales`: sale entity, request/response DTOs, repository, service, and controller.
- `backend/src/main/resources`: Spring application configuration.
- `backend/src/test/java/com/jewelflow/backend`: Spring Boot context test.
- `infra`: Docker Compose file for local PostgreSQL.

## Newly Added Files
- `PROJECT_STATUS.md`: current project status, API inventory, setup notes, testing flow, known issues, and next steps.
- `.gitignore`: updated to ignore local `postman/` workspace files in addition to `.postman/`.
- `backend/src/main/java/com/jewelflow/backend/invoice/Invoice.java`: invoice/order JPA entity backed by the `invoices` table.
- `backend/src/main/java/com/jewelflow/backend/invoice/InvoiceRequest.java`: invoice/order creation request DTO with validation annotations.
- `backend/src/main/java/com/jewelflow/backend/invoice/InvoiceResponse.java`: invoice/order response DTO.
- `backend/src/main/java/com/jewelflow/backend/invoice/InvoiceRepository.java`: Spring Data JPA repository for invoices.
- `backend/src/main/java/com/jewelflow/backend/invoice/InvoiceService.java`: invoice/order business logic.
- `backend/src/main/java/com/jewelflow/backend/invoice/InvoiceController.java`: REST controller for `/api/invoices`.

## Completed Features
- Spring Boot backend project with Maven Wrapper.
- PostgreSQL configuration for local development.
- Docker Compose setup for PostgreSQL 16.
- Inventory CRUD APIs for jewelry items.
- Automatic item pricing during inventory create/update.
- Inventory create/update request validation for required fields and non-negative/positive numeric values.
- Inventory create/update cross-field validation for impossible weight combinations.
- Standalone pricing calculator API using `BigDecimal`.
- Pricing calculator request validation for required fields and non-negative/positive numeric values.
- Pricing rejects discounts that exceed the subtotal before tax.
- Purity factor calculation for `24K`, `22K`, `18K`, and `14K`.
- Gold rate management APIs for creating and reading rate history.
- Latest gold rate lookup by `metalType` and `purity`.
- Pricing fallback to latest saved gold rate when `goldRatePerGram` is not provided.
- Customer CRUD APIs.
- Customer request validation for full name, phone number, and email format.
- Duplicate customer phone-number protection.
- Sale/invoice creation API.
- Sale records snapshot customer, item, weight, gold rate, tax, discount, and final amount.
- Sale creation changes the sold inventory item status from `AVAILABLE` to `SOLD`.
- Sale creation prevents selling items that are not `AVAILABLE`.
- Invoice/order creation API.
- Invoice records snapshot customer, item, quantity, weights, gold rate, tax, discount, and final amount.
- Invoice creation reuses `PricingService` instead of duplicating gold/purity pricing formulas.
- Invoice creation validates customer id, item id, quantity, tax percentage, discount, payment status, and payment method.
- Invoice creation changes the invoiced inventory item status from `AVAILABLE` to `SOLD`.
- Invoice creation prevents invoicing items that are not `AVAILABLE`.
- Controlled enum validation for item status, metal type, purity, payment status, payment method, and invoice order status.
- Dashboard summary API for inventory counts, inventory value, sales counts, revenue, and recent sales.
- Shared `ResourceNotFoundException`.
- Shared `GlobalExceptionHandler` for 404, bad request, and validation responses.
- Local security configuration permits `/api/**` without authentication.
- Basic Spring Boot context test and focused validation unit tests.

## Current API Endpoints

| Method | Endpoint path | Controller file | Purpose | Request body needed | Sample response shape |
| --- | --- | --- | --- | --- | --- |
| `POST` | `/api/items` | `JewelleryItemController.java` | Create inventory item and calculate pricing | `JewelleryItemRequest` with item, weight, purity, pricing inputs | `{ "id": 1, "itemName": "...", "goldRatePerGram": 6500, "goldValue": 0, "taxAmount": 0, "sellingPrice": 0, "status": "AVAILABLE" }` |
| `GET` | `/api/items` | `JewelleryItemController.java` | List all inventory items | No | `[ { "id": 1, "itemName": "...", "status": "AVAILABLE" } ]` |
| `GET` | `/api/items/{id}` | `JewelleryItemController.java` | Get one inventory item by id | No | `{ "id": 1, "itemName": "...", "sellingPrice": 0 }` |
| `PUT` | `/api/items/{id}` | `JewelleryItemController.java` | Update item and recalculate pricing | `JewelleryItemRequest` | `{ "id": 1, "itemName": "...", "sellingPrice": 0, "updatedAt": "..." }` |
| `DELETE` | `/api/items/{id}` | `JewelleryItemController.java` | Delete inventory item | No | Empty response body |
| `POST` | `/api/pricing/calculate` | `PricingController.java` | Calculate gold value, subtotal, tax, and final price | `PricingRequest` | `{ "purityFactor": 0.916667, "goldRatePerGram": 6500, "goldValue": 0, "subtotal": 0, "taxAmount": 0, "finalPrice": 0 }` |
| `POST` | `/api/customers` | `CustomerController.java` | Create customer | `CustomerRequest` | `{ "id": 1, "fullName": "...", "phoneNumber": "...", "email": "...", "createdAt": "..." }` |
| `GET` | `/api/customers` | `CustomerController.java` | List all customers | No | `[ { "id": 1, "fullName": "...", "phoneNumber": "..." } ]` |
| `GET` | `/api/customers/{id}` | `CustomerController.java` | Get one customer by id | No | `{ "id": 1, "fullName": "...", "phoneNumber": "..." }` |
| `PUT` | `/api/customers/{id}` | `CustomerController.java` | Update customer | `CustomerRequest` | `{ "id": 1, "fullName": "...", "updatedAt": "..." }` |
| `DELETE` | `/api/customers/{id}` | `CustomerController.java` | Delete customer | No | Empty response body |
| `POST` | `/api/sales` | `SaleController.java` | Create sale invoice and mark item sold | `SaleRequest` with `customerId`, `itemId`, `paymentStatus`, `paymentMethod` | `{ "id": 1, "invoiceNumber": "JF-INV-000001", "customerName": "...", "itemName": "...", "finalAmount": 0, "paymentStatus": "PAID" }` |
| `GET` | `/api/sales` | `SaleController.java` | List all sales | No | `[ { "id": 1, "invoiceNumber": "JF-INV-000001", "finalAmount": 0 } ]` |
| `GET` | `/api/sales/{id}` | `SaleController.java` | Get one sale by id | No | `{ "id": 1, "invoiceNumber": "JF-INV-000001", "finalAmount": 0 }` |
| `POST` | `/api/invoices` | `InvoiceController.java` | Create invoice/order and mark item sold | `InvoiceRequest` with `customerId`, `itemId`, `quantity`, `taxPercentage`, `discount`, `paymentStatus`, `paymentMethod` | `{ "id": 1, "invoiceNumber": "JF-ORDER-000001", "customerName": "...", "itemName": "...", "quantity": 1, "subtotal": 0, "taxAmount": 0, "finalAmount": 0, "orderStatus": "ISSUED" }` |
| `GET` | `/api/invoices` | `InvoiceController.java` | List all invoices/orders | No | `[ { "id": 1, "invoiceNumber": "JF-ORDER-000001", "finalAmount": 0, "orderStatus": "ISSUED" } ]` |
| `GET` | `/api/invoices/{id}` | `InvoiceController.java` | Get one invoice/order by id | No | `{ "id": 1, "invoiceNumber": "JF-ORDER-000001", "quantity": 1, "finalAmount": 0 }` |
| `POST` | `/api/gold-rates` | `GoldRateController.java` | Create a gold rate history record | `GoldRateRequest` | `{ "id": 1, "metalType": "GOLD", "purity": "22K", "ratePerGram": 6500, "rateDate": "2026-05-08" }` |
| `GET` | `/api/gold-rates` | `GoldRateController.java` | List all gold rates, optionally filtered by `metalType` and `purity` | No | `[ { "id": 1, "metalType": "GOLD", "purity": "22K", "ratePerGram": 6500 } ]` |
| `GET` | `/api/gold-rates/latest?metalType=Gold&purity=22K` | `GoldRateController.java` | Get latest rate for a metal and purity | No | `{ "id": 1, "metalType": "GOLD", "purity": "22K", "ratePerGram": 6500 }` |
| `GET` | `/api/gold-rates/{id}` | `GoldRateController.java` | Get one gold rate record by id | No | `{ "id": 1, "ratePerGram": 6500, "rateDate": "2026-05-08" }` |
| `GET` | `/api/dashboard/summary` | `DashboardController.java` | Get dashboard metrics | No | `{ "totalCustomers": 0, "totalInventoryItems": 0, "availableItems": 0, "totalRevenue": 0, "recentSales": [] }` |

## Current Data Models

### `JewelleryItem`
Important fields: `id`, `itemName`, `category`, `metalType`, `purity`, `grossWeight`, `netWeight`, `stoneWeight`, `goldRatePerGram`, `stonePrice`, `makingCharges`, `taxPercentage`, `discount`, `goldValue`, `taxAmount`, `purchaseCost`, `sellingPrice`, `status`, `createdAt`, `updatedAt`.

### `Customer`
Important fields: `id`, `fullName`, `phoneNumber`, `email`, `address`, `city`, `state`, `postalCode`, `notes`, `createdAt`, `updatedAt`.

### `Sale`
Important fields: `id`, `invoiceNumber`, `customerId`, `customerName`, `customerPhoneNumber`, `itemId`, `itemName`, `category`, `metalType`, `purity`, `grossWeight`, `netWeight`, `stoneWeight`, `goldRatePerGram`, `goldValue`, `stonePrice`, `makingCharges`, `taxPercentage`, `taxAmount`, `discount`, `finalAmount`, `paymentStatus`, `paymentMethod`, `notes`, `saleDate`, `createdAt`, `updatedAt`.

### `Invoice`
Important fields: `id`, `invoiceNumber`, `customerId`, `customerName`, `customerPhoneNumber`, `itemId`, `itemName`, `category`, `metalType`, `purity`, `quantity`, `grossWeight`, `netWeight`, `stoneWeight`, `goldRatePerGram`, `goldValue`, `stonePrice`, `makingCharges`, `subtotal`, `taxPercentage`, `taxAmount`, `discount`, `unitFinalAmount`, `finalAmount`, `orderStatus`, `paymentStatus`, `paymentMethod`, `notes`, `invoiceDate`, `createdAt`, `updatedAt`.

### `GoldRate`
Important fields: `id`, `metalType`, `purity`, `ratePerGram`, `rateDate`, `source`, `notes`, `createdAt`, `updatedAt`.

### DTOs / Response Models
- `JewelleryItemRequest`: inventory create/update input with validation annotations.
- `PricingRequest`: pricing calculator input with validation annotations.
- `PricingResponse`: calculated pricing output.
- `CustomerRequest`: customer create/update input with validation annotations.
- `SaleRequest`: sale creation input with validation annotations.
- `SaleResponse`: sale/invoice output.
- `InvoiceRequest`: invoice/order creation input with validation annotations.
- `InvoiceResponse`: invoice/order output.
- `GoldRateRequest`: gold rate creation input with validation annotations.
- `GoldRateResponse`: gold rate output.
- `DashboardSummaryResponse`: dashboard totals and recent sales.
- `RecentSaleResponse`: compact sale summary for dashboard.

## Database / Model Changes
- Existing JPA entities map to `jewellery_items`, `customers`, `sales`, `gold_rates`, and `invoices`.
- `Invoice` uses `@Table(name = "invoices")`.
- `Invoice.invoiceNumber` is unique and required.
- `Invoice.customerId`, `Invoice.itemId`, and `Invoice.quantity` are required columns.
- With `spring.jpa.hibernate.ddl-auto=update`, Hibernate creates/updates the `invoices` table automatically in local development.
- Invoice records snapshot customer details, item details, weights, pricing values, tax, discount, payment values, and final amount at invoice creation time.
- There are no explicit foreign-key relationships yet between `Invoice` and `Customer` / `JewelleryItem`; ids and snapshot fields are stored directly.

## Configuration Notes
- Local server port is `8080`.
- `application.properties` sets `spring.application.name=backend`.
- `application.yml` configures PostgreSQL at `jdbc:postgresql://localhost:5432/jewelflow`.
- The current local datasource username is `jewelflow_user`.
- The current local datasource password is a local development value from the checked-in config.
- Hibernate is configured with `spring.jpa.hibernate.ddl-auto=update`.
- SQL logging is enabled with `spring.jpa.show-sql=true` and formatted SQL.
- Docker Compose starts `postgres:16` on host port `5432`.
- Spring Security is installed but currently configured to permit all `/api/**` and all other requests.
- CSRF is disabled in `SecurityConfig`.
- Controllers use `@CrossOrigin(origins = "*")`.
- There is no JWT implementation yet, even though `.env.example` includes a `JWT_SECRET` placeholder.

## How to Run Locally
From the repository root:

```bash
cd infra
docker compose up -d
```

Then start the backend:

```bash
cd ../backend
./mvnw spring-boot:run
```

Run tests:

```bash
cd backend
./mvnw test
```

On Windows, use:

```bash
backend\mvnw.cmd test
```

## Commands / Tests Run
Latest verification command:

```bash
cd backend
./mvnw test
```

Latest result:

```text
BUILD SUCCESS
Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
```

## How to Test in Postman
Use base URL:

```text
http://localhost:8080
```

Suggested testing flow:

1. Create a gold rate:

```http
POST http://localhost:8080/api/gold-rates
```

```json
{
  "metalType": "Gold",
  "purity": "22K",
  "ratePerGram": 6500,
  "rateDate": "2026-05-08",
  "source": "Manual",
  "notes": "Store rate"
}
```

2. Create a customer:

```http
POST http://localhost:8080/api/customers
```

```json
{
  "fullName": "Rahul Mehta",
  "phoneNumber": "9876543210",
  "email": "rahul@example.com",
  "address": "MG Road",
  "city": "Mumbai",
  "state": "Maharashtra",
  "postalCode": "400001",
  "notes": "Interested in gold rings"
}
```

3. Create an inventory item. If `goldRatePerGram` is omitted, the latest saved gold rate is used:

```http
POST http://localhost:8080/api/items
```

```json
{
  "itemName": "Gold Ring",
  "category": "Ring",
  "metalType": "Gold",
  "purity": "22K",
  "grossWeight": 10.5,
  "netWeight": 9.8,
  "stoneWeight": 0.7,
  "stonePrice": 1500,
  "makingCharges": 2500,
  "taxPercentage": 3,
  "discount": 1000,
  "purchaseCost": 58000,
  "status": "AVAILABLE"
}
```

4. Create a sale:

```http
POST http://localhost:8080/api/sales
```

```json
{
  "customerId": 1,
  "itemId": 1,
  "paymentStatus": "PAID",
  "paymentMethod": "CASH",
  "notes": "First purchase"
}
```

5. Create an invoice/order:

```http
POST http://localhost:8080/api/invoices
```

```json
{
  "customerId": 1,
  "itemId": 1,
  "quantity": 1,
  "taxPercentage": 3,
  "discount": 1000,
  "paymentStatus": "PAID",
  "paymentMethod": "CASH",
  "notes": "Invoice created from Postman"
}
```

Supported invoice `paymentStatus` values:

```text
PAID, UNPAID, PARTIAL
```

Supported invoice `paymentMethod` values:

```text
CASH, CARD, UPI, BANK_TRANSFER, OTHER
```

Example invoice response shape:

```json
{
  "id": 1,
  "invoiceNumber": "JF-ORDER-000001",
  "customerId": 1,
  "customerName": "Rahul Mehta",
  "customerPhoneNumber": "9876543210",
  "itemId": 1,
  "itemName": "Gold Ring",
  "category": "Ring",
  "metalType": "Gold",
  "purity": "22K",
  "quantity": 1,
  "grossWeight": 10.50,
  "netWeight": 9.80,
  "stoneWeight": 0.70,
  "goldRatePerGram": 6500,
  "goldValue": 58391.67,
  "stonePrice": 1500.00,
  "makingCharges": 2500.00,
  "subtotal": 61391.67,
  "taxPercentage": 3,
  "taxAmount": 1841.75,
  "discount": 1000,
  "unitFinalAmount": 63233.42,
  "finalAmount": 63233.42,
  "orderStatus": "ISSUED",
  "paymentStatus": "PAID",
  "paymentMethod": "CASH",
  "notes": "Invoice created from Postman",
  "invoiceDate": "2026-05-08T..."
}
```

6. List invoices/orders:

```http
GET http://localhost:8080/api/invoices
```

7. Get one invoice/order:

```http
GET http://localhost:8080/api/invoices/1
```

8. Check the dashboard:

```http
GET http://localhost:8080/api/dashboard/summary
```

## Known Issues
- Inventory and pricing validation covers required fields, numeric ranges, controlled enum values, item weight relationships, and discount ceilings.
- `goldRatePerGram` remains optional by design because pricing can fall back to the latest saved gold rate for `metalType` and `purity`.
- `status`, `purity`, `metalType`, `paymentStatus`, and `paymentMethod` are stored as strings in the database, but application inputs are normalized through controlled enums.
- Inventory and customer delete endpoints perform hard deletes.
- Sale invoice number generation is based on `saleRepository.count() + 1`, which is not safe under concurrent requests.
- Invoice/order number generation is based on `invoiceRepository.count() + 1`, which is not safe under concurrent requests.
- Invoice quantity scales pricing values, but the current inventory model still represents one serialized jewelry item and does not track stock quantity decrementing.
- Invoice creation and sale creation are separate flows that both mark a single item as `SOLD`; they should eventually be unified or clearly separated by business purpose.
- There is no real authentication, authorization, JWT, or role model yet.
- Spring Security logs a generated password even though local config permits requests.
- `ddl-auto=update` is convenient locally but should be replaced with migrations before production.
- Tests only verify application context startup; service and controller behavior are not covered.
- No frontend module exists in the repository.
- No pagination, sorting, or search filters exist for list endpoints.
- Dashboard metrics use server-local date boundaries.
- Local Postman workspace files under `postman/` are ignored by `.gitignore`; commit only intentionally exported and sanitized API collections.

## Git and .gitignore Notes
Do not commit local, generated, or sensitive files such as:

- `.env`
- `.env.*` except `.env.example`
- `backend/target/`
- `backend/*.log`
- `backend/logs/`
- `.idea/`
- `.vscode/`
- `*.iml`
- `logs/`
- `*.log`
- `tmp/`
- `temp/`
- generated build artifacts such as `*.class`, `*.jar`, `*.war`, `*.ear`
- local Docker overrides such as `docker-compose.override.yml`
- local Postman workspace files such as `.postman/`, `.postman`, and `postman/` unless intentionally exported and sanitized

Maven wrapper files should remain committed:

- `backend/mvnw`
- `backend/mvnw.cmd`
- `backend/.mvn/wrapper/maven-wrapper.jar`, if present

## Next Recommended Steps
1. Add focused service/controller tests for customer duplicate phone handling, sales creation, invoice creation, and dashboard summaries.
2. Improve sale and invoice numbering so it is safe and predictable under concurrent requests.
3. Add real authentication and role-based authorization for owner/admin, salesperson, and inventory manager workflows.

Recommended commit message for the latest validation/status update:

```text
Add inventory and pricing request validation
```
