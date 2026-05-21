package com.jewelflow.backend.common;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PageRequestFactoryTest {

    private static final Map<String, String> ALLOWED_SORTS = Map.of(
            "createdAt", "createdAt",
            "name", "fullName"
    );

    @Test
    void createClampsPageSizeAndUsesDefaultSort() {
        Pageable pageable = PageRequestFactory.create(-2, 500, null, null, ALLOWED_SORTS, "createdAt");

        assertThat(pageable.getPageNumber()).isZero();
        assertThat(pageable.getPageSize()).isEqualTo(100);
        assertThat(pageable.getSort().getOrderFor("createdAt")).isNotNull();
        assertThat(pageable.getSort().getOrderFor("createdAt").getDirection()).isEqualTo(Sort.Direction.DESC);
    }

    @Test
    void createRejectsUnsupportedSortField() {
        assertThatThrownBy(() -> PageRequestFactory.create(0, 20, "missing", "ASC", ALLOWED_SORTS, "createdAt"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported sort field");
    }
}
