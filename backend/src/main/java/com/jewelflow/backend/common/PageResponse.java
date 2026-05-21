package com.jewelflow.backend.common;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.function.Function;

@Getter
@Builder
public class PageResponse<T> {

    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
    private String sortBy;
    private String direction;

    public static <T> PageResponse<T> from(Page<T> page) {
        return from(page, Function.identity());
    }

    public static <T, R> PageResponse<R> from(Page<T> page, Function<T, R> mapper) {
        Sort.Order sortOrder = page.getSort()
                .stream()
                .findFirst()
                .orElse(null);

        return PageResponse.<R>builder()
                .content(page.getContent().stream().map(mapper).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .sortBy(sortOrder == null ? null : sortOrder.getProperty())
                .direction(sortOrder == null ? null : sortOrder.getDirection().name())
                .build();
    }
}
