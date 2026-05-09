package com.jewelflow.backend.common;

import java.util.Arrays;

public enum PaymentStatus {
    PAID,
    UNPAID,
    PARTIAL;

    public static PaymentStatus from(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Payment status is required");
        }
        String normalized = value.trim().toUpperCase().replace(" ", "_");
        return Arrays.stream(values())
                .filter(status -> status.name().equals(normalized))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unsupported payment status: " + value + ". Supported values: PAID, UNPAID, PARTIAL"
                ));
    }
}
