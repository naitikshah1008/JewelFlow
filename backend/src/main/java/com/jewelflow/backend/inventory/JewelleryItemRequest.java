package com.jewelflow.backend.inventory;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class JewelleryItemRequest {
    private String itemName;
    private String category;
    private String metalType;
    private String purity;
    private BigDecimal grossWeight;
    private BigDecimal netWeight;
    private BigDecimal stoneWeight;
    private BigDecimal goldRatePerGram;
    private BigDecimal stonePrice;
    private BigDecimal makingCharges;
    private BigDecimal taxPercentage;
    private BigDecimal discount;
    private BigDecimal purchaseCost;
    private String status;
}