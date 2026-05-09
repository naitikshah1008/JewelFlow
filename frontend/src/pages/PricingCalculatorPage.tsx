import { FormEvent, useState } from "react";
import { ApiError } from "../api/client";
import { api } from "../api/jewelflow";
import { Alert } from "../components/Alert";
import { Button } from "../components/Button";
import { Card } from "../components/Card";
import { Field, SelectInput, TextInput } from "../components/FormControls";
import type { PricingRequest, PricingResponse } from "../types";
import { formatCurrency, formatNumber, toNumber } from "../utils/format";

export function PricingCalculatorPage() {
  const [form, setForm] = useState({
    netWeight: "",
    metalType: "GOLD",
    purity: "22K",
    goldRatePerGram: "",
    stonePrice: "0",
    makingCharges: "0",
    taxPercentage: "3",
    discount: "0"
  });
  const [result, setResult] = useState<PricingResponse | null>(null);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  function update(field: keyof typeof form, value: string) {
    setForm((current) => ({ ...current, [field]: value }));
  }

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    setError("");
    setResult(null);
    if (!form.netWeight) {
      setError("Net weight is required");
      return;
    }
    const payload: PricingRequest = {
      netWeight: Number(form.netWeight),
      metalType: form.metalType,
      purity: form.purity,
      goldRatePerGram: toNumber(form.goldRatePerGram),
      stonePrice: Number(form.stonePrice),
      makingCharges: Number(form.makingCharges),
      taxPercentage: Number(form.taxPercentage),
      discount: Number(form.discount)
    };

    setSaving(true);
    try {
      setResult(await api.calculatePrice(payload));
    } catch (err) {
      if (err instanceof ApiError && err.fieldErrors) {
        setError(Object.values(err.fieldErrors).join(", "));
      } else {
        setError(err instanceof Error ? err.message : "Unable to calculate price");
      }
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className="two-column">
      <Card title="Pricing Calculator">
        <form className="form-grid compact" onSubmit={handleSubmit}>
          {error && <Alert type="error" message={error} />}
          <Field label="Net Weight">
            <TextInput type="number" min="0" step="0.01" value={form.netWeight} onChange={(event) => update("netWeight", event.target.value)} />
          </Field>
          <Field label="Metal">
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
          <div className="form-actions">
            <Button type="submit" disabled={saving}>
              {saving ? "Calculating" : "Calculate"}
            </Button>
          </div>
        </form>
      </Card>

      <Card title="Price Result">
        {result ? (
          <div className="summary-list">
            <div>
              <span>Purity factor</span>
              <strong>{formatNumber(result.purityFactor, 6)}</strong>
            </div>
            <div>
              <span>Gold rate</span>
              <strong>{formatCurrency(result.goldRatePerGram)}</strong>
            </div>
            <div>
              <span>Gold value</span>
              <strong>{formatCurrency(result.goldValue)}</strong>
            </div>
            <div>
              <span>Subtotal</span>
              <strong>{formatCurrency(result.subtotal)}</strong>
            </div>
            <div>
              <span>Tax</span>
              <strong>{formatCurrency(result.taxAmount)}</strong>
            </div>
            <div>
              <span>Final price</span>
              <strong>{formatCurrency(result.finalPrice)}</strong>
            </div>
          </div>
        ) : (
          <div className="empty-state">
            <strong>No calculation yet</strong>
          </div>
        )}
      </Card>
    </div>
  );
}
