import { FormEvent, useEffect, useState } from "react";
import { ApiError } from "../api/client";
import { api } from "../api/jewelflow";
import { Alert } from "../components/Alert";
import { Button } from "../components/Button";
import { Card } from "../components/Card";
import { Field, SelectInput, TextArea, TextInput } from "../components/FormControls";
import { Loading } from "../components/Loading";
import type { JewelleryItemRequest } from "../types";
import { toNumber } from "../utils/format";

interface InventoryFormPageProps {
  itemId?: number;
  onNavigate: (path: string) => void;
}

interface InventoryFormState {
  itemName: string;
  category: string;
  metalType: string;
  purity: string;
  grossWeight: string;
  netWeight: string;
  stoneWeight: string;
  goldRatePerGram: string;
  stonePrice: string;
  makingCharges: string;
  taxPercentage: string;
  discount: string;
  purchaseCost: string;
  status: string;
  notes: string;
}

const initialState: InventoryFormState = {
  itemName: "",
  category: "",
  metalType: "GOLD",
  purity: "22K",
  grossWeight: "",
  netWeight: "",
  stoneWeight: "0",
  goldRatePerGram: "",
  stonePrice: "0",
  makingCharges: "0",
  taxPercentage: "3",
  discount: "0",
  purchaseCost: "0",
  status: "AVAILABLE",
  notes: ""
};

