import { FormEvent, useEffect, useMemo, useState } from "react";
import { ApiError } from "../api/client";
import { api } from "../api/jewelflow";
import { Alert } from "../components/Alert";
import { Button } from "../components/Button";
import { Card } from "../components/Card";
import { Field, SelectInput, TextArea, TextInput } from "../components/FormControls";
import { Loading } from "../components/Loading";
import type { Customer, Invoice, InvoiceRequest, JewelleryItem } from "../types";
import { formatCurrency } from "../utils/format";

interface CreateInvoicePageProps {
  onNavigate: (path: string) => void;
}

export function CreateInvoicePage({ onNavigate }: CreateInvoicePageProps) {
  const [customers, setCustomers] = useState<Customer[]>([]);
  const [items, setItems] = useState<JewelleryItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [createdInvoice, setCreatedInvoice] = useState<Invoice | null>(null);
  const [form, setForm] = useState({
    customerId: "",
    itemId: "",
    quantity: "1",
    taxPercentage: "3",
    discount: "0",
    paymentStatus: "PAID",
    paymentMethod: "CASH",
    notes: ""
  });

  useEffect(() => {
    Promise.all([api.customers(), api.items({ status: "AVAILABLE" })])
      .then(([customerData, itemData]) => {
        setCustomers(customerData);
        setItems(itemData);
      })
      .catch((err: Error) => setError(err.message))
      .finally(() => setLoading(false));
  }, []);

  const selectedItem = useMemo(
    () => items.find((item) => String(item.id) === form.itemId),
    [items, form.itemId]
  );

  useEffect(() => {
    if (!selectedItem) {
      return;
    }
    setForm((current) => ({
      ...current,
      taxPercentage: String(selectedItem.taxPercentage ?? current.taxPercentage),
      discount: String(selectedItem.discount ?? current.discount)
    }));
  }, [selectedItem]);

  function update(field: keyof typeof form, value: string) {
    setForm((current) => ({ ...current, [field]: value }));
  }

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    setError("");
    setCreatedInvoice(null);
    if (!form.customerId || !form.itemId) {
      setError("Customer and item are required");
      return;
    }
    const payload: InvoiceRequest = {
      customerId: Number(form.customerId),
      itemId: Number(form.itemId),
      quantity: Number(form.quantity),
      taxPercentage: Number(form.taxPercentage),
      discount: Number(form.discount),
      paymentStatus: form.paymentStatus,
      paymentMethod: form.paymentMethod,
      notes: form.notes.trim() || undefined
    };

    setSaving(true);
    try {
      const invoice = await api.createInvoice(payload);
      setCreatedInvoice(invoice);
      const freshItems = await api.items({ status: "AVAILABLE" });
      setItems(freshItems);
      setForm((current) => ({ ...current, itemId: "" }));
    } catch (err) {
      if (err instanceof ApiError && err.fieldErrors) {
        setError(Object.values(err.fieldErrors).join(", "));
      } else {
        setError(err instanceof Error ? err.message : "Unable to create invoice");
      }
    } finally {
      setSaving(false);
    }
  }

  if (loading) {
    return <Loading label="Loading invoice data" />;
  }

  return (
    <div className="two-column">
      <Card title="Create Invoice">
        <form className="form-grid compact" onSubmit={handleSubmit}>
          {error && <Alert type="error" message={error} />}
          {createdInvoice && (
            <Alert type="success" message={`Created ${createdInvoice.invoiceNumber}`} />
          )}
          <Field label="Customer">
            <SelectInput value={form.customerId} onChange={(event) => update("customerId", event.target.value)}>
              <option value="">Select customer</option>
              {customers.map((customer) => (
                <option key={customer.id} value={customer.id}>
                  {customer.fullName} - {customer.phoneNumber}
                </option>
              ))}
            </SelectInput>
          </Field>
          <Field label="Available Item">
            <SelectInput value={form.itemId} onChange={(event) => update("itemId", event.target.value)}>
              <option value="">Select item</option>
              {items.map((item) => (
                <option key={item.id} value={item.id}>
                  {item.itemName} - {formatCurrency(item.sellingPrice)}
                </option>
              ))}
            </SelectInput>
          </Field>
          <Field label="Quantity">
            <TextInput type="number" min="1" step="1" value={form.quantity} onChange={(event) => update("quantity", event.target.value)} />
          </Field>
          <Field label="Tax %">
            <TextInput type="number" min="0" step="0.01" value={form.taxPercentage} onChange={(event) => update("taxPercentage", event.target.value)} />
          </Field>
          <Field label="Discount">
            <TextInput type="number" min="0" step="0.01" value={form.discount} onChange={(event) => update("discount", event.target.value)} />
          </Field>
          <Field label="Payment Status">
            <SelectInput value={form.paymentStatus} onChange={(event) => update("paymentStatus", event.target.value)}>
              <option value="PAID">Paid</option>
              <option value="UNPAID">Unpaid</option>
              <option value="PARTIAL">Partial</option>
            </SelectInput>
          </Field>
          <Field label="Payment Method">
            <SelectInput value={form.paymentMethod} onChange={(event) => update("paymentMethod", event.target.value)}>
              <option value="CASH">Cash</option>
              <option value="CARD">Card</option>
              <option value="UPI">UPI</option>
              <option value="BANK_TRANSFER">Bank Transfer</option>
              <option value="OTHER">Other</option>
            </SelectInput>
          </Field>
          <Field label="Notes">
            <TextArea value={form.notes} onChange={(event) => update("notes", event.target.value)} />
          </Field>
          <div className="form-actions">
            <Button type="button" variant="secondary" onClick={() => onNavigate("/invoices")}>
              View Invoices
            </Button>
            <Button type="submit" disabled={saving}>
              {saving ? "Creating" : "Create Invoice"}
            </Button>
          </div>
        </form>
      </Card>

      <Card title="Selected Item">
        {selectedItem ? (
          <div className="summary-list">
            <div>
              <span>Item</span>
              <strong>{selectedItem.itemName}</strong>
            </div>
            <div>
              <span>Metal</span>
              <strong>
                {selectedItem.metalType} {selectedItem.purity}
              </strong>
            </div>
            <div>
              <span>Net weight</span>
              <strong>{selectedItem.netWeight} g</strong>
            </div>
            <div>
              <span>Selling price</span>
              <strong>{formatCurrency(selectedItem.sellingPrice)}</strong>
            </div>
          </div>
        ) : (
          <div className="empty-state">
            <strong>No item selected</strong>
          </div>
        )}
      </Card>
    </div>
  );
}
