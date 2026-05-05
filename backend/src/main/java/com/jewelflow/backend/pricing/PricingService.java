package com.jewelflow.backend.pricing;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class PricingService {

    public PricingResponse calculatePrice(PricingRequest request) {
        BigDecimal purityFactor = getPurityFactor(request.getPurity());
        BigDecimal netWeight = defaultValue(request.getNetWeight());
        BigDecimal goldRatePerGram = defaultValue(request.getGoldRatePerGram());
        BigDecimal stonePrice = defaultValue(request.getStonePrice());
        BigDecimal makingCharges = defaultValue(request.getMakingCharges());
        BigDecimal taxPercentage = defaultValue(request.getTaxPercentage());
        BigDecimal discount = defaultValue(request.getDiscount());
        BigDecimal goldValue = netWeight.multiply(goldRatePerGram).multiply(purityFactor).setScale(2, RoundingMode.HALF_UP);
        BigDecimal subtotal = goldValue.add(stonePrice).add(makingCharges).subtract(discount).setScale(2, RoundingMode.HALF_UP);
        BigDecimal taxAmount = subtotal.multiply(taxPercentage).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal finalPrice = subtotal.add(taxAmount).setScale(2, RoundingMode.HALF_UP);
        return PricingResponse.builder().purityFactor(purityFactor).goldValue(goldValue).stonePrice(stonePrice).makingCharges(makingCharges).subtotal(subtotal).taxAmount(taxAmount).discount(discount).finalPrice(finalPrice) .build();
    }

    private BigDecimal getPurityFactor(String purity) {
        if (purity == null) {
            throw new IllegalArgumentException("Purity is required");
        }
        return switch (purity.toUpperCase()) {
            case "24K" -> BigDecimal.valueOf(24).divide(BigDecimal.valueOf(24), 6, RoundingMode.HALF_UP);
            case "22K" -> BigDecimal.valueOf(22).divide(BigDecimal.valueOf(24), 6, RoundingMode.HALF_UP);
            case "18K" -> BigDecimal.valueOf(18).divide(BigDecimal.valueOf(24), 6, RoundingMode.HALF_UP);
            case "14K" -> BigDecimal.valueOf(14).divide(BigDecimal.valueOf(24), 6, RoundingMode.HALF_UP);
            default -> throw new IllegalArgumentException("Unsupported purity: " + purity);
        };
    }

    private BigDecimal defaultValue(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}