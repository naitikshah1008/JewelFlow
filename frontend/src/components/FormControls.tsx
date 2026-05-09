import type { InputHTMLAttributes, ReactNode, SelectHTMLAttributes, TextareaHTMLAttributes } from "react";

interface FieldProps {
  label: string;
  error?: string;
  children: ReactNode;
}

export function Field({ label, error, children }: FieldProps) {
  return (
    <label className="field">
      <span>{label}</span>
      {children}
      {error && <small className="field-error">{error}</small>}
    </label>
  );
}

export function TextInput(props: InputHTMLAttributes<HTMLInputElement>) {
  return <input className="control" {...props} />;
}

export function TextArea(props: TextareaHTMLAttributes<HTMLTextAreaElement>) {
  return <textarea className="control" rows={3} {...props} />;
}

export function SelectInput(props: SelectHTMLAttributes<HTMLSelectElement>) {
  return <select className="control" {...props} />;
}
