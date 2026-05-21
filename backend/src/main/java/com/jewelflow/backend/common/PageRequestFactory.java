package com.jewelflow.backend.common;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Locale;
import java.util.Map;

public final class PageRequestFactory {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;
    private static final String DEFAULT_DIRECTION = "DESC";

    private PageRequestFactory() {
    }

    public static Pageable create(
            Integer page,
            Integer size,
            String sortBy,
            String direction,
            Map<String, String> allowedSorts,
            String defaultSortBy
    ) {
        int normalizedPage = page == null ? DEFAULT_PAGE : Math.max(page, 0);
        int normalizedSize = size == null ? DEFAULT_SIZE : Math.min(Math.max(size, 1), MAX_SIZE);
        String requestedSort = isBlank(sortBy) ? defaultSortBy : sortBy.trim();
        String sortProperty = allowedSorts.get(requestedSort);
        if (sortProperty == null) {
            throw new IllegalArgumentException("Unsupported sort field: " + requestedSort);
        }
        Sort.Direction sortDirection = parseDirection(direction);
        return PageRequest.of(normalizedPage, normalizedSize, Sort.by(sortDirection, sortProperty));
    }

    private static Sort.Direction parseDirection(String direction) {
        String normalizedDirection = isBlank(direction) ? DEFAULT_DIRECTION : direction.trim().toUpperCase(Locale.ROOT);
        try {
            return Sort.Direction.fromString(normalizedDirection);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Unsupported sort direction: " + direction);
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
