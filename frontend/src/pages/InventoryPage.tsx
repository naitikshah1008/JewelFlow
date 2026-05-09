import { useEffect, useState } from "react";
import { api } from "../api/jewelflow";
import { Alert } from "../components/Alert";
import { Button } from "../components/Button";
import { Card } from "../components/Card";
import { Field, SelectInput, TextInput } from "../components/FormControls";
import { Loading } from "../components/Loading";
import { Table } from "../components/Table";
import type { JewelleryItem } from "../types";
import { formatCurrency, formatDateTime, formatNumber } from "../utils/format";

interface InventoryPageProps {
  onNavigate: (path: string) => void;
}

export function InventoryPage({ onNavigate }: InventoryPageProps) {
  const [items, setItems] = useState<JewelleryItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [filters, setFilters] = useState({
    keyword: "",
    status: "",
    category: "",
    metalType: "",
    purity: ""
  });

  useEffect(() => {
    setLoading(true);
    api
      .items(filters)
      .then(setItems)
      .catch((err: Error) => setError(err.message))
      .finally(() => setLoading(false));
  }, [filters]);

  return (
    <div className="page-stack">
      <Card
        title="Inventory Items"
        action={<Button onClick={() => onNavigate("/inventory/new")}>Add Item</Button>}
      >
        <div className="filters-grid">
          <Field label="Search">
            <TextInput
              value={filters.keyword}
              onChange={(event) => setFilters({ ...filters, keyword: event.target.value })}
              placeholder="Name, category, metal, status"
            />
          </Field>
          <Field label="Status">
            <SelectInput
              value={filters.status}
              onChange={(event) => setFilters({ ...filters, status: event.target.value })}
            >
              <option value="">All</option>
              <option value="AVAILABLE">Available</option>
              <option value="RESERVED">Reserved</option>
              <option value="SOLD">Sold</option>
            </SelectInput>
          </Field>
          <Field label="Metal">
            <SelectInput
              value={filters.metalType}
              onChange={(event) => setFilters({ ...filters, metalType: event.target.value })}
            >
              <option value="">All</option>
              <option value="GOLD">Gold</option>
              <option value="SILVER">Silver</option>
              <option value="PLATINUM">Platinum</option>
            </SelectInput>
          </Field>
          <Field label="Purity">
            <SelectInput
              value={filters.purity}
              onChange={(event) => setFilters({ ...filters, purity: event.target.value })}
            >
              <option value="">All</option>
              <option value="24K">24K</option>
              <option value="22K">22K</option>
              <option value="18K">18K</option>
              <option value="14K">14K</option>
            </SelectInput>
          </Field>
        </div>
      </Card>

      {error && <Alert type="error" message={error} />}
      {loading ? (
        <Loading label="Loading inventory" />
      ) : (
        <Card>
          <Table
            columns={["Item", "Metal", "Weight", "Price", "Status", "Updated", ""]}
            empty={items.length === 0}
            emptyTitle="No inventory items found"
          >
            {items.map((item) => (
              <tr key={item.id}>
                <td>
                  <strong>{item.itemName}</strong>
                  <small>{item.category}</small>
                </td>
                <td>
                  {item.metalType} {item.purity}
                </td>
                <td>{formatNumber(item.netWeight)} g</td>
                <td>{formatCurrency(item.sellingPrice)}</td>
                <td>
                  <span className="status-pill">{item.status}</span>
                </td>
                <td>{formatDateTime(item.updatedAt ?? item.createdAt)}</td>
                <td className="row-actions">
                  <Button variant="ghost" onClick={() => onNavigate(`/inventory/${item.id}/edit`)}>
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
