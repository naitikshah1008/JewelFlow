import { useEffect, useState } from "react";
import { api } from "../api/jewelflow";
import { Alert } from "../components/Alert";
import { Card } from "../components/Card";
import { Loading } from "../components/Loading";
import { Table } from "../components/Table";
import type { DashboardSummary } from "../types";
import { formatCurrency, formatDateTime, formatNumber } from "../utils/format";

interface DashboardPageProps {
  onNavigate: (path: string) => void;
}

export function DashboardPage({ onNavigate }: DashboardPageProps) {
  const [summary, setSummary] = useState<DashboardSummary | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    api
      .dashboard()
      .then(setSummary)
      .catch((err: Error) => setError(err.message))
      .finally(() => setLoading(false));
  }, []);

  if (loading) {
    return <Loading label="Loading dashboard" />;
  }

  if (error) {
    return <Alert type="error" title="Dashboard unavailable" message={error} />;
  }

  if (!summary) {
    return null;
  }

  const cards = [
    ["Customers", formatNumber(summary.totalCustomers, 0), "/customers"],
    ["Inventory Items", formatNumber(summary.totalInventoryItems, 0), "/inventory"],
    ["Available", formatNumber(summary.availableItems, 0), "/inventory"],
    ["Sold", formatNumber(summary.soldItems, 0), "/inventory"],
    ["Revenue", formatCurrency(summary.totalRevenue), "/invoices"],
    ["Invoices / Sales", formatNumber(summary.totalBillingCount, 0), "/invoices"]
  ];

  return (
    <div className="page-stack">
      <div className="metric-grid">
        {cards.map(([label, value, path]) => (
          <button key={label} type="button" className="metric-card" onClick={() => onNavigate(path)}>
            <span>{label}</span>
            <strong>{value}</strong>
          </button>
        ))}
      </div>

      <div className="two-column">
        <Card title="Recent Invoices">
          <Table
            columns={["Invoice", "Customer", "Item", "Amount", "Status", "Date"]}
            empty={summary.recentInvoices.length === 0}
            emptyTitle="No invoices yet"
          >
            {summary.recentInvoices.map((invoice) => (
              <tr key={invoice.id}>
                <td>{invoice.invoiceNumber}</td>
                <td>{invoice.customerName}</td>
                <td>{invoice.itemName}</td>
                <td>{formatCurrency(invoice.finalAmount)}</td>
                <td>
                  <span className="status-pill">{invoice.paymentStatus}</span>
                </td>
                <td>{formatDateTime(invoice.invoiceDate)}</td>
              </tr>
            ))}
          </Table>
        </Card>

        <Card title="Inventory Value">
          <div className="summary-list">
            <div>
              <span>Active stock value</span>
              <strong>{formatCurrency(summary.activeInventoryValue)}</strong>
            </div>
            <div>
              <span>Available value</span>
              <strong>{formatCurrency(summary.availableInventoryValue)}</strong>
            </div>
            <div>
              <span>Reserved value</span>
              <strong>{formatCurrency(summary.reservedInventoryValue)}</strong>
            </div>
            <div>
              <span>Invoice revenue</span>
              <strong>{formatCurrency(summary.totalInvoiceRevenue)}</strong>
            </div>
          </div>
        </Card>
      </div>

      <Card title="Recent Sales">
        <Table
          columns={["Sale", "Customer", "Item", "Amount", "Status", "Date"]}
          empty={summary.recentSales.length === 0}
          emptyTitle="No sales yet"
        >
          {summary.recentSales.map((sale) => (
            <tr key={sale.id}>
              <td>{sale.invoiceNumber}</td>
              <td>{sale.customerName}</td>
              <td>{sale.itemName}</td>
              <td>{formatCurrency(sale.finalAmount)}</td>
              <td>
                <span className="status-pill">{sale.paymentStatus}</span>
              </td>
              <td>{formatDateTime(sale.saleDate)}</td>
            </tr>
          ))}
        </Table>
      </Card>
    </div>
  );
}
