# JewelFlow Project Status

## Current State
JewelFlow Version 1 is a local-demo-ready jewelry store management system with a Spring Boot backend and a React TypeScript frontend. The current branch adds the next-step JWT authentication baseline with `ADMIN` and `STAFF` roles. The frontend connects to the backend at `http://localhost:8080` and runs locally at `http://localhost:5173`.

## Project Purpose
JewelFlow helps small and mid-sized jewelry stores manage serialized jewelry inventory, customers, gold rates, pricing, invoices/orders, sales records, and store dashboard metrics.

## Current Tech Stack
- Backend framework: Spring Boot 4.0.6
- Backend language: Java 17 target, Lombok
- Backend API style: REST controllers under `/api/**`
- Backend persistence: Spring Data JPA with PostgreSQL
- Database local setup: PostgreSQL 16 via Docker Compose
- Backend build tool: Maven Wrapper (`backend/mvnw`, `backend/mvnw.cmd`)
- Frontend: React, TypeScript, Vite
- Frontend API layer: fetch with typed request/response models
- Testing: JUnit 5, Mockito, Spring Boot Test, Maven Surefire

## Current Folder Structure
- `backend/src/main/java/com/jewelflow/backend/common`: controlled enums for item status, metal type, purity, order status, payment status, and payment method.
- `backend/src/main/java/com/jewelflow/backend/auth`: application users, login DTOs, JWT issuing, and bootstrap demo accounts.
- `backend/src/main/java/com/jewelflow/backend/config`: JWT-backed Spring Security configuration.
- `backend/src/main/java/com/jewelflow/backend/customer`: customer entity, request DTO, repository, service, and controller.
- `backend/src/main/java/com/jewelflow/backend/dashboard`: dashboard summary DTOs, recent activity DTOs, service, and controller.
- `backend/src/main/java/com/jewelflow/backend/exception`: shared exception type and global REST exception handler.
- `backend/src/main/java/com/jewelflow/backend/goldrate`: gold rate entity, request/response DTOs, repository, service, and controller.
- `backend/src/main/java/com/jewelflow/backend/inventory`: jewelry item entity, request DTO, repository, service, and controller.
- `backend/src/main/java/com/jewelflow/backend/invoice`: invoice/order entity, request/response DTOs, repository, service, and controller.
- `backend/src/main/java/com/jewelflow/backend/pricing`: pricing request/response DTOs, pricing service, and controller.
- `backend/src/main/java/com/jewelflow/backend/sales`: sale entity, request/response DTOs, repository, service, and controller.
- `backend/src/test/java/com/jewelflow/backend`: backend context and focused service tests.
- `frontend/src/api`: frontend API client and JewelFlow endpoint functions.
- `frontend/src/components`: reusable UI components.
- `frontend/src/pages`: dashboard, table, and form pages.
- `frontend/src/types`: TypeScript DTOs matching backend API models.
- `frontend/src/utils`: formatting helpers.
- `infra`: Docker Compose file for local PostgreSQL.

## Version 1 Completed Features
- React + TypeScript + Vite frontend module.
- Sidebar navigation and top header.
- Dashboard page with customer, inventory, availability, sold, revenue, billing count, inventory value, recent invoice, and recent sale views.
- Inventory table with filters by status, category, metal type, purity, and keyword.
- Add/edit inventory item forms with client-side required-field and weight validation.
- Customer table with keyword search.
- Add/edit customer forms.
- Gold rate table and create-rate form.
- Pricing calculator page.
- Invoice/order table with customer, payment status, order status, and keyword filters.
- Create invoice page using customers and available inventory items.
- Invoice creation marks the selected item as `SOLD`.
- Sales page for the existing sales flow.
- Frontend loading states, empty states, and backend error display.
- `frontend/.env.example` with `VITE_API_BASE_URL=http://localhost:8080`.
- Backend inventory, pricing, customer, sales, invoice, gold-rate, and dashboard APIs.
- Controlled enum validation for item status, metal type, purity, payment status, payment method, and order status.
- Inventory cross-field validation for impossible weight combinations.
- Pricing rejects discounts greater than subtotal before tax.
- Backend search/filter support for inventory, customers, invoices, and sales.
- Safer invoice/order numbers and sale numbers using timestamp plus random suffix instead of `repository.count() + 1`.
- Dashboard summary includes invoice/order revenue and counts in addition to legacy sales data.
- Shared `ResourceNotFoundException`.
- Shared `GlobalExceptionHandler` for 404, bad request, and validation responses.
- JWT login with bootstrap `ADMIN` and `STAFF` users.
- Backend APIs are protected by default after login.
- Customer and inventory deletes require the `ADMIN` role.
- Frontend login screen, token persistence, protected routing, bearer-token requests, and logout.

