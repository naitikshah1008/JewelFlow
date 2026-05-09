package com.jewelflow.backend.common;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

public enum Purity {
    K24("24K", 24),
    K22("22K", 22),
    K18("18K", 18),
    K14("14K", 14);

    private final String code;
    private final int karats;

    Purity(String code, int karats) {
        this.code = code;
        this.karats = karats;
    }

    public String getCode() {
        return code;
    }

    public BigDecimal getFactor() {
        return BigDecimal.valueOf(karats).divide(BigDecimal.valueOf(24), 6, RoundingMode.HALF_UP);
    }

    public static Purity from(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Purity is required");
        }
        String normalized = value.trim().toUpperCase();
        return Arrays.stream(values())
                .filter(purity -> purity.code.equals(normalized))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unsupported purity: " + value + ". Supported values: 24K, 22K, 18K, 14K"
                ));
    }
}
