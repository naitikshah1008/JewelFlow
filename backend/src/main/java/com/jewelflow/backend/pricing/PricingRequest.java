package com.jewelflow.backend.pricing;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PricingRequest {

    private BigDecimal netWeight;
    private String metalType;
    private String purity;
    private BigDecimal goldRatePerGram;
    private BigDecimal stonePrice;
    private BigDecimal makingCharges;
    private BigDecimal taxPercentage;
    private BigDecimal discount;
}
