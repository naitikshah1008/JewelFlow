import { useEffect, useMemo, useState } from "react";
import { Layout, type NavItem } from "./components/Layout";
import { CreateInvoicePage } from "./pages/CreateInvoicePage";
import { CustomerFormPage } from "./pages/CustomerFormPage";
import { CustomersPage } from "./pages/CustomersPage";
import { DashboardPage } from "./pages/DashboardPage";
import { GoldRatesPage } from "./pages/GoldRatesPage";
import { InventoryFormPage } from "./pages/InventoryFormPage";
import { InventoryPage } from "./pages/InventoryPage";
import { InvoicesPage } from "./pages/InvoicesPage";
import { NotFoundPage } from "./pages/NotFoundPage";
import { PricingCalculatorPage } from "./pages/PricingCalculatorPage";
import { SalesPage } from "./pages/SalesPage";

const navItems: NavItem[] = [
  { path: "/", label: "Dashboard" },
  { path: "/inventory", label: "Inventory" },
  { path: "/customers", label: "Customers" },
  { path: "/gold-rates", label: "Gold Rates" },
  { path: "/pricing", label: "Pricing" },
  { path: "/invoices", label: "Invoices" },
  { path: "/sales", label: "Sales" }
];

function getPathTitle(path: string): string {
  if (path === "/") return "Dashboard";
  if (path.startsWith("/inventory/new")) return "Add Inventory Item";
  if (path.startsWith("/inventory/")) return "Edit Inventory Item";
  if (path.startsWith("/inventory")) return "Inventory Items";
  if (path.startsWith("/customers/new")) return "Add Customer";
  if (path.startsWith("/customers/")) return "Edit Customer";
  if (path.startsWith("/customers")) return "Customers";
  if (path.startsWith("/gold-rates")) return "Gold Rates";
  if (path.startsWith("/pricing")) return "Pricing Calculator";
  if (path.startsWith("/invoices/new")) return "Create Invoice";
  if (path.startsWith("/invoices")) return "Invoices / Orders";
  if (path.startsWith("/sales")) return "Sales";
  return "Not Found";
}

function parseId(path: string, prefix: string): number | undefined {
  const match = path.match(new RegExp(`^${prefix}/(\\d+)/edit$`));
  return match ? Number(match[1]) : undefined;
}

export function App() {
  const [path, setPath] = useState(window.location.pathname);

  useEffect(() => {
    const handlePop = () => setPath(window.location.pathname);
    window.addEventListener("popstate", handlePop);
    return () => window.removeEventListener("popstate", handlePop);
  }, []);

  function navigate(nextPath: string) {
    window.history.pushState({}, "", nextPath);
    setPath(nextPath);
  }

  const title = useMemo(() => getPathTitle(path), [path]);
  const inventoryEditId = parseId(path, "/inventory");
  const customerEditId = parseId(path, "/customers");

  let page;
  if (path === "/") page = <DashboardPage onNavigate={navigate} />;
  else if (path === "/inventory") page = <InventoryPage onNavigate={navigate} />;
  else if (path === "/inventory/new") page = <InventoryFormPage onNavigate={navigate} />;
  else if (inventoryEditId) page = <InventoryFormPage itemId={inventoryEditId} onNavigate={navigate} />;
  else if (path === "/customers") page = <CustomersPage onNavigate={navigate} />;
  else if (path === "/customers/new") page = <CustomerFormPage onNavigate={navigate} />;
  else if (customerEditId) page = <CustomerFormPage customerId={customerEditId} onNavigate={navigate} />;
  else if (path === "/gold-rates") page = <GoldRatesPage />;
  else if (path === "/pricing") page = <PricingCalculatorPage />;
  else if (path === "/invoices") page = <InvoicesPage onNavigate={navigate} />;
  else if (path === "/invoices/new") page = <CreateInvoicePage onNavigate={navigate} />;
  else if (path === "/sales") page = <SalesPage />;
  else page = <NotFoundPage onNavigate={navigate} />;

  return (
    <Layout path={path} title={title} navItems={navItems} onNavigate={navigate}>
      {page}
    </Layout>
  );
}
