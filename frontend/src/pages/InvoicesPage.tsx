import { useEffect, useState } from "react";
import { api } from "../api/jewelflow";
import { Alert } from "../components/Alert";
import { Button } from "../components/Button";
import { Card } from "../components/Card";
import { Field, SelectInput, TextInput } from "../components/FormControls";
import { Loading } from "../components/Loading";
import { PaginationControls } from "../components/PaginationControls";
import { Table } from "../components/Table";
import type { Invoice } from "../types";
import { formatCurrency, formatDateTime } from "../utils/format";
import { createPageQuery, emptyPage, resetPage, toPageParams } from "../utils/pagination";

interface InvoicesPageProps {
  onNavigate: (path: string) => void;
}

export function InvoicesPage({ onNavigate }: InvoicesPageProps) {
  const [invoicesPage, setInvoicesPage] = useState(() => emptyPage<Invoice>(createPageQuery("invoiceDate")));
  const [pageQuery, setPageQuery] = useState(() => createPageQuery("invoiceDate"));
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
      .invoicesPage({ ...filters, ...toPageParams(pageQuery) })
      .then(setInvoicesPage)
      .catch((err: Error) => setError(err.message))
      .finally(() => setLoading(false));
  }, [filters, pageQuery]);

  function updateFilter(field: keyof typeof filters, value: string) {
    setFilters((current) => ({ ...current, [field]: value }));
    setPageQuery((current) => resetPage(current));
  }

  function updatePageQuery(field: keyof typeof pageQuery, value: string | number) {
    setPageQuery((current) => ({
      ...current,
      page: field === "page" ? Number(value) : 0,
      [field]: value
    }));
  }

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
              onChange={(event) => updateFilter("keyword", event.target.value)}
              placeholder="Invoice, customer, item"
            />
          </Field>
          <Field label="Customer">
            <TextInput
              value={filters.customerName}
              onChange={(event) => updateFilter("customerName", event.target.value)}
            />
          </Field>
          <Field label="Payment">
            <SelectInput
              value={filters.paymentStatus}
              onChange={(event) => updateFilter("paymentStatus", event.target.value)}
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
              onChange={(event) => updateFilter("orderStatus", event.target.value)}
            >
              <option value="">All</option>
              <option value="ISSUED">Issued</option>
            </SelectInput>
          </Field>
          <Field label="Sort By">
            <SelectInput value={pageQuery.sortBy} onChange={(event) => updatePageQuery("sortBy", event.target.value)}>
              <option value="invoiceDate">Invoice Date</option>
              <option value="invoiceNumber">Invoice Number</option>
              <option value="customerName">Customer</option>
              <option value="finalAmount">Amount</option>
              <option value="paymentStatus">Payment</option>
            </SelectInput>
          </Field>
          <Field label="Direction">
            <SelectInput value={pageQuery.direction} onChange={(event) => updatePageQuery("direction", event.target.value)}>
              <option value="DESC">Descending</option>
              <option value="ASC">Ascending</option>
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
            empty={invoicesPage.content.length === 0}
            emptyTitle="No invoices found"
          >
            {invoicesPage.content.map((invoice) => (
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
          <PaginationControls
            page={invoicesPage}
            onPageChange={(page) => updatePageQuery("page", page)}
            onPageSizeChange={(size) => updatePageQuery("size", size)}
          />
        </Card>
      )}
    </div>
  );
}
