import { type FormEvent, useMemo, useState } from "react";
import { api } from "../api/jewelflow";
import { Alert } from "../components/Alert";
import { Button } from "../components/Button";
import { Field, TextInput } from "../components/FormControls";

interface AcceptInvitePageProps {
  onNavigate: (path: string) => void;
}

export function AcceptInvitePage({ onNavigate }: AcceptInvitePageProps) {
  const token = useMemo(() => new URLSearchParams(window.location.search).get("token") ?? "", []);
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError("");
    setSuccess("");
    if (!token) {
      setError("Invite token is missing.");
      return;
    }
    if (password.length < 8) {
      setError("Password must be at least 8 characters.");
      return;
    }
    if (password !== confirmPassword) {
      setError("Passwords do not match.");
      return;
    }

    setSubmitting(true);
    try {
      await api.acceptUserInvite({ token, password });
      setPassword("");
      setConfirmPassword("");
      setSuccess("Account created. You can sign in now.");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Unable to accept invite.");
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="auth-shell">
      <section className="auth-panel">
        <div className="auth-brand">
          <span className="brand-mark">JF</span>
          <div>
            <strong>JewelFlow</strong>
            <span>Store dashboard</span>
          </div>
        </div>

        <form className="auth-form" onSubmit={handleSubmit}>
          <h1>Accept Invite</h1>
          {error && <Alert type="error" message={error} />}
          {success && <Alert type="success" message={success} />}
          <Field label="Password">
            <TextInput
              type="password"
              autoComplete="new-password"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
            />
          </Field>
          <Field label="Confirm Password">
            <TextInput
              type="password"
              autoComplete="new-password"
              value={confirmPassword}
              onChange={(event) => setConfirmPassword(event.target.value)}
            />
          </Field>
          <Button type="submit" disabled={submitting || Boolean(success)}>
            {submitting ? "Creating Account" : "Create Account"}
          </Button>
          <Button type="button" variant="ghost" onClick={() => onNavigate("/login")}>
            Back to Sign In
          </Button>
        </form>
      </section>
    </div>
  );
}
