package com.jewelflow.backend.inventory;

import com.jewelflow.backend.pricing.PricingService;
import com.jewelflow.backend.pricing.PricingResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

    @Test
    void createItemStoresCalculatedPricingValues() {
        JewelleryItemRequest request = baseRequest();
        PricingResponse pricing = PricingResponse.builder()
                .goldRatePerGram(BigDecimal.valueOf(6500))
                .goldValue(BigDecimal.valueOf(55125))
                .taxAmount(BigDecimal.valueOf(1743.75))
                .finalPrice(BigDecimal.valueOf(59868.75))
                .build();
        when(pricingService.calculatePrice(any())).thenReturn(pricing);
        when(repository.save(any(JewelleryItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        JewelleryItem item = jewelleryItemService.createItem(request);

        assertThat(item.getGoldRatePerGram()).isEqualByComparingTo("6500");
        assertThat(item.getGoldValue()).isEqualByComparingTo("55125");
        assertThat(item.getTaxAmount()).isEqualByComparingTo("1743.75");
        assertThat(item.getSellingPrice()).isEqualByComparingTo("59868.75");
        assertThat(item.getStatus()).isEqualTo("AVAILABLE");
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
