import { useEffect, useState } from "react";
import { api } from "../api/jewelflow";
import { Alert } from "../components/Alert";
import { Button } from "../components/Button";
import { Card } from "../components/Card";
import { Field, SelectInput, TextInput } from "../components/FormControls";
import { Loading } from "../components/Loading";
import { PaginationControls } from "../components/PaginationControls";
import { Table } from "../components/Table";
import type { Customer } from "../types";
import { formatDateTime } from "../utils/format";
import { createPageQuery, emptyPage, resetPage, toPageParams } from "../utils/pagination";

interface CustomersPageProps {
  onNavigate: (path: string) => void;
}

export function CustomersPage({ onNavigate }: CustomersPageProps) {
  const [customersPage, setCustomersPage] = useState(() => emptyPage<Customer>(createPageQuery("createdAt")));
  const [pageQuery, setPageQuery] = useState(() => createPageQuery("createdAt"));
  const [keyword, setKeyword] = useState("");
  const [includeArchived, setIncludeArchived] = useState("false");
  const [refreshKey, setRefreshKey] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    setLoading(true);
    setError("");
    api
      .customersPage({ keyword, includeArchived, ...toPageParams(pageQuery) })
      .then(setCustomersPage)
      .catch((err: Error) => setError(err.message))
      .finally(() => setLoading(false));
  }, [keyword, includeArchived, pageQuery, refreshKey]);

  function updateKeyword(value: string) {
    setKeyword(value);
    setPageQuery((current) => resetPage(current));
  }

  function updatePageQuery(field: keyof typeof pageQuery, value: string | number) {
    setPageQuery((current) => ({
      ...current,
      page: field === "page" ? Number(value) : 0,
      [field]: value
    }));
  }

  function updateArchiveFilter(value: string) {
    setIncludeArchived(value);
    setPageQuery((current) => resetPage(current));
  }

  async function archiveCustomer(customer: Customer) {
    if (!window.confirm(`Archive ${customer.fullName}?`)) {
      return;
    }
    try {
      setError("");
      await api.archiveCustomer(customer.id);
      setRefreshKey((current) => current + 1);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to archive customer");
    }
  }

  async function restoreCustomer(customer: Customer) {
    try {
      setError("");
      await api.restoreCustomer(customer.id);
      setRefreshKey((current) => current + 1);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to restore customer");
    }
  }

  return (
    <div className="page-stack">
      <Card
        title="Customers"
        action={<Button onClick={() => onNavigate("/customers/new")}>Add Customer</Button>}
      >
        <Field label="Search">
          <TextInput
            value={keyword}
            onChange={(event) => updateKeyword(event.target.value)}
            placeholder="Name, phone, or email"
          />
        </Field>
        <div className="filters-grid">
          <Field label="Sort By">
            <SelectInput value={pageQuery.sortBy} onChange={(event) => updatePageQuery("sortBy", event.target.value)}>
              <option value="createdAt">Created</option>
              <option value="updatedAt">Updated</option>
              <option value="fullName">Name</option>
              <option value="phoneNumber">Phone</option>
              <option value="city">City</option>
            </SelectInput>
          </Field>
          <Field label="Direction">
            <SelectInput value={pageQuery.direction} onChange={(event) => updatePageQuery("direction", event.target.value)}>
              <option value="DESC">Descending</option>
              <option value="ASC">Ascending</option>
            </SelectInput>
          </Field>
          <Field label="Archived">
            <SelectInput value={includeArchived} onChange={(event) => updateArchiveFilter(event.target.value)}>
              <option value="false">Active only</option>
              <option value="true">Include archived</option>
            </SelectInput>
          </Field>
        </div>
      </Card>

      {error && <Alert type="error" message={error} />}
      {loading ? (
        <Loading label="Loading customers" />
      ) : (
        <Card>
          <Table
            columns={["Customer", "Phone", "Email", "Location", "Created", ""]}
            empty={customersPage.content.length === 0}
            emptyTitle="No customers found"
          >
            {customersPage.content.map((customer) => (
              <tr key={customer.id}>
                <td>
                  <strong>{customer.fullName}</strong>
                  <small>{customer.archived ? "Archived customer" : customer.notes}</small>
                </td>
                <td>{customer.phoneNumber}</td>
                <td>{customer.email || "Not set"}</td>
                <td>{[customer.city, customer.state].filter(Boolean).join(", ") || "Not set"}</td>
                <td>{formatDateTime(customer.createdAt)}</td>
                <td className="row-actions">
                  {customer.archived ? (
                    <Button variant="secondary" onClick={() => restoreCustomer(customer)}>
                      Restore
                    </Button>
                  ) : (
                    <>
                      <Button variant="ghost" onClick={() => onNavigate(`/customers/${customer.id}/edit`)}>
                        Edit
                      </Button>
                      <Button variant="danger" onClick={() => archiveCustomer(customer)}>
                        Archive
                      </Button>
                    </>
                  )}
                </td>
              </tr>
            ))}
          </Table>
          <PaginationControls
            page={customersPage}
            onPageChange={(page) => updatePageQuery("page", page)}
            onPageSizeChange={(size) => updatePageQuery("size", size)}
          />
        </Card>
      )}
    </div>
  );
}
