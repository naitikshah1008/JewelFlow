export function formatCurrency(value: number | null | undefined): string {
  return new Intl.NumberFormat("en-US", {
    style: "currency",
    currency: "USD",
    maximumFractionDigits: 2
  }).format(Number(value ?? 0));
}

export function formatNumber(value: number | null | undefined, maximumFractionDigits = 2): string {
  return new Intl.NumberFormat("en-US", { maximumFractionDigits }).format(Number(value ?? 0));
}

export function formatDateTime(value?: string): string {
  if (!value) {
    return "Not set";
  }
  return new Intl.DateTimeFormat("en-US", {
    dateStyle: "medium",
    timeStyle: "short"
  }).format(new Date(value));
}

export function formatDate(value?: string): string {
  if (!value) {
    return "Not set";
  }
  return new Intl.DateTimeFormat("en-US", { dateStyle: "medium" }).format(new Date(value));
}

export function toNumber(value: string): number | undefined {
  if (value.trim() === "") {
    return undefined;
  }
  return Number(value);
}
