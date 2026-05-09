interface AlertProps {
  type?: "error" | "success" | "info";
  title?: string;
  message: string;
}

export function Alert({ type = "info", title, message }: AlertProps) {
  return (
    <div className={`alert alert-${type}`} role="alert">
      {title && <strong>{title}</strong>}
      <span>{message}</span>
    </div>
  );
}
