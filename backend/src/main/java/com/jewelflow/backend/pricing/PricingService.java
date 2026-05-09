package com.jewelflow.backend.pricing;

import com.jewelflow.backend.common.MetalType;
import com.jewelflow.backend.common.Purity;
import com.jewelflow.backend.goldrate.GoldRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class PricingService {

    private static final MetalType DEFAULT_METAL_TYPE = MetalType.GOLD;

    private final GoldRateService goldRateService;

    public PricingResponse calculatePrice(PricingRequest request) {
        Purity purity = Purity.from(request.getPurity());
        if (request.getMetalType() != null && !request.getMetalType().isBlank()) {
            MetalType.from(request.getMetalType());
        }
        BigDecimal purityFactor = purity.getFactor();
        BigDecimal netWeight = defaultValue(request.getNetWeight());
        BigDecimal goldRatePerGram = resolveGoldRatePerGram(request, purity);
        BigDecimal stonePrice = defaultValue(request.getStonePrice());
        BigDecimal makingCharges = defaultValue(request.getMakingCharges());
        BigDecimal taxPercentage = defaultValue(request.getTaxPercentage());
        BigDecimal discount = defaultValue(request.getDiscount());
        BigDecimal goldValue = netWeight.multiply(goldRatePerGram).multiply(purityFactor).setScale(2, RoundingMode.HALF_UP);
        BigDecimal subtotal = goldValue.add(stonePrice).add(makingCharges).subtract(discount).setScale(2, RoundingMode.HALF_UP);
        BigDecimal taxAmount = subtotal.multiply(taxPercentage).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal finalPrice = subtotal.add(taxAmount).setScale(2, RoundingMode.HALF_UP);
        return PricingResponse.builder()
                .purityFactor(purityFactor)
                .goldRatePerGram(goldRatePerGram)
                .goldValue(goldValue)
                .stonePrice(stonePrice)
                .makingCharges(makingCharges)
                .subtotal(subtotal)
                .taxAmount(taxAmount)
                .discount(discount)
                .finalPrice(finalPrice)
                .build();
    }

    private BigDecimal resolveGoldRatePerGram(PricingRequest request, Purity purity) {
        if (request.getGoldRatePerGram() != null) {
            return request.getGoldRatePerGram();
        }

        MetalType metalType = request.getMetalType() == null || request.getMetalType().isBlank()
                ? DEFAULT_METAL_TYPE
                : MetalType.from(request.getMetalType());

        return goldRateService.getLatestRatePerGram(metalType.name(), purity.getCode());
    }

    private BigDecimal defaultValue(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
