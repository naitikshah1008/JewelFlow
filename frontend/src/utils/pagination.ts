import type { PageQuery, PageResponse, SortDirection } from "../types";

export const DEFAULT_PAGE_SIZE = 10;

export function createPageQuery(sortBy: string, direction: SortDirection = "DESC"): PageQuery {
  return {
    page: 0,
    size: DEFAULT_PAGE_SIZE,
    sortBy,
    direction
  };
}

export function emptyPage<T>(query: PageQuery): PageResponse<T> {
  return {
    content: [],
    page: query.page,
    size: query.size,
    totalElements: 0,
    totalPages: 0,
    first: true,
    last: true,
    sortBy: query.sortBy,
    direction: query.direction
  };
}

export function toPageParams(query: PageQuery): Record<string, string | number> {
  return {
    page: query.page,
    size: query.size,
    sortBy: query.sortBy,
    direction: query.direction
  };
}

export function resetPage(query: PageQuery): PageQuery {
  return { ...query, page: 0 };
}
