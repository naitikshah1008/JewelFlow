import type { ApiErrorBody } from "../types";
import { clearAuthSession, readAuthSession } from "../utils/auth";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";

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

export async function apiRequest<T>(
  path: string,
  options: RequestInit = {},
  params?: Record<string, QueryValue>
): Promise<T> {
  const session = readAuthSession();
  const response = await fetch(`${API_BASE_URL}${path}${buildQuery(params)}`, {
    headers: {
      "Content-Type": "application/json",
      ...(session ? { Authorization: `${session.tokenType} ${session.token}` } : {}),
      ...(options.headers ?? {})
    },
    ...options
  });

  if (!response.ok) {
    if (response.status === 401 && path !== "/api/auth/login") {
      clearAuthSession();
      window.dispatchEvent(new Event("jewelflow:auth-expired"));
    }
    let body: ApiErrorBody = {};
    try {
      body = await response.json();
    } catch {
      body = { message: response.statusText };
    }
    throw new ApiError(body.message ?? "Request failed", response.status, body.fieldErrors);
  }

  if (response.status === 204) {
    return undefined as T;
  }

  return response.json() as Promise<T>;
}
