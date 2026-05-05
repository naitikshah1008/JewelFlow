package com.jewelflow.backend.inventory;

import com.jewelflow.backend.pricing.PricingRequest;
import com.jewelflow.backend.pricing.PricingResponse;
import com.jewelflow.backend.pricing.PricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.jewelflow.backend.exception.ResourceNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JewelleryItemService {

    private final JewelleryItemRepository repository;
    private final PricingService pricingService;

    public JewelleryItem createItem(JewelleryItemRequest request) {
        PricingResponse pricing = calculatePricing(request);
        JewelleryItem item = JewelleryItem.builder()
                .itemName(request.getItemName())
                .category(request.getCategory())
                .metalType(request.getMetalType())
                .purity(request.getPurity())
                .grossWeight(request.getGrossWeight())
                .netWeight(request.getNetWeight())
                .stoneWeight(request.getStoneWeight())
                .goldRatePerGram(request.getGoldRatePerGram())
                .stonePrice(request.getStonePrice())
                .makingCharges(request.getMakingCharges())
                .taxPercentage(request.getTaxPercentage())
                .discount(request.getDiscount())
                .goldValue(pricing.getGoldValue())
                .taxAmount(pricing.getTaxAmount())
                .purchaseCost(request.getPurchaseCost())
                .sellingPrice(pricing.getFinalPrice())
                .status(request.getStatus())
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
        PricingResponse pricing = calculatePricing(request);
        item.setItemName(request.getItemName());
        item.setCategory(request.getCategory());
        item.setMetalType(request.getMetalType());
        item.setPurity(request.getPurity());
        item.setGrossWeight(request.getGrossWeight());
        item.setNetWeight(request.getNetWeight());
        item.setStoneWeight(request.getStoneWeight());
        item.setGoldRatePerGram(request.getGoldRatePerGram());
        item.setStonePrice(request.getStonePrice());
        item.setMakingCharges(request.getMakingCharges());
        item.setTaxPercentage(request.getTaxPercentage());
        item.setDiscount(request.getDiscount());
        item.setGoldValue(pricing.getGoldValue());
        item.setTaxAmount(pricing.getTaxAmount());
        item.setPurchaseCost(request.getPurchaseCost());
        item.setSellingPrice(pricing.getFinalPrice());
        item.setStatus(request.getStatus());
        return repository.save(item);
    }

    public void deleteItem(Long id) {
        JewelleryItem item = getItemById(id);
        repository.delete(item);
    }

    private PricingResponse calculatePricing(JewelleryItemRequest request) {
        PricingRequest pricingRequest = new PricingRequest();
        pricingRequest.setNetWeight(request.getNetWeight());
        pricingRequest.setPurity(request.getPurity());
        pricingRequest.setGoldRatePerGram(request.getGoldRatePerGram());
        pricingRequest.setStonePrice(request.getStonePrice());
        pricingRequest.setMakingCharges(request.getMakingCharges());
        pricingRequest.setTaxPercentage(request.getTaxPercentage());
        pricingRequest.setDiscount(request.getDiscount());
        return pricingService.calculatePrice(pricingRequest);
    }
}