import { useEffect, useState } from "react";
import { api } from "../api/jewelflow";
import { Alert } from "../components/Alert";
import { Button } from "../components/Button";
import { Card } from "../components/Card";
import { Field, SelectInput, TextInput } from "../components/FormControls";
import { Loading } from "../components/Loading";
import { Table } from "../components/Table";
import type { Invoice } from "../types";
import { formatCurrency, formatDateTime } from "../utils/format";

interface InvoicesPageProps {
  onNavigate: (path: string) => void;
}

export function InvoicesPage({ onNavigate }: InvoicesPageProps) {
  const [invoices, setInvoices] = useState<Invoice[]>([]);
  const [filters, setFilters] = useState({
    keyword: "",
    paymentStatus: "",
    orderStatus: "",
    customerName: ""
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    setLoading(true);
    api
      .invoices(filters)
      .then(setInvoices)
      .catch((err: Error) => setError(err.message))
      .finally(() => setLoading(false));
  }, [filters]);

  return (
    <div className="page-stack">
      <Card
        title="Invoices / Orders"
        action={<Button onClick={() => onNavigate("/invoices/new")}>Create Invoice</Button>}
      >
        <div className="filters-grid">
          <Field label="Search">
            <TextInput
              value={filters.keyword}
              onChange={(event) => setFilters({ ...filters, keyword: event.target.value })}
              placeholder="Invoice, customer, item"
            />
          </Field>
          <Field label="Customer">
            <TextInput
              value={filters.customerName}
              onChange={(event) => setFilters({ ...filters, customerName: event.target.value })}
            />
          </Field>
          <Field label="Payment">
            <SelectInput
              value={filters.paymentStatus}
              onChange={(event) => setFilters({ ...filters, paymentStatus: event.target.value })}
            >
              <option value="">All</option>
              <option value="PAID">Paid</option>
              <option value="UNPAID">Unpaid</option>
              <option value="PARTIAL">Partial</option>
            </SelectInput>
          </Field>
          <Field label="Order">
            <SelectInput
              value={filters.orderStatus}
              onChange={(event) => setFilters({ ...filters, orderStatus: event.target.value })}
            >
              <option value="">All</option>
              <option value="ISSUED">Issued</option>
            </SelectInput>
          </Field>
        </div>
      </Card>

      {error && <Alert type="error" message={error} />}
      {loading ? (
        <Loading label="Loading invoices" />
      ) : (
        <Card>
          <Table
            columns={["Invoice", "Customer", "Item", "Amount", "Payment", "Date"]}
            empty={invoices.length === 0}
            emptyTitle="No invoices found"
          >
            {invoices.map((invoice) => (
              <tr key={invoice.id}>
                <td>
                  <strong>{invoice.invoiceNumber}</strong>
                  <small>{invoice.orderStatus}</small>
                </td>
                <td>
                  {invoice.customerName}
                  <small>{invoice.customerPhoneNumber}</small>
                </td>
                <td>
                  {invoice.itemName}
                  <small>
                    {invoice.metalType} {invoice.purity}
                  </small>
                </td>
                <td>{formatCurrency(invoice.finalAmount)}</td>
                <td>
                  <span className="status-pill">{invoice.paymentStatus}</span>
                </td>
                <td>{formatDateTime(invoice.invoiceDate)}</td>
              </tr>
            ))}
          </Table>
        </Card>
      )}
    </div>
  );
}
