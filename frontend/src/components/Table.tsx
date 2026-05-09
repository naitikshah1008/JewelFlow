import type { ReactNode } from "react";
import { EmptyState } from "./EmptyState";

interface TableProps {
  columns: string[];
  children: ReactNode;
  empty?: boolean;
  emptyTitle?: string;
}

export function Table({ columns, children, empty, emptyTitle = "No records found" }: TableProps) {
  if (empty) {
    return <EmptyState title={emptyTitle} />;
  }

  return (
    <div className="table-wrap">
      <table>
        <thead>
          <tr>
            {columns.map((column) => (
              <th key={column}>{column}</th>
            ))}
          </tr>
        </thead>
        <tbody>{children}</tbody>
      </table>
    </div>
  );
}
