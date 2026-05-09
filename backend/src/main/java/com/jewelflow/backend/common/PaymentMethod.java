package com.jewelflow.backend.common;

import java.util.Arrays;

public enum PaymentMethod {
    CASH,
    CARD,
    UPI,
    BANK_TRANSFER,
    OTHER;

    public static PaymentMethod from(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Payment method is required");
        }
        String normalized = value.trim().toUpperCase().replace(" ", "_");
        return Arrays.stream(values())
                .filter(method -> method.name().equals(normalized))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unsupported payment method: " + value
                                + ". Supported values: CASH, CARD, UPI, BANK_TRANSFER, OTHER"
                ));
    }
}
