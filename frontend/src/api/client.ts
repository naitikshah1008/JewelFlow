import type { ApiErrorBody, AuthResponse, AuthSession } from "../types";
import { clearAuthSession, readAuthSession, writeAuthSession } from "../utils/auth";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";
const PUBLIC_AUTH_PATHS = new Set(["/api/auth/login", "/api/auth/refresh", "/api/auth/logout"]);

export class ApiError extends Error {
  fieldErrors?: Record<string, string>;
  status?: number;

  constructor(message: string, status?: number, fieldErrors?: Record<string, string>) {
    super(message);
    this.name = "ApiError";
    this.status = status;
    this.fieldErrors = fieldErrors;
  }
}

type QueryValue = string | number | undefined | null;

function buildQuery(params?: Record<string, QueryValue>): string {
  if (!params) {
    return "";
  }
  const search = new URLSearchParams();
  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null && String(value).trim() !== "") {
      search.set(key, String(value));
    }
  });
  const query = search.toString();
  return query ? `?${query}` : "";
}

async function refreshAuthSession(session: AuthSession): Promise<AuthSession | null> {
  if (!session.refreshToken) {
    return null;
  }
  const response = await fetch(`${API_BASE_URL}/api/auth/refresh`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ refreshToken: session.refreshToken })
  });

  if (!response.ok) {
    return null;
  }

  const nextSession = (await response.json()) as AuthResponse;
  writeAuthSession(nextSession);
  window.dispatchEvent(new CustomEvent<AuthSession>("jewelflow:auth-updated", { detail: nextSession }));
  return nextSession;
}

async function parseError(response: Response): Promise<ApiError> {
  let body: ApiErrorBody = {};
  try {
    body = await response.json();
  } catch {
    body = { message: response.statusText };
  }
  return new ApiError(body.message ?? "Request failed", response.status, body.fieldErrors);
}

async function sendRequest(
  path: string,
  options: RequestInit,
  params: Record<string, QueryValue> | undefined,
  session: AuthSession | null
): Promise<Response> {
  const isPublicAuthPath = PUBLIC_AUTH_PATHS.has(path);
  return fetch(`${API_BASE_URL}${path}${buildQuery(params)}`, {
    headers: {
      "Content-Type": "application/json",
      ...(session && !isPublicAuthPath ? { Authorization: `${session.tokenType} ${session.token}` } : {}),
      ...(options.headers ?? {})
    },
    ...options
  });
}

export async function apiRequest<T>(
  path: string,
  options: RequestInit = {},
  params?: Record<string, QueryValue>
): Promise<T> {
  const session = readAuthSession();
  let response = await sendRequest(path, options, params, session);

  if (!response.ok) {
    if (response.status === 401 && session && !PUBLIC_AUTH_PATHS.has(path)) {
      const refreshedSession = await refreshAuthSession(session);
      if (refreshedSession) {
        response = await sendRequest(path, options, params, refreshedSession);
      }
    }

    if (!response.ok && response.status === 401 && !PUBLIC_AUTH_PATHS.has(path)) {
      clearAuthSession();
      window.dispatchEvent(new Event("jewelflow:auth-expired"));
    }
    if (!response.ok) {
      throw await parseError(response);
    }
  }

  if (response.status === 204 || !response.headers.get("Content-Type")?.includes("application/json")) {
    return undefined as T;
  }

  return response.json() as Promise<T>;
}
