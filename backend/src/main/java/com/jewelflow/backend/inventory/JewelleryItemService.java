package com.jewelflow.backend.inventory;

import com.jewelflow.backend.common.ItemStatus;
import com.jewelflow.backend.common.MetalType;
import com.jewelflow.backend.common.Purity;
import com.jewelflow.backend.pricing.PricingRequest;
import com.jewelflow.backend.pricing.PricingResponse;
import com.jewelflow.backend.pricing.PricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.jewelflow.backend.exception.ResourceNotFoundException;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JewelleryItemService {

    private final JewelleryItemRepository repository;
    private final PricingService pricingService;

    public JewelleryItem createItem(JewelleryItemRequest request) {
        validateWeightRules(request);
        MetalType metalType = MetalType.from(request.getMetalType());
        Purity purity = Purity.from(request.getPurity());
        ItemStatus status = ItemStatus.from(request.getStatus());
        PricingResponse pricing = calculatePricing(request);
        JewelleryItem item = JewelleryItem.builder()
                .itemName(request.getItemName())
                .category(request.getCategory())
                .metalType(metalType.name())
                .purity(purity.getCode())
                .grossWeight(request.getGrossWeight())
                .netWeight(request.getNetWeight())
                .stoneWeight(request.getStoneWeight())
                .goldRatePerGram(pricing.getGoldRatePerGram())
                .stonePrice(request.getStonePrice())
                .makingCharges(request.getMakingCharges())
                .taxPercentage(request.getTaxPercentage())
                .discount(request.getDiscount())
                .goldValue(pricing.getGoldValue())
                .taxAmount(pricing.getTaxAmount())
                .purchaseCost(request.getPurchaseCost())
                .sellingPrice(pricing.getFinalPrice())
                .status(status.name())
                .build();
        return repository.save(item);
    }

    public List<JewelleryItem> getAllItems() {
        return repository.findAll();
    }

    public JewelleryItem getItemById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + id));
    }

    public JewelleryItem updateItem(Long id, JewelleryItemRequest request) {
        JewelleryItem item = getItemById(id);
        validateWeightRules(request);
        MetalType metalType = MetalType.from(request.getMetalType());
        Purity purity = Purity.from(request.getPurity());
        ItemStatus status = ItemStatus.from(request.getStatus());
        PricingResponse pricing = calculatePricing(request);
        item.setItemName(request.getItemName());
        item.setCategory(request.getCategory());
        item.setMetalType(metalType.name());
        item.setPurity(purity.getCode());
        item.setGrossWeight(request.getGrossWeight());
        item.setNetWeight(request.getNetWeight());
        item.setStoneWeight(request.getStoneWeight());
        item.setGoldRatePerGram(pricing.getGoldRatePerGram());
        item.setStonePrice(request.getStonePrice());
        item.setMakingCharges(request.getMakingCharges());
        item.setTaxPercentage(request.getTaxPercentage());
        item.setDiscount(request.getDiscount());
        item.setGoldValue(pricing.getGoldValue());
        item.setTaxAmount(pricing.getTaxAmount());
        item.setPurchaseCost(request.getPurchaseCost());
        item.setSellingPrice(pricing.getFinalPrice());
        item.setStatus(status.name());
        return repository.save(item);
    }

    public void deleteItem(Long id) {
        JewelleryItem item = getItemById(id);
        repository.delete(item);
    }

    private void validateWeightRules(JewelleryItemRequest request) {
        BigDecimal grossWeight = request.getGrossWeight();
        BigDecimal netWeight = request.getNetWeight();
        BigDecimal stoneWeight = request.getStoneWeight();

        if (grossWeight == null || netWeight == null || stoneWeight == null) {
            return;
        }
        if (netWeight.compareTo(grossWeight) > 0) {
            throw new IllegalArgumentException("Net weight cannot exceed gross weight");
        }
        if (stoneWeight.compareTo(grossWeight) > 0) {
            throw new IllegalArgumentException("Stone weight cannot exceed gross weight");
        }
        if (netWeight.add(stoneWeight).compareTo(grossWeight) > 0) {
            throw new IllegalArgumentException("Net weight plus stone weight cannot exceed gross weight");
        }
    }

    private PricingResponse calculatePricing(JewelleryItemRequest request) {
        PricingRequest pricingRequest = new PricingRequest();
        pricingRequest.setNetWeight(request.getNetWeight());
        pricingRequest.setMetalType(MetalType.from(request.getMetalType()).name());
        pricingRequest.setPurity(Purity.from(request.getPurity()).getCode());
        pricingRequest.setGoldRatePerGram(request.getGoldRatePerGram());
        pricingRequest.setStonePrice(request.getStonePrice());
        pricingRequest.setMakingCharges(request.getMakingCharges());
        pricingRequest.setTaxPercentage(request.getTaxPercentage());
        pricingRequest.setDiscount(request.getDiscount());
        return pricingService.calculatePrice(pricingRequest);
    }
}