## Current API Endpoints

| Method | Endpoint path | Purpose |
| --- | --- | --- |
| `POST` | `/api/auth/login` | Authenticate and issue JWT |
| `GET` | `/api/auth/me` | Get current authenticated user |
| `POST` | `/api/items` | Create inventory item and calculate pricing |
| `GET` | `/api/items?status=&category=&metalType=&purity=&keyword=` | List/filter inventory items |
| `GET` | `/api/items/{id}` | Get inventory item |
| `PUT` | `/api/items/{id}` | Update inventory item and recalculate pricing |
| `DELETE` | `/api/items/{id}` | Delete inventory item |
| `POST` | `/api/pricing/calculate` | Calculate gold value, subtotal, tax, and final price |
| `POST` | `/api/customers` | Create customer |
| `GET` | `/api/customers?keyword=` | List/search customers |
| `GET` | `/api/customers/{id}` | Get customer |
| `PUT` | `/api/customers/{id}` | Update customer |
| `DELETE` | `/api/customers/{id}` | Delete customer |
| `POST` | `/api/gold-rates` | Create gold rate history record |
| `GET` | `/api/gold-rates?metalType=&purity=` | List gold rates, optionally filtered by metal and purity |
| `GET` | `/api/gold-rates/latest?metalType=Gold&purity=22K` | Get latest rate |
| `GET` | `/api/gold-rates/{id}` | Get gold rate |
| `POST` | `/api/invoices` | Create invoice/order and mark item sold |
| `GET` | `/api/invoices?customerName=&paymentStatus=&orderStatus=&keyword=` | List/filter invoices/orders |
| `GET` | `/api/invoices/{id}` | Get invoice/order |
| `POST` | `/api/sales` | Create legacy sale record and mark item sold |
| `GET` | `/api/sales?customerName=&paymentStatus=&keyword=` | List/filter sales |
| `GET` | `/api/sales/{id}` | Get sale |
| `GET` | `/api/dashboard/summary` | Get dashboard metrics and recent activity |

## Version 1 Browser Flow
1. Start PostgreSQL with Docker Compose.
2. Start Spring Boot backend.
3. Start React frontend.
4. Open `http://localhost:5173`.
5. Sign in with a bootstrap demo user.
6. Create a gold rate.
7. Create a customer.
8. Create an inventory item and confirm calculated pricing.
9. Create an invoice/order for that customer and item.
10. Confirm the item status changes from `AVAILABLE` to `SOLD`.
11. Confirm dashboard revenue, billing count, and recent invoices update.

