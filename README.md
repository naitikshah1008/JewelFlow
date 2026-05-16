# JewelFlow

JewelFlow is a local-demo-ready jewelry store management system for inventory, customers, gold rates, pricing, invoices/orders, sales records, dashboard reporting, and authenticated store access.

Version 1 includes a Spring Boot backend and a React TypeScript dashboard frontend connected to `http://localhost:8080`. The current branch adds the next-step JWT authentication baseline for `ADMIN` and `STAFF` users.

## Tech Stack
- Backend: Java 17, Spring Boot 4, Spring Data JPA, Hibernate, PostgreSQL, Maven Wrapper
- Frontend: React, TypeScript, Vite, fetch API
- Database: PostgreSQL 16 through Docker Compose
- Validation: Jakarta Bean Validation plus service-level business rules
- Testing: JUnit 5, Mockito, Spring Boot Test

## Folder Structure
- `backend/` - Spring Boot API application
- `backend/src/main/java/com/jewelflow/backend/auth` - users, login, JWT issuing, and bootstrap demo accounts
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

Backend authentication environment variables:

```bash
JWT_SECRET=replace-with-at-least-32-characters
JWT_EXPIRATION_MINUTES=480
JEWELFLOW_ADMIN_USERNAME=admin
JEWELFLOW_ADMIN_PASSWORD=change_me
JEWELFLOW_STAFF_USERNAME=staff
JEWELFLOW_STAFF_PASSWORD=change_me
JEWELFLOW_CORS_ALLOWED_ORIGIN=http://localhost:5173
```

Local defaults create two demo users when they do not already exist:
- `admin` / `AdminDemo123!`
- `staff` / `StaffDemo123!`

Override those credentials before using the app outside a local demo environment.

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

## Authentication Baseline
- JWT login endpoint at `POST /api/auth/login`
- Authenticated current-user endpoint at `GET /api/auth/me`
- Protected backend APIs by default
- `ADMIN`-only deletes for customers and inventory items
- Frontend login screen, protected routes, token persistence, bearer-token API requests, and logout

## Postman Notes
Postman remains useful for direct API checks. Use base URL:

```text
http://localhost:8080
```

Recommended manual flow:
1. `POST /api/auth/login`
2. Copy the JWT into the `Authorization: Bearer <token>` header
3. `POST /api/gold-rates`
4. `POST /api/customers`
5. `POST /api/items`
6. `POST /api/invoices`
7. `GET /api/dashboard/summary`

## Known Limitations
- Authentication is limited to bootstrap demo users; there is no user-management UI, password reset flow, or refresh-token flow yet.
- Inventory and customer deletes are still hard deletes.
- Invoice and sales flows both exist; V1 frontend uses invoices/orders as the main billing flow and keeps sales read-only.
- Inventory still models serialized jewelry items rather than stock quantity decrementing.
- Hibernate `ddl-auto=update` is convenient locally but should be replaced by migrations before production.
- List endpoints support practical filtering but not full pagination yet.

## Next Steps
- Add production migrations.
- Add user management, password reset, and refresh-token support.
- Introduce soft deletes or archive flows.
- Add pagination and sorting contracts for larger datasets.
- Decide whether sales should be merged into invoices or kept as a separate retail-sale workflow.
