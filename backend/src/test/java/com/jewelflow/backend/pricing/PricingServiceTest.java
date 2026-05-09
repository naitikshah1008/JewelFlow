package com.jewelflow.backend.pricing;

import com.jewelflow.backend.goldrate.GoldRateService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class PricingServiceTest {

    private final GoldRateService goldRateService = mock(GoldRateService.class);
    private final PricingService pricingService = new PricingService(goldRateService);

    @Test
    void calculatePriceRejectsDiscountGreaterThanPreTaxSubtotal() {
        PricingRequest request = baseRequest();
        request.setDiscount(BigDecimal.valueOf(126));

        assertThatThrownBy(() -> pricingService.calculatePrice(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Discount cannot exceed subtotal before tax: 125.00");
    }

    @Test
    void calculatePriceAllowsDiscountEqualToPreTaxSubtotal() {
        PricingRequest request = baseRequest();
        request.setDiscount(BigDecimal.valueOf(125));

        PricingResponse response = pricingService.calculatePrice(request);

        assertThat(response.getSubtotal()).isEqualByComparingTo("0.00");
        assertThat(response.getFinalPrice()).isEqualByComparingTo("0.00");
    }

    private PricingRequest baseRequest() {
        PricingRequest request = new PricingRequest();
        request.setNetWeight(BigDecimal.ONE);
        request.setMetalType("Gold");
        request.setPurity("24K");
        request.setGoldRatePerGram(BigDecimal.valueOf(100));
        request.setStonePrice(BigDecimal.TEN);
        request.setMakingCharges(BigDecimal.valueOf(15));
        request.setTaxPercentage(BigDecimal.valueOf(3));
        request.setDiscount(BigDecimal.ZERO);
        return request;
    }
}