## Configuration Notes
- Backend default URL: `http://localhost:8080`
- Frontend default URL: `http://localhost:5173`
- `frontend/.env.example` sets `VITE_API_BASE_URL=http://localhost:8080`.
- Local datasource URL: `jdbc:postgresql://localhost:5432/jewelflow`
- Local datasource username: `jewelflow_user`
- Local datasource password: `jewelflow_pass`
- Hibernate uses `spring.jpa.hibernate.ddl-auto=update`.
- SQL logging is currently enabled.
- JWT secret env var: `JWT_SECRET`
- JWT expiry env var: `JWT_EXPIRATION_MINUTES`
- Bootstrap admin env vars: `JEWELFLOW_ADMIN_USERNAME`, `JEWELFLOW_ADMIN_PASSWORD`
- Bootstrap staff env vars: `JEWELFLOW_STAFF_USERNAME`, `JEWELFLOW_STAFF_PASSWORD`
- Allowed frontend origin env var: `JEWELFLOW_CORS_ALLOWED_ORIGIN`
- Default local demo users are `admin` / `AdminDemo123!` and `staff` / `StaffDemo123!`.
- Spring Security protects APIs by default; only `POST /api/auth/login` is public.
- Controllers use `@CrossOrigin(origins = "*")`.

## How to Run Locally
From the repository root:

```bash
cd infra
docker compose up -d
```

Start backend:

```bash
cd ../backend
./mvnw spring-boot:run
```

Start frontend:

```bash
cd ../frontend
npm install
npm run dev
```

Run backend tests:

```bash
cd backend
./mvnw test
```

Build frontend:

```bash
cd frontend
npm install
npm run build
```

## Commands / Tests Run
Backend verification:

```text
cd backend && ./mvnw test
BUILD SUCCESS
Tests run: 11, Failures: 0, Errors: 0, Skipped: 0
```

Frontend verification:

```text
cd frontend && npm install
added packages, audited packages, found 0 vulnerabilities

cd frontend && npm run build
vite build completed successfully
```

Browser smoke verification:

```text
Login screen loaded: true
Admin login loaded protected dashboard: true
Logout returned to /login: true
```

## Current Test Coverage
- Spring Boot context startup.
- Pricing discount ceiling.
- Inventory impossible-weight validation.
- Inventory pricing values saved from `PricingService`.
- Customer duplicate phone validation.
- Invoice creation.
- Invoice creation marks item as `SOLD`.
- Cannot invoice an already `SOLD` item.
- Invoice number generation no longer uses repository count.
- Dashboard summary combines sales and invoice/order revenue.
- Authentication login service returns JWT responses.

## Sales vs Invoices
Both flows remain because both existed in the backend. Version 1 frontend treats invoices/orders as the primary billing workflow. The sales page is read-only and supports the existing legacy sales list.

## Known Limitations
- Authentication currently uses bootstrap demo users only; there is no user-management UI, password reset, or refresh-token flow yet.
- Inventory and customer delete endpoints perform hard deletes.
- `status`, `purity`, `metalType`, `paymentStatus`, and `paymentMethod` are stored as strings in the database, with application-level enum validation.
- Invoice quantity scales pricing values, but the inventory model still represents one serialized jewelry item and does not track stock quantity decrementing.
- List endpoints have practical filtering but not full pagination contracts yet.
- Dashboard date boundaries use the server-local date.
- `ddl-auto=update` is for local demo convenience and should be replaced with migrations before production.
- User lifecycle management is not implemented yet; bootstrap credentials are the only account provisioning path.
- SQL logging is useful for local debugging but noisy for production.

## Git and .gitignore Notes
Do not commit local, generated, or sensitive files such as:
- `.env`
- `.env.*` except `.env.example`
- `frontend/node_modules/`
- `frontend/dist/`
- `frontend/*.tsbuildinfo`
- `backend/target/`
- logs
- IDE files
- local Postman workspace files under `.postman/` or `postman/`

## Next Recommended Steps
1. Replace `ddl-auto=update` with migration tooling.
2. Add user-management, password-reset, and refresh-token flows.
3. Add pagination and sorting response contracts for large datasets.
4. Decide whether to merge sales into invoices or keep sales as a separate workflow.
5. Add soft delete/archive behavior for customers and inventory.

Recommended commit message:

```text
Add JWT authentication and role-based access
```
