# JewelFlow

JewelFlow is a local-demo-ready jewelry store management system for inventory, customers, gold rates, pricing, invoices/orders, sales records, and dashboard reporting.

Version 1 includes a Spring Boot backend and a React TypeScript dashboard frontend connected to `http://localhost:8080`.

## Tech Stack
- Backend: Java 17, Spring Boot 4, Spring Data JPA, Hibernate, PostgreSQL, Maven Wrapper
- Frontend: React, TypeScript, Vite, fetch API
- Database: PostgreSQL 16 through Docker Compose
- Validation: Jakarta Bean Validation plus service-level business rules
- Testing: JUnit 5, Mockito, Spring Boot Test

## Folder Structure
- `backend/` - Spring Boot API application
- `frontend/` - React + TypeScript + Vite dashboard
- `infra/` - Docker Compose PostgreSQL setup
- `PROJECT_STATUS.md` - detailed implementation state and known limitations

Frontend source folders:
- `frontend/src/api` - typed API client and JewelFlow service functions
- `frontend/src/components` - reusable UI controls
- `frontend/src/pages` - dashboard, list, and form pages
- `frontend/src/types` - TypeScript DTOs matching backend responses/requests
- `frontend/src/utils` - formatting helpers

## Run Locally
Start PostgreSQL:

```bash
cd infra
docker compose up -d
```

Start the backend:

```bash
cd ../backend
./mvnw spring-boot:run
```

Start the frontend:

```bash
cd ../frontend
npm install
npm run dev
```

Backend API base URL:
- `http://localhost:8080`

Frontend URL:
- `http://localhost:5173`

Frontend environment example:

```bash
cp frontend/.env.example frontend/.env
```

Do not commit local `.env` files.

## Test
Backend:

```bash
cd backend
./mvnw test
```

Frontend:

```bash
cd frontend
npm install
npm run build
```

## Version 1 Features
- Dashboard cards for customers, inventory, availability, sold items, revenue, and billing count
- Recent invoice and sales activity
- Inventory list with filters and add/edit forms
- Customer list with search and add/edit forms
- Gold rate list and rate creation form
- Pricing calculator using backend pricing rules
- Invoice/order list with filters
- Create invoice flow that marks the selected inventory item as `SOLD`
- Read-only sales list for the existing sales flow
- Loading, empty, and backend error states
- Backend search/filter support for inventory, customers, invoices, and sales
- Safer invoice and sale numbers using timestamp plus random suffix instead of `count() + 1`
- Dashboard revenue includes invoices/orders and existing sales

## Postman Notes
Postman remains useful for direct API checks. Use base URL:

```text
http://localhost:8080
```

Recommended manual flow:
1. `POST /api/gold-rates`
2. `POST /api/customers`
3. `POST /api/items`
4. `POST /api/invoices`
5. `GET /api/dashboard/summary`

## Known Limitations
- Local V1 has no login, JWT, or role-based authorization.
- Inventory and customer deletes are still hard deletes.
- Invoice and sales flows both exist; V1 frontend uses invoices/orders as the main billing flow and keeps sales read-only.
- Inventory still models serialized jewelry items rather than stock quantity decrementing.
- Hibernate `ddl-auto=update` is convenient locally but should be replaced by migrations before production.
- List endpoints support practical filtering but not full pagination yet.

## Next Steps
- Add production migrations.
- Add authentication and role-based authorization.
- Introduce soft deletes or archive flows.
- Add pagination and sorting contracts for larger datasets.
- Decide whether sales should be merged into invoices or kept as a separate retail-sale workflow.
