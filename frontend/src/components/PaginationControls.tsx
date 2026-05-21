import type { PageResponse } from "../types";
import { Button } from "./Button";

interface PaginationControlsProps {
  page: PageResponse<unknown>;
  onPageChange: (page: number) => void;
  onPageSizeChange: (size: number) => void;
}

export function PaginationControls({ page, onPageChange, onPageSizeChange }: PaginationControlsProps) {
  const start = page.totalElements === 0 ? 0 : page.page * page.size + 1;
  const end = Math.min((page.page + 1) * page.size, page.totalElements);

  return (
    <div className="pagination-bar">
      <span>
        Showing {start}-{end} of {page.totalElements}
      </span>
      <div className="pagination-actions">
        <label className="pagination-size">
          <span>Rows</span>
          <select
            className="control"
            value={page.size}
            onChange={(event) => onPageSizeChange(Number(event.target.value))}
          >
            <option value={10}>10</option>
            <option value={20}>20</option>
            <option value={50}>50</option>
          </select>
        </label>
        <Button type="button" variant="secondary" disabled={page.first} onClick={() => onPageChange(page.page - 1)}>
          Previous
        </Button>
        <Button type="button" variant="secondary" disabled={page.last} onClick={() => onPageChange(page.page + 1)}>
          Next
        </Button>
      </div>
    </div>
  );
}
