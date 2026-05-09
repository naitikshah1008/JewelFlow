import { useEffect, useState } from "react";
import { api } from "../api/jewelflow";
import { Alert } from "../components/Alert";
import { Button } from "../components/Button";
import { Card } from "../components/Card";
import { Field, TextInput } from "../components/FormControls";
import { Loading } from "../components/Loading";
import { Table } from "../components/Table";
import type { Customer } from "../types";
import { formatDateTime } from "../utils/format";

interface CustomersPageProps {
  onNavigate: (path: string) => void;
}

export function CustomersPage({ onNavigate }: CustomersPageProps) {
  const [customers, setCustomers] = useState<Customer[]>([]);
  const [keyword, setKeyword] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    setLoading(true);
    api
      .customers(keyword)
      .then(setCustomers)
      .catch((err: Error) => setError(err.message))
      .finally(() => setLoading(false));
  }, [keyword]);

  return (
    <div className="page-stack">
      <Card
        title="Customers"
        action={<Button onClick={() => onNavigate("/customers/new")}>Add Customer</Button>}
      >
        <Field label="Search">
          <TextInput
            value={keyword}
            onChange={(event) => setKeyword(event.target.value)}
            placeholder="Name, phone, or email"
          />
        </Field>
      </Card>

      {error && <Alert type="error" message={error} />}
      {loading ? (
        <Loading label="Loading customers" />
      ) : (
        <Card>
          <Table
            columns={["Customer", "Phone", "Email", "Location", "Created", ""]}
            empty={customers.length === 0}
            emptyTitle="No customers found"
          >
            {customers.map((customer) => (
              <tr key={customer.id}>
                <td>
                  <strong>{customer.fullName}</strong>
                  <small>{customer.notes}</small>
                </td>
                <td>{customer.phoneNumber}</td>
                <td>{customer.email || "Not set"}</td>
                <td>{[customer.city, customer.state].filter(Boolean).join(", ") || "Not set"}</td>
                <td>{formatDateTime(customer.createdAt)}</td>
                <td className="row-actions">
                  <Button variant="ghost" onClick={() => onNavigate(`/customers/${customer.id}/edit`)}>
                    Edit
                  </Button>
                </td>
              </tr>
            ))}
          </Table>
        </Card>
      )}
    </div>
  );
}
