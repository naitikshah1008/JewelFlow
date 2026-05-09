package com.jewelflow.backend.common;

import java.util.Arrays;

public enum MetalType {
    GOLD,
    SILVER,
    PLATINUM;

    public static MetalType from(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Metal type is required");
        }
        String normalized = value.trim().toUpperCase().replace(" ", "_");
        return Arrays.stream(values())
                .filter(metalType -> metalType.name().equals(normalized))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unsupported metal type: " + value + ". Supported values: GOLD, SILVER, PLATINUM"
                ));
    }
}
