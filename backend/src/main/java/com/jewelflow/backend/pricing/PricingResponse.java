package com.jewelflow.backend.pricing;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class PricingResponse {
    private BigDecimal purityFactor;
    private BigDecimal goldRatePerGram;
    private BigDecimal goldValue;
    private BigDecimal stonePrice;
    private BigDecimal makingCharges;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal discount;
    private BigDecimal finalPrice;
}
