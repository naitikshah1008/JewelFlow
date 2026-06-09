import { useEffect, useState } from "react";
import { api } from "../api/jewelflow";
import { Alert } from "../components/Alert";
import { Card } from "../components/Card";
import { Field, SelectInput, TextInput } from "../components/FormControls";
import { Loading } from "../components/Loading";
import { PaginationControls } from "../components/PaginationControls";
import { Table } from "../components/Table";
import type { Sale } from "../types";
import { formatCurrency, formatDateTime } from "../utils/format";
import { createPageQuery, emptyPage, resetPage, toPageParams } from "../utils/pagination";

export function SalesPage() {
  const [salesPage, setSalesPage] = useState(() => emptyPage<Sale>(createPageQuery("saleDate")));
  const [pageQuery, setPageQuery] = useState(() => createPageQuery("saleDate"));
  const [filters, setFilters] = useState({
    keyword: "",
    paymentStatus: "",
    customerName: ""
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    setLoading(true);
    api
      .salesPage({ ...filters, ...toPageParams(pageQuery) })
      .then(setSalesPage)
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
      <Alert
        type="info"
        title="Legacy records"
        message="Invoices / Orders is the primary billing flow. This page keeps older sales records searchable for reference."
      />
      <Card title="Legacy Sales">
        <div className="filters-grid">
          <Field label="Search">
            <TextInput
              value={filters.keyword}
              onChange={(event) => updateFilter("keyword", event.target.value)}
              placeholder="Sale, customer, item"
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
          <Field label="Sort By">
            <SelectInput value={pageQuery.sortBy} onChange={(event) => updatePageQuery("sortBy", event.target.value)}>
              <option value="saleDate">Sale Date</option>
              <option value="invoiceNumber">Legacy Number</option>
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
        <Loading label="Loading sales" />
      ) : (
        <Card>
          <Table
            columns={["Legacy Sale", "Customer", "Item", "Amount", "Payment", "Date"]}
            empty={salesPage.content.length === 0}
            emptyTitle="No legacy sales found"
          >
            {salesPage.content.map((sale) => (
              <tr key={sale.id}>
                <td>{sale.invoiceNumber}</td>
                <td>
                  {sale.customerName}
                  <small>{sale.customerPhoneNumber}</small>
                </td>
                <td>{sale.itemName}</td>
                <td>{formatCurrency(sale.finalAmount)}</td>
                <td>
                  <span className="status-pill">{sale.paymentStatus}</span>
                </td>
                <td>{formatDateTime(sale.saleDate)}</td>
              </tr>
            ))}
          </Table>
          <PaginationControls
            page={salesPage}
            onPageChange={(page) => updatePageQuery("page", page)}
            onPageSizeChange={(size) => updatePageQuery("size", size)}
          />
        </Card>
      )}
    </div>
  );
}
