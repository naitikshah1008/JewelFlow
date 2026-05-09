export type ItemStatus = "AVAILABLE" | "RESERVED" | "SOLD";
export type MetalType = "GOLD" | "SILVER" | "PLATINUM";
export type Purity = "24K" | "22K" | "18K" | "14K";
export type PaymentStatus = "PAID" | "UNPAID" | "PARTIAL";
export type PaymentMethod = "CASH" | "CARD" | "UPI" | "BANK_TRANSFER" | "OTHER";
export type OrderStatus = "ISSUED";

export interface ApiErrorBody {
  timestamp?: string;
  status?: number;
  error?: string;
  message?: string;
  fieldErrors?: Record<string, string>;
}

export interface JewelleryItem {
  id: number;
  itemName: string;
  category: string;
  metalType: MetalType;
  purity: Purity;
  grossWeight: number;
  netWeight: number;
  stoneWeight: number;
  goldRatePerGram: number;
  stonePrice: number;
  makingCharges: number;
  taxPercentage: number;
  discount: number;
  goldValue: number;
  taxAmount: number;
  purchaseCost: number;
  sellingPrice: number;
  status: ItemStatus;
  createdAt?: string;
  updatedAt?: string;
}

export interface JewelleryItemRequest {
  itemName: string;
  category: string;
  metalType: string;
  purity: string;
  grossWeight: number;
  netWeight: number;
  stoneWeight: number;
  goldRatePerGram?: number;
  stonePrice: number;
  makingCharges: number;
  taxPercentage: number;
  discount: number;
  purchaseCost: number;
  status?: string;
}

export interface Customer {
  id: number;
  fullName: string;
  phoneNumber: string;
  email?: string;
  address?: string;
  city?: string;
  state?: string;
  postalCode?: string;
  notes?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface CustomerRequest {
  fullName: string;
  phoneNumber: string;
  email?: string;
  address?: string;
  city?: string;
  state?: string;
  postalCode?: string;
  notes?: string;
}

export interface GoldRate {
  id: number;
  metalType: MetalType;
  purity: Purity;
  ratePerGram: number;
  rateDate: string;
  source?: string;
  notes?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface GoldRateRequest {
  metalType: string;
  purity: string;
  ratePerGram: number;
  rateDate: string;
  source?: string;
  notes?: string;
}

export interface PricingRequest {
  netWeight: number;
  metalType?: string;
  purity: string;
  goldRatePerGram?: number;
  stonePrice: number;
  makingCharges: number;
  taxPercentage: number;
  discount: number;
}

export interface PricingResponse {
  purityFactor: number;
  goldRatePerGram: number;
  goldValue: number;
  stonePrice: number;
  makingCharges: number;
  subtotal: number;
  taxAmount: number;
  discount: number;
  finalPrice: number;
}

export interface InvoiceRequest {
  customerId: number;
  itemId: number;
  quantity: number;
  taxPercentage: number;
  discount: number;
  paymentStatus: string;
  paymentMethod: string;
  notes?: string;
}

export interface Invoice {
  id: number;
  invoiceNumber: string;
  customerId: number;
  customerName: string;
  customerPhoneNumber: string;
  itemId: number;
  itemName: string;
  category: string;
  metalType: MetalType;
  purity: Purity;
  quantity: number;
  grossWeight: number;
  netWeight: number;
  stoneWeight: number;
  goldRatePerGram: number;
  goldValue: number;
  stonePrice: number;
  makingCharges: number;
  subtotal: number;
  taxPercentage: number;
  taxAmount: number;
  discount: number;
  unitFinalAmount: number;
  finalAmount: number;
  orderStatus: OrderStatus;
  paymentStatus: PaymentStatus;
  paymentMethod: PaymentMethod;
  notes?: string;
  invoiceDate?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface Sale {
  id: number;
  invoiceNumber: string;
  customerId: number;
  customerName: string;
  customerPhoneNumber: string;
  itemId: number;
  itemName: string;
  category: string;
  metalType: MetalType;
  purity: Purity;
  grossWeight: number;
  netWeight: number;
  stoneWeight: number;
  goldRatePerGram: number;
  goldValue: number;
  stonePrice: number;
  makingCharges: number;
  taxPercentage: number;
  taxAmount: number;
  discount: number;
  finalAmount: number;
  paymentStatus: PaymentStatus;
  paymentMethod: PaymentMethod;
  notes?: string;
  saleDate?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface RecentInvoice {
  id: number;
  invoiceNumber: string;
  customerName: string;
  itemName: string;
  finalAmount: number;
  paymentStatus: PaymentStatus;
  orderStatus: OrderStatus;
  invoiceDate?: string;
}

export interface RecentSale {
  id: number;
  invoiceNumber: string;
  customerName: string;
  itemName: string;
  finalAmount: number;
  paymentStatus: PaymentStatus;
  saleDate?: string;
}

export interface DashboardSummary {
  totalCustomers: number;
  totalInventoryItems: number;
  availableItems: number;
  reservedItems: number;
  soldItems: number;
  activeInventoryValue: number;
  availableInventoryValue: number;
  reservedInventoryValue: number;
  totalSalesCount: number;
  totalInvoicesCount: number;
  totalBillingCount: number;
  totalRevenue: number;
  totalSalesRevenue: number;
  totalInvoiceRevenue: number;
  todaySalesCount: number;
  todayInvoicesCount: number;
  todayRevenue: number;
  todaySalesRevenue: number;
  todayInvoiceRevenue: number;
  monthlySalesCount: number;
  monthlyInvoicesCount: number;
  monthlyRevenue: number;
  monthlySalesRevenue: number;
  monthlyInvoiceRevenue: number;
  recentSales: RecentSale[];
  recentInvoices: RecentInvoice[];
}
