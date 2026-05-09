# JewelFlow v1.0.0 - Demo MVP

## Overview

JewelFlow is a local-first jewelry store management system for tracking customers, inventory, gold rates, pricing, invoices/orders, sales history, and dashboard metrics. Version 1.0.0 is a demo-ready MVP that connects a Spring Boot backend to a React TypeScript frontend for an end-to-end browser workflow.

## Version 1 Features

- React + TypeScript + Vite frontend dashboard.
- Spring Boot REST API for customers, inventory, gold rates, pricing, invoices, sales, and dashboard summaries.
- PostgreSQL database via Docker Compose.
- Customer, inventory, gold rate, pricing, invoice/order, and sales list screens.
- Add/edit forms for customers and inventory items.
- Invoice/order creation flow that marks invoiced inventory items as `SOLD`.
- Dashboard totals for customers, inventory, available items, sold items, revenue, invoices, sales, and recent activity.
- Client-side validation, loading states, backend error messages, empty states, and responsive layout.

## Backend Features

- Java 17 Spring Boot backend with Maven Wrapper.
- PostgreSQL persistence through Spring Data JPA.
- Controller, service, repository layering.
- DTO validation and centralized exception handling.
- Centralized jewelry price calculation in `PricingService`.
- Inventory filters by status, category, metal type, purity, and keyword.
- Customer search by name, phone, or email keyword.
- Invoice filters by customer name, payment status, order status, and keyword.
- Sales filters by customer name, payment method, date range, and keyword.
- Safer invoice and sale number generation using timestamp and random suffixes instead of `count() + 1`.
- Focused backend tests for duplicate customer phone validation, pricing, discount validation, invoice creation, sold-item protection, and dashboard summary calculations.

## Frontend Dashboard Features

- Sidebar navigation and top header.
- Dashboard summary cards and recent activity.
- Inventory, customer, gold rate, invoice/order, and sales tables.
- Inventory and customer create/edit forms.
- Gold rate creation form.
- Pricing calculator backed by the API.
- Invoice/order creation flow using existing customers and available inventory.
- Reusable UI components for buttons, cards, alerts, form controls, loading states, empty states, and tables.
- Environment-based API URL configuration.

## Local Setup

Start PostgreSQL:

```bash
cd infra
docker compose up -d
```

Run the backend:

```bash
cd backend
./mvnw spring-boot:run
```

Run the frontend:

```bash
cd frontend
npm install
npm run dev
```

Run backend tests:

```bash
cd backend
./mvnw test
```

Build the frontend:

```bash
cd frontend
npm install
npm run build
```

## URLs And Environment

- Backend API: `http://localhost:8080`
- Frontend app: `http://localhost:5173`
- Frontend environment example: `frontend/.env.example`

Required frontend environment variable:

```bash
VITE_API_BASE_URL=http://localhost:8080
```

Default backend database settings are defined in `backend/src/main/resources/application.yml` and match `infra/docker-compose.yml`:

- Database: `jewelflow`
- User: `jewelflow_user`
- Password: `jewelflow_pass`
- Port: `5432`

## Demo Flow

1. Start PostgreSQL with Docker Compose.
2. Start the Spring Boot backend.
3. Start the React frontend.
4. Open `http://localhost:5173`.
5. Create a gold rate.
6. Create a customer.
7. Create an inventory item and confirm calculated pricing.
8. Create an invoice/order for the customer and available item.
9. Confirm the item status changes from `AVAILABLE` to `SOLD`.
10. Return to the dashboard and confirm totals, revenue, invoice count, and recent activity update.

## Release Verification

Verified for this release:

```bash
cd backend
./mvnw test
```

Result: 10 tests passed.

```bash
cd frontend
npm install
npm run build
```

Result: dependencies were up to date and the production build completed successfully.

## Known Limitations

- No JWT or role-based authentication; Version 1 is intended for local demo access.
- Deletes are still hard deletes.
- Sales and invoices remain separate flows; invoices/orders are the primary billing path in the frontend.
- List endpoints support filtering, but not full pagination across all resources.
- Inventory does not model stock quantities or partial item sales.
- Backend uses `spring.jpa.hibernate.ddl-auto=update`, which is convenient for demos but should be replaced with migrations before production.
- Spring Security still emits a generated development password even though the API is configured for local demo access.
- SQL logging is enabled for development visibility.

## Next Planned Improvements

- Add real authentication and role-based access.
- Introduce database migrations with Flyway or Liquibase.
- Add pagination and sorting consistently across all list endpoints.
- Add soft deletes and audit history.
- Clarify and potentially merge overlapping sales and invoice workflows.
- Add inventory stock quantity support where appropriate.
- Expand backend integration tests and add frontend component or end-to-end tests.
- Add production deployment configuration.
