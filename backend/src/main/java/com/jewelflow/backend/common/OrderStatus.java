package com.jewelflow.backend.common;

import java.util.Arrays;

public enum OrderStatus {
    ISSUED;

    public static OrderStatus from(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Order status is required");
        }
        String normalized = value.trim().toUpperCase().replace(" ", "_");
        return Arrays.stream(values())
                .filter(status -> status.name().equals(normalized))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unsupported order status: " + value + ". Supported values: ISSUED"
                ));
    }
}