export function InventoryFormPage({ itemId, onNavigate }: InventoryFormPageProps) {
  const [form, setForm] = useState(initialState);
  const [loading, setLoading] = useState(Boolean(itemId));
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});

  useEffect(() => {
    if (!itemId) {
      return;
    }
    api
      .item(itemId)
      .then((item) =>
        setForm({
          itemName: item.itemName ?? "",
          category: item.category ?? "",
          metalType: item.metalType ?? "GOLD",
          purity: item.purity ?? "22K",
          grossWeight: String(item.grossWeight ?? ""),
          netWeight: String(item.netWeight ?? ""),
          stoneWeight: String(item.stoneWeight ?? "0"),
          goldRatePerGram: String(item.goldRatePerGram ?? ""),
          stonePrice: String(item.stonePrice ?? "0"),
          makingCharges: String(item.makingCharges ?? "0"),
          taxPercentage: String(item.taxPercentage ?? "3"),
          discount: String(item.discount ?? "0"),
          purchaseCost: String(item.purchaseCost ?? "0"),
          status: item.status ?? "AVAILABLE",
          notes: ""
        })
      )
      .catch((err: Error) => setError(err.message))
      .finally(() => setLoading(false));
  }, [itemId]);

  function update(field: keyof InventoryFormState, value: string) {
    setForm((current) => ({ ...current, [field]: value }));
  }

  function validate(): boolean {
    const errors: Record<string, string> = {};
    if (!form.itemName.trim()) errors.itemName = "Item name is required";
    if (!form.category.trim()) errors.category = "Category is required";
    if (!form.grossWeight) errors.grossWeight = "Gross weight is required";
    if (!form.netWeight) errors.netWeight = "Net weight is required";
    if (!form.stoneWeight) errors.stoneWeight = "Stone weight is required";
    if (Number(form.netWeight) + Number(form.stoneWeight) > Number(form.grossWeight)) {
      errors.netWeight = "Net and stone weight cannot exceed gross weight";
    }
    setFieldErrors(errors);
    return Object.keys(errors).length === 0;
  }

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    setError("");
    setFieldErrors({});
    if (!validate()) {
      return;
    }
    const payload: JewelleryItemRequest = {
      itemName: form.itemName.trim(),
      category: form.category.trim(),
      metalType: form.metalType,
      purity: form.purity,
      grossWeight: Number(form.grossWeight),
      netWeight: Number(form.netWeight),
      stoneWeight: Number(form.stoneWeight),
      goldRatePerGram: toNumber(form.goldRatePerGram),
      stonePrice: Number(form.stonePrice),
      makingCharges: Number(form.makingCharges),
      taxPercentage: Number(form.taxPercentage),
      discount: Number(form.discount),
      purchaseCost: Number(form.purchaseCost),
      status: form.status
    };

    setSaving(true);
    try {
      if (itemId) {
        await api.updateItem(itemId, payload);
      } else {
        await api.createItem(payload);
      }
      onNavigate("/inventory");
    } catch (err) {
      if (err instanceof ApiError) {
        setFieldErrors(err.fieldErrors ?? {});
      }
      setError(err instanceof Error ? err.message : "Unable to save item");
    } finally {
      setSaving(false);
    }
  }

  if (loading) {
    return <Loading label="Loading item" />;
  }

  return (
    <Card title={itemId ? "Edit Inventory Item" : "Add Inventory Item"}>
      <form className="form-grid" onSubmit={handleSubmit}>
        {error && <Alert type="error" message={error} />}
        <Field label="Item Name" error={fieldErrors.itemName}>
          <TextInput value={form.itemName} onChange={(event) => update("itemName", event.target.value)} />
        </Field>
        <Field label="Category" error={fieldErrors.category}>
          <TextInput value={form.category} onChange={(event) => update("category", event.target.value)} />
        </Field>
        <Field label="Metal Type">
          <SelectInput value={form.metalType} onChange={(event) => update("metalType", event.target.value)}>
            <option value="GOLD">Gold</option>
            <option value="SILVER">Silver</option>
            <option value="PLATINUM">Platinum</option>
          </SelectInput>
        </Field>
        <Field label="Purity">
          <SelectInput value={form.purity} onChange={(event) => update("purity", event.target.value)}>
            <option value="24K">24K</option>
            <option value="22K">22K</option>
            <option value="18K">18K</option>
            <option value="14K">14K</option>
          </SelectInput>
        </Field>
        <Field label="Gross Weight" error={fieldErrors.grossWeight}>
          <TextInput type="number" min="0" step="0.01" value={form.grossWeight} onChange={(event) => update("grossWeight", event.target.value)} />
        </Field>
        <Field label="Net Weight" error={fieldErrors.netWeight}>
          <TextInput type="number" min="0" step="0.01" value={form.netWeight} onChange={(event) => update("netWeight", event.target.value)} />
        </Field>
        <Field label="Stone Weight" error={fieldErrors.stoneWeight}>
          <TextInput type="number" min="0" step="0.01" value={form.stoneWeight} onChange={(event) => update("stoneWeight", event.target.value)} />
        </Field>
        <Field label="Gold Rate Per Gram">
          <TextInput type="number" min="0" step="0.01" value={form.goldRatePerGram} onChange={(event) => update("goldRatePerGram", event.target.value)} placeholder="Use latest saved rate" />
        </Field>
        <Field label="Stone Price">
          <TextInput type="number" min="0" step="0.01" value={form.stonePrice} onChange={(event) => update("stonePrice", event.target.value)} />
        </Field>
        <Field label="Making Charges">
          <TextInput type="number" min="0" step="0.01" value={form.makingCharges} onChange={(event) => update("makingCharges", event.target.value)} />
        </Field>
        <Field label="Tax %">
          <TextInput type="number" min="0" step="0.01" value={form.taxPercentage} onChange={(event) => update("taxPercentage", event.target.value)} />
        </Field>
        <Field label="Discount">
          <TextInput type="number" min="0" step="0.01" value={form.discount} onChange={(event) => update("discount", event.target.value)} />
        </Field>
        <Field label="Purchase Cost">
          <TextInput type="number" min="0" step="0.01" value={form.purchaseCost} onChange={(event) => update("purchaseCost", event.target.value)} />
        </Field>
        <Field label="Status">
          <SelectInput value={form.status} onChange={(event) => update("status", event.target.value)}>
            <option value="AVAILABLE">Available</option>
            <option value="RESERVED">Reserved</option>
            <option value="SOLD">Sold</option>
          </SelectInput>
        </Field>
        <Field label="Notes">
          <TextArea value={form.notes} onChange={(event) => update("notes", event.target.value)} />
        </Field>
        <div className="form-actions">
          <Button type="button" variant="secondary" onClick={() => onNavigate("/inventory")}>
            Cancel
          </Button>
          <Button type="submit" disabled={saving}>
            {saving ? "Saving" : "Save Item"}
          </Button>
        </div>
      </form>
    </Card>
  );
}
