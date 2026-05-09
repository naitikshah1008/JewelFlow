package com.jewelflow.backend.pricing;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PricingRequest {

    @NotNull(message = "Net weight is required")
    @DecimalMin(value = "0.01", message = "Net weight must be greater than zero")
    private BigDecimal netWeight;

    private String metalType;

    @NotBlank(message = "Purity is required")
    private String purity;

    @DecimalMin(value = "0.01", message = "Gold rate per gram must be greater than zero")
    private BigDecimal goldRatePerGram;

    @NotNull(message = "Stone price is required")
    @DecimalMin(value = "0.00", message = "Stone price cannot be negative")
    private BigDecimal stonePrice;

    @NotNull(message = "Making charges are required")
    @DecimalMin(value = "0.00", message = "Making charges cannot be negative")
    private BigDecimal makingCharges;

    @NotNull(message = "Tax percentage is required")
    @DecimalMin(value = "0.00", message = "Tax percentage cannot be negative")
    private BigDecimal taxPercentage;

    @NotNull(message = "Discount is required")
    @DecimalMin(value = "0.00", message = "Discount cannot be negative")
    private BigDecimal discount;
}
