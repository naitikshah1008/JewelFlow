package com.jewelflow.backend.goldrate;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class GoldRateResponse {

    private Long id;
    private String metalType;
    private String purity;
    private BigDecimal ratePerGram;
    private LocalDate rateDate;
    private String source;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
