import type { AuthSession } from "../types";

const AUTH_STORAGE_KEY = "jewelflow.auth";

export function readAuthSession(): AuthSession | null {
  const raw = window.localStorage.getItem(AUTH_STORAGE_KEY);
  if (!raw) {
    return null;
  }
  try {
    const session = JSON.parse(raw) as AuthSession;
    return isSessionValid(session) ? session : null;
  } catch {
    return null;
  }
}

export function writeAuthSession(session: AuthSession) {
  window.localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(session));
}

export function clearAuthSession() {
  window.localStorage.removeItem(AUTH_STORAGE_KEY);
}

export function isSessionValid(session: AuthSession): boolean {
  return Boolean(session.token) && new Date(session.expiresAt).getTime() > Date.now();
}
