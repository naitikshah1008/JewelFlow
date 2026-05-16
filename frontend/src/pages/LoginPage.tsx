import { type FormEvent, useState } from "react";
import { api } from "../api/jewelflow";
import { Alert } from "../components/Alert";
import { Button } from "../components/Button";
import { Field, TextInput } from "../components/FormControls";
import type { AuthSession } from "../types";
import { writeAuthSession } from "../utils/auth";

interface LoginPageProps {
  onLogin: (session: AuthSession) => void;
}

export function LoginPage({ onLogin }: LoginPageProps) {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!username.trim() || !password) {
      setError("Username and password are required.");
      return;
    }

    setSubmitting(true);
    setError("");
    try {
      const response = await api.login({ username: username.trim(), password });
      const session: AuthSession = {
        token: response.token,
        tokenType: response.tokenType,
        expiresAt: response.expiresAt,
        username: response.username,
        role: response.role
      };
      writeAuthSession(session);
      onLogin(session);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Unable to sign in.");
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
          <h1>Sign In</h1>
          {error && <Alert type="error" message={error} />}
          <Field label="Username">
            <TextInput
              autoComplete="username"
              value={username}
              onChange={(event) => setUsername(event.target.value)}
            />
          </Field>
          <Field label="Password">
            <TextInput
              type="password"
              autoComplete="current-password"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
            />
          </Field>
          <Button type="submit" disabled={submitting}>
            {submitting ? "Signing In" : "Sign In"}
          </Button>
        </form>
      </section>
    </div>
  );
}
