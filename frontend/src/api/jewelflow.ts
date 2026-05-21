import { apiRequest } from "./client";
import type {
  Customer,
  CustomerRequest,
  CurrentUser,
  DashboardSummary,
  GoldRate,
  GoldRateRequest,
  AuthResponse,
  Invoice,
  InvoiceRequest,
  JewelleryItem,
  JewelleryItemRequest,
  LoginRequest,
  LogoutRequest,
  PageResponse,
  PricingRequest,
  PricingResponse,
  ResetPasswordRequest,
  Sale,
  UpdateUserRequest,
  UserAccount,
  CreateUserRequest
} from "../types";

export const api = {
  login: (body: LoginRequest) =>
    apiRequest<AuthResponse>("/api/auth/login", { method: "POST", body: JSON.stringify(body) }),
  logout: (body: LogoutRequest) =>
    apiRequest<void>("/api/auth/logout", { method: "POST", body: JSON.stringify(body) }),
  currentUser: () => apiRequest<CurrentUser>("/api/auth/me"),

  users: () => apiRequest<UserAccount[]>("/api/users"),
  usersPage: (params?: Record<string, string | number>) => apiRequest<PageResponse<UserAccount>>("/api/users/page", {}, params),
  createUser: (body: CreateUserRequest) =>
    apiRequest<UserAccount>("/api/users", { method: "POST", body: JSON.stringify(body) }),
  updateUser: (id: number, body: UpdateUserRequest) =>
    apiRequest<UserAccount>(`/api/users/${id}`, { method: "PUT", body: JSON.stringify(body) }),
  resetUserPassword: (id: number, body: ResetPasswordRequest) =>
    apiRequest<UserAccount>(`/api/users/${id}/reset-password`, { method: "POST", body: JSON.stringify(body) }),

  dashboard: () => apiRequest<DashboardSummary>("/api/dashboard/summary"),

  items: (params?: Record<string, string>) => apiRequest<JewelleryItem[]>("/api/items", {}, params),
  itemsPage: (params?: Record<string, string | number>) => apiRequest<PageResponse<JewelleryItem>>("/api/items/page", {}, params),
  item: (id: number) => apiRequest<JewelleryItem>(`/api/items/${id}`),
  createItem: (body: JewelleryItemRequest) =>
    apiRequest<JewelleryItem>("/api/items", { method: "POST", body: JSON.stringify(body) }),
  updateItem: (id: number, body: JewelleryItemRequest) =>
    apiRequest<JewelleryItem>(`/api/items/${id}`, { method: "PUT", body: JSON.stringify(body) }),

  customers: (keyword?: string) => apiRequest<Customer[]>("/api/customers", {}, { keyword }),
  customersPage: (params?: Record<string, string | number>) =>
    apiRequest<PageResponse<Customer>>("/api/customers/page", {}, params),
  customer: (id: number) => apiRequest<Customer>(`/api/customers/${id}`),
  createCustomer: (body: CustomerRequest) =>
    apiRequest<Customer>("/api/customers", { method: "POST", body: JSON.stringify(body) }),
  updateCustomer: (id: number, body: CustomerRequest) =>
    apiRequest<Customer>(`/api/customers/${id}`, { method: "PUT", body: JSON.stringify(body) }),

  goldRates: (params?: Record<string, string>) => apiRequest<GoldRate[]>("/api/gold-rates", {}, params),
  goldRatesPage: (params?: Record<string, string | number>) =>
    apiRequest<PageResponse<GoldRate>>("/api/gold-rates/page", {}, params),
  createGoldRate: (body: GoldRateRequest) =>
    apiRequest<GoldRate>("/api/gold-rates", { method: "POST", body: JSON.stringify(body) }),

  calculatePrice: (body: PricingRequest) =>
    apiRequest<PricingResponse>("/api/pricing/calculate", { method: "POST", body: JSON.stringify(body) }),

  invoices: (params?: Record<string, string>) => apiRequest<Invoice[]>("/api/invoices", {}, params),
  invoicesPage: (params?: Record<string, string | number>) =>
    apiRequest<PageResponse<Invoice>>("/api/invoices/page", {}, params),
  invoice: (id: number) => apiRequest<Invoice>(`/api/invoices/${id}`),
  createInvoice: (body: InvoiceRequest) =>
    apiRequest<Invoice>("/api/invoices", { method: "POST", body: JSON.stringify(body) }),

  sales: (params?: Record<string, string>) => apiRequest<Sale[]>("/api/sales", {}, params),
  salesPage: (params?: Record<string, string | number>) => apiRequest<PageResponse<Sale>>("/api/sales/page", {}, params),
  sale: (id: number) => apiRequest<Sale>(`/api/sales/${id}`)
};
