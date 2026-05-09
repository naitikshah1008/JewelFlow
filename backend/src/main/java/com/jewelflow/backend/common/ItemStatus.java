package com.jewelflow.backend.common;

import java.util.Arrays;

public enum ItemStatus {
    AVAILABLE,
    RESERVED,
    SOLD;

    public static ItemStatus from(String value) {
        if (value == null || value.isBlank()) {
            return AVAILABLE;
        }
        String normalized = value.trim().toUpperCase().replace(" ", "_");
        return Arrays.stream(values())
                .filter(status -> status.name().equals(normalized))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unsupported item status: " + value + ". Supported values: AVAILABLE, RESERVED, SOLD"
                ));
    }
}
