import { FormEvent, useEffect, useState } from "react";
import { ApiError } from "../api/client";
import { api } from "../api/jewelflow";
import { Alert } from "../components/Alert";
import { Button } from "../components/Button";
import { Card } from "../components/Card";
import { Field, SelectInput, TextInput } from "../components/FormControls";
import { Loading } from "../components/Loading";
import { Table } from "../components/Table";
import type { CreateUserRequest, UserAccount, UserRole } from "../types";
import { formatDateTime } from "../utils/format";

const roleOptions: UserRole[] = ["ADMIN", "STAFF"];

export function UsersPage() {
  const [users, setUsers] = useState<UserAccount[]>([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [updatingUserId, setUpdatingUserId] = useState<number | null>(null);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [resetPasswords, setResetPasswords] = useState<Record<number, string>>({});
  const [form, setForm] = useState<CreateUserRequest>({
    username: "",
    password: "",
    role: "STAFF"
  });

  function loadUsers() {
    setLoading(true);
    api
      .users()
      .then(setUsers)
      .catch((err: Error) => setError(err.message))
      .finally(() => setLoading(false));
  }

  useEffect(loadUsers, []);

  function setFormValue<K extends keyof CreateUserRequest>(field: K, value: CreateUserRequest[K]) {
    setForm((current) => ({ ...current, [field]: value }));
  }

  function readError(err: unknown, fallback: string) {
    if (err instanceof ApiError && err.fieldErrors) {
      return Object.values(err.fieldErrors).join(", ");
    }
    return err instanceof Error ? err.message : fallback;
  }

  async function handleCreate(event: FormEvent) {
    event.preventDefault();
    setError("");
    setSuccess("");
    if (!form.username.trim() || !form.password.trim()) {
      setError("Username and password are required.");
      return;
    }
    if (form.password.length < 8) {
      setError("Password must be at least 8 characters.");
      return;
    }

    setSaving(true);
    try {
      const created = await api.createUser({ ...form, username: form.username.trim() });
      setUsers((current) => [...current, created].sort((a, b) => a.username.localeCompare(b.username)));
      setForm({ username: "", password: "", role: "STAFF" });
      setSuccess("User created.");
    } catch (err) {
      setError(readError(err, "Unable to create user."));
    } finally {
      setSaving(false);
    }
  }

  async function updateUser(user: UserAccount, next: Pick<UserAccount, "role" | "enabled">) {
    setError("");
    setSuccess("");
    setUpdatingUserId(user.id);
    try {
      const updated = await api.updateUser(user.id, next);
      setUsers((current) => current.map((entry) => (entry.id === updated.id ? updated : entry)));
      setSuccess("User updated.");
    } catch (err) {
      setError(readError(err, "Unable to update user."));
    } finally {
      setUpdatingUserId(null);
    }
  }

  async function resetPassword(event: FormEvent, user: UserAccount) {
    event.preventDefault();
    const newPassword = resetPasswords[user.id]?.trim() ?? "";
    setError("");
    setSuccess("");
    if (newPassword.length < 8) {
      setError("Password must be at least 8 characters.");
      return;
    }

    setUpdatingUserId(user.id);
    try {
      await api.resetUserPassword(user.id, { newPassword });
      setResetPasswords((current) => ({ ...current, [user.id]: "" }));
      setSuccess(`Password reset for ${user.username}.`);
    } catch (err) {
      setError(readError(err, "Unable to reset password."));
    } finally {
      setUpdatingUserId(null);
    }
  }

  return (
    <div className="page-stack">
      <Card title="Create User">
        <form className="form-grid compact" onSubmit={handleCreate}>
          {error && <Alert type="error" message={error} />}
          {success && <Alert type="success" message={success} />}
          <Field label="Username">
            <TextInput
              value={form.username}
              onChange={(event) => setFormValue("username", event.target.value)}
              autoComplete="off"
            />
          </Field>
          <Field label="Temporary Password">
            <TextInput
              type="password"
              value={form.password}
              onChange={(event) => setFormValue("password", event.target.value)}
              autoComplete="new-password"
            />
          </Field>
          <Field label="Role">
            <SelectInput value={form.role} onChange={(event) => setFormValue("role", event.target.value as UserRole)}>
              {roleOptions.map((role) => (
                <option key={role} value={role}>
                  {role}
                </option>
              ))}
            </SelectInput>
          </Field>
          <div className="form-actions">
            <Button type="submit" disabled={saving}>
              {saving ? "Creating" : "Create User"}
            </Button>
          </div>
        </form>
      </Card>

      {loading ? (
        <Loading label="Loading users" />
      ) : (
        <Card title="User Accounts">
          <Table
            columns={["User", "Role", "Status", "Created", "Reset Password"]}
            empty={users.length === 0}
            emptyTitle="No users found"
          >
            {users.map((user) => (
              <tr key={user.id}>
                <td>
                  <strong>{user.username}</strong>
                  <small>Updated {formatDateTime(user.updatedAt)}</small>
                </td>
                <td>
                  <SelectInput
                    value={user.role}
                    disabled={updatingUserId === user.id}
                    onChange={(event) => updateUser(user, { role: event.target.value as UserRole, enabled: user.enabled })}
                  >
                    {roleOptions.map((role) => (
                      <option key={role} value={role}>
                        {role}
                      </option>
                    ))}
                  </SelectInput>
                </td>
                <td>
                  <label className="checkbox-field">
                    <input
                      type="checkbox"
                      checked={user.enabled}
                      disabled={updatingUserId === user.id}
                      onChange={(event) => updateUser(user, { role: user.role, enabled: event.target.checked })}
                    />
                    <span>{user.enabled ? "Enabled" : "Disabled"}</span>
                  </label>
                </td>
                <td>{formatDateTime(user.createdAt)}</td>
                <td>
                  <form className="inline-controls" onSubmit={(event) => resetPassword(event, user)}>
                    <TextInput
                      type="password"
                      minLength={8}
                      value={resetPasswords[user.id] ?? ""}
                      placeholder="New password"
                      autoComplete="new-password"
                      onChange={(event) =>
                        setResetPasswords((current) => ({ ...current, [user.id]: event.target.value }))
                      }
                    />
                    <Button type="submit" variant="secondary" disabled={updatingUserId === user.id}>
                      Reset
                    </Button>
                  </form>
                </td>
              </tr>
            ))}
          </Table>
        </Card>
      )}
    </div>
  );
}
