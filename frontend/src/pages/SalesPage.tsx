import { useEffect, useState } from "react";
import { api } from "../api/jewelflow";
import { Alert } from "../components/Alert";
import { Card } from "../components/Card";
import { Field, SelectInput, TextInput } from "../components/FormControls";
import { Loading } from "../components/Loading";
import { Table } from "../components/Table";
import type { Sale } from "../types";
import { formatCurrency, formatDateTime } from "../utils/format";

export function SalesPage() {
  const [sales, setSales] = useState<Sale[]>([]);
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
      .sales(filters)
      .then(setSales)
      .catch((err: Error) => setError(err.message))
      .finally(() => setLoading(false));
  }, [filters]);

  return (
    <div className="page-stack">
      <Card title="Sales">
        <div className="filters-grid">
          <Field label="Search">
            <TextInput
              value={filters.keyword}
              onChange={(event) => setFilters({ ...filters, keyword: event.target.value })}
              placeholder="Sale, customer, item"
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
        </div>
      </Card>

      {error && <Alert type="error" message={error} />}
      {loading ? (
        <Loading label="Loading sales" />
      ) : (
        <Card>
          <Table
            columns={["Sale", "Customer", "Item", "Amount", "Payment", "Date"]}
            empty={sales.length === 0}
            emptyTitle="No sales found"
          >
            {sales.map((sale) => (
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
        </Card>
      )}
    </div>
  );
}
