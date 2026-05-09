import { FormEvent, useEffect, useState } from "react";
import { ApiError } from "../api/client";
import { api } from "../api/jewelflow";
import { Alert } from "../components/Alert";
import { Button } from "../components/Button";
import { Card } from "../components/Card";
import { Field, SelectInput, TextArea, TextInput } from "../components/FormControls";
import { Loading } from "../components/Loading";
import { Table } from "../components/Table";
import type { GoldRate, GoldRateRequest } from "../types";
import { formatCurrency, formatDate, formatDateTime } from "../utils/format";

export function GoldRatesPage() {
  const [rates, setRates] = useState<GoldRate[]>([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [form, setForm] = useState({
    metalType: "GOLD",
    purity: "22K",
    ratePerGram: "",
    rateDate: new Date().toISOString().slice(0, 10),
    source: "Manual",
    notes: ""
  });

  function loadRates() {
    setLoading(true);
    api
      .goldRates()
      .then(setRates)
      .catch((err: Error) => setError(err.message))
      .finally(() => setLoading(false));
  }

  useEffect(loadRates, []);

  function update(field: keyof typeof form, value: string) {
    setForm((current) => ({ ...current, [field]: value }));
  }

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    setError("");
    setSuccess("");
    if (!form.ratePerGram || !form.rateDate) {
      setError("Rate per gram and rate date are required");
      return;
    }
    const payload: GoldRateRequest = {
      metalType: form.metalType,
      purity: form.purity,
      ratePerGram: Number(form.ratePerGram),
      rateDate: form.rateDate,
      source: form.source.trim() || undefined,
      notes: form.notes.trim() || undefined
    };
    setSaving(true);
    try {
      await api.createGoldRate(payload);
      setSuccess("Gold rate saved");
      setForm((current) => ({ ...current, ratePerGram: "", notes: "" }));
      loadRates();
    } catch (err) {
      if (err instanceof ApiError && err.fieldErrors) {
        setError(Object.values(err.fieldErrors).join(", "));
      } else {
        setError(err instanceof Error ? err.message : "Unable to save gold rate");
      }
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className="page-stack">
      <Card title="Gold Rates">
        <form className="form-grid compact" onSubmit={handleSubmit}>
          {error && <Alert type="error" message={error} />}
          {success && <Alert type="success" message={success} />}
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
          <Field label="Rate Per Gram">
            <TextInput type="number" min="0" step="0.01" value={form.ratePerGram} onChange={(event) => update("ratePerGram", event.target.value)} />
          </Field>
          <Field label="Rate Date">
            <TextInput type="date" value={form.rateDate} onChange={(event) => update("rateDate", event.target.value)} />
          </Field>
          <Field label="Source">
            <TextInput value={form.source} onChange={(event) => update("source", event.target.value)} />
          </Field>
          <Field label="Notes">
            <TextArea value={form.notes} onChange={(event) => update("notes", event.target.value)} />
          </Field>
          <div className="form-actions">
            <Button type="submit" disabled={saving}>
              {saving ? "Saving" : "Save Rate"}
            </Button>
          </div>
        </form>
      </Card>

      {loading ? (
        <Loading label="Loading gold rates" />
      ) : (
        <Card>
          <Table
            columns={["Metal", "Purity", "Rate", "Date", "Source", "Created"]}
            empty={rates.length === 0}
            emptyTitle="No gold rates found"
          >
            {rates.map((rate) => (
              <tr key={rate.id}>
                <td>{rate.metalType}</td>
                <td>{rate.purity}</td>
                <td>{formatCurrency(rate.ratePerGram)}</td>
                <td>{formatDate(rate.rateDate)}</td>
                <td>{rate.source || "Not set"}</td>
                <td>{formatDateTime(rate.createdAt)}</td>
              </tr>
            ))}
          </Table>
        </Card>
      )}
    </div>
  );
}
