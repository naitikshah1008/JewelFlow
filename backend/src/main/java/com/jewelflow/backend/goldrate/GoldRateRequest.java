package com.jewelflow.backend.goldrate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class GoldRateRequest {

    @NotBlank(message = "Metal type is required")
    private String metalType;

    @NotBlank(message = "Purity is required")
    private String purity;

    @NotNull(message = "Rate per gram is required")
    @Positive(message = "Rate per gram must be greater than zero")
    private BigDecimal ratePerGram;

    @NotNull(message = "Rate date is required")
    private LocalDate rateDate;

    private String source;
    private String notes;
}
