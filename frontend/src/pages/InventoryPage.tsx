import { useEffect, useState } from "react";
import { api } from "../api/jewelflow";
import { Alert } from "../components/Alert";
import { Button } from "../components/Button";
import { Card } from "../components/Card";
import { Field, SelectInput, TextInput } from "../components/FormControls";
import { Loading } from "../components/Loading";
import { PaginationControls } from "../components/PaginationControls";
import { Table } from "../components/Table";
import type { JewelleryItem } from "../types";
import { formatCurrency, formatDateTime, formatNumber } from "../utils/format";
import { createPageQuery, emptyPage, resetPage, toPageParams } from "../utils/pagination";

interface InventoryPageProps {
  onNavigate: (path: string) => void;
}

export function InventoryPage({ onNavigate }: InventoryPageProps) {
  const [itemsPage, setItemsPage] = useState(() => emptyPage<JewelleryItem>(createPageQuery("createdAt")));
  const [pageQuery, setPageQuery] = useState(() => createPageQuery("createdAt"));
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
      .itemsPage({ ...filters, ...toPageParams(pageQuery) })
      .then(setItemsPage)
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
        title="Inventory Items"
        action={<Button onClick={() => onNavigate("/inventory/new")}>Add Item</Button>}
      >
        <div className="filters-grid">
          <Field label="Search">
            <TextInput
              value={filters.keyword}
              onChange={(event) => updateFilter("keyword", event.target.value)}
              placeholder="Name, category, metal, status"
            />
          </Field>
          <Field label="Status">
            <SelectInput
              value={filters.status}
              onChange={(event) => updateFilter("status", event.target.value)}
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
              onChange={(event) => updateFilter("metalType", event.target.value)}
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
              onChange={(event) => updateFilter("purity", event.target.value)}
            >
              <option value="">All</option>
              <option value="24K">24K</option>
              <option value="22K">22K</option>
              <option value="18K">18K</option>
              <option value="14K">14K</option>
            </SelectInput>
          </Field>
          <Field label="Sort By">
            <SelectInput value={pageQuery.sortBy} onChange={(event) => updatePageQuery("sortBy", event.target.value)}>
              <option value="createdAt">Created</option>
              <option value="updatedAt">Updated</option>
              <option value="itemName">Item Name</option>
              <option value="category">Category</option>
              <option value="status">Status</option>
              <option value="sellingPrice">Price</option>
              <option value="netWeight">Net Weight</option>
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
        <Loading label="Loading inventory" />
      ) : (
        <Card>
          <Table
            columns={["Item", "Metal", "Weight", "Price", "Status", "Updated", ""]}
            empty={itemsPage.content.length === 0}
            emptyTitle="No inventory items found"
          >
            {itemsPage.content.map((item) => (
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
          <PaginationControls
            page={itemsPage}
            onPageChange={(page) => updatePageQuery("page", page)}
            onPageSizeChange={(size) => updatePageQuery("size", size)}
          />
        </Card>
      )}
    </div>
  );
}
