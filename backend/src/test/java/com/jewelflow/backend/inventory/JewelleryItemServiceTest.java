package com.jewelflow.backend.inventory;

import com.jewelflow.backend.pricing.PricingService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class JewelleryItemServiceTest {

    private final JewelleryItemRepository repository = mock(JewelleryItemRepository.class);
    private final PricingService pricingService = mock(PricingService.class);
    private final JewelleryItemService jewelleryItemService = new JewelleryItemService(repository, pricingService);

    @Test
    void createItemRejectsNetWeightGreaterThanGrossWeight() {
        JewelleryItemRequest request = baseRequest();
        request.setNetWeight(BigDecimal.valueOf(10.01));

        assertThatThrownBy(() -> jewelleryItemService.createItem(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Net weight cannot exceed gross weight");
    }

    @Test
    void createItemRejectsCombinedNetAndStoneWeightGreaterThanGrossWeight() {
        JewelleryItemRequest request = baseRequest();
        request.setNetWeight(BigDecimal.valueOf(9.50));
        request.setStoneWeight(BigDecimal.valueOf(0.75));

        assertThatThrownBy(() -> jewelleryItemService.createItem(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Net weight plus stone weight cannot exceed gross weight");
    }

    private JewelleryItemRequest baseRequest() {
        JewelleryItemRequest request = new JewelleryItemRequest();
        request.setItemName("Gold Ring");
        request.setCategory("Ring");
        request.setMetalType("Gold");
        request.setPurity("22K");
        request.setGrossWeight(BigDecimal.TEN);
        request.setNetWeight(BigDecimal.valueOf(9.25));
        request.setStoneWeight(BigDecimal.valueOf(0.75));
        request.setGoldRatePerGram(BigDecimal.valueOf(6500));
        request.setStonePrice(BigDecimal.valueOf(1500));
        request.setMakingCharges(BigDecimal.valueOf(2500));
        request.setTaxPercentage(BigDecimal.valueOf(3));
        request.setDiscount(BigDecimal.valueOf(1000));
        request.setPurchaseCost(BigDecimal.valueOf(58000));
        request.setStatus("AVAILABLE");
        return request;
    }
}
