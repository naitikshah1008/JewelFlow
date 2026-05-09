import { FormEvent, useEffect, useState } from "react";
import { ApiError } from "../api/client";
import { api } from "../api/jewelflow";
import { Alert } from "../components/Alert";
import { Button } from "../components/Button";
import { Card } from "../components/Card";
import { Field, TextArea, TextInput } from "../components/FormControls";
import { Loading } from "../components/Loading";
import type { CustomerRequest } from "../types";

interface CustomerFormPageProps {
  customerId?: number;
  onNavigate: (path: string) => void;
}

const emptyCustomer = {
  fullName: "",
  phoneNumber: "",
  email: "",
  address: "",
  city: "",
  state: "",
  postalCode: "",
  notes: ""
};

export function CustomerFormPage({ customerId, onNavigate }: CustomerFormPageProps) {
  const [form, setForm] = useState(emptyCustomer);
  const [loading, setLoading] = useState(Boolean(customerId));
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});

  useEffect(() => {
    if (!customerId) {
      return;
    }
    api
      .customer(customerId)
      .then((customer) =>
        setForm({
          fullName: customer.fullName ?? "",
          phoneNumber: customer.phoneNumber ?? "",
          email: customer.email ?? "",
          address: customer.address ?? "",
          city: customer.city ?? "",
          state: customer.state ?? "",
          postalCode: customer.postalCode ?? "",
          notes: customer.notes ?? ""
        })
      )
      .catch((err: Error) => setError(err.message))
      .finally(() => setLoading(false));
  }, [customerId]);

  function update(field: keyof typeof emptyCustomer, value: string) {
    setForm((current) => ({ ...current, [field]: value }));
  }

  function validate(): boolean {
    const errors: Record<string, string> = {};
    if (!form.fullName.trim()) errors.fullName = "Customer full name is required";
    if (!form.phoneNumber.trim()) errors.phoneNumber = "Phone number is required";
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
    const payload: CustomerRequest = {
      fullName: form.fullName.trim(),
      phoneNumber: form.phoneNumber.trim(),
      email: form.email.trim() || undefined,
      address: form.address.trim() || undefined,
      city: form.city.trim() || undefined,
      state: form.state.trim() || undefined,
      postalCode: form.postalCode.trim() || undefined,
      notes: form.notes.trim() || undefined
    };

    setSaving(true);
    try {
      if (customerId) {
        await api.updateCustomer(customerId, payload);
      } else {
        await api.createCustomer(payload);
      }
      onNavigate("/customers");
    } catch (err) {
      if (err instanceof ApiError) {
        setFieldErrors(err.fieldErrors ?? {});
      }
      setError(err instanceof Error ? err.message : "Unable to save customer");
    } finally {
      setSaving(false);
    }
  }

  if (loading) {
    return <Loading label="Loading customer" />;
  }

  return (
    <Card title={customerId ? "Edit Customer" : "Add Customer"}>
      <form className="form-grid" onSubmit={handleSubmit}>
        {error && <Alert type="error" message={error} />}
        <Field label="Full Name" error={fieldErrors.fullName}>
          <TextInput value={form.fullName} onChange={(event) => update("fullName", event.target.value)} />
        </Field>
        <Field label="Phone Number" error={fieldErrors.phoneNumber}>
          <TextInput value={form.phoneNumber} onChange={(event) => update("phoneNumber", event.target.value)} />
        </Field>
        <Field label="Email" error={fieldErrors.email}>
          <TextInput type="email" value={form.email} onChange={(event) => update("email", event.target.value)} />
        </Field>
        <Field label="Address">
          <TextInput value={form.address} onChange={(event) => update("address", event.target.value)} />
        </Field>
        <Field label="City">
          <TextInput value={form.city} onChange={(event) => update("city", event.target.value)} />
        </Field>
        <Field label="State">
          <TextInput value={form.state} onChange={(event) => update("state", event.target.value)} />
        </Field>
        <Field label="Postal Code">
          <TextInput value={form.postalCode} onChange={(event) => update("postalCode", event.target.value)} />
        </Field>
        <Field label="Notes">
          <TextArea value={form.notes} onChange={(event) => update("notes", event.target.value)} />
        </Field>
        <div className="form-actions">
          <Button type="button" variant="secondary" onClick={() => onNavigate("/customers")}>
            Cancel
          </Button>
          <Button type="submit" disabled={saving}>
            {saving ? "Saving" : "Save Customer"}
          </Button>
        </div>
      </form>
    </Card>
  );
}
