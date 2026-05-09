package com.jewelflow.backend.inventory;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class JewelleryItemRequest {

    @NotBlank(message = "Item name is required")
    private String itemName;

    @NotBlank(message = "Category is required")
    private String category;

    @NotBlank(message = "Metal type is required")
    private String metalType;

    @NotBlank(message = "Purity is required")
    private String purity;

    @NotNull(message = "Gross weight is required")
    @DecimalMin(value = "0.01", message = "Gross weight must be greater than zero")
    private BigDecimal grossWeight;

    @NotNull(message = "Net weight is required")
    @DecimalMin(value = "0.01", message = "Net weight must be greater than zero")
    private BigDecimal netWeight;

    @NotNull(message = "Stone weight is required")
    @DecimalMin(value = "0.00", message = "Stone weight cannot be negative")
    private BigDecimal stoneWeight;

    @DecimalMin(value = "0.01", message = "Gold rate per gram must be greater than zero")
    private BigDecimal goldRatePerGram;

    @NotNull(message = "Stone price is required")
    @DecimalMin(value = "0.00", message = "Stone price cannot be negative")
    private BigDecimal stonePrice;

    @NotNull(message = "Making charges are required")
    @DecimalMin(value = "0.00", message = "Making charges cannot be negative")
    private BigDecimal makingCharges;

    @NotNull(message = "Tax percentage is required")
    @DecimalMin(value = "0.00", message = "Tax percentage cannot be negative")
    private BigDecimal taxPercentage;

    @NotNull(message = "Discount is required")
    @DecimalMin(value = "0.00", message = "Discount cannot be negative")
    private BigDecimal discount;

    @NotNull(message = "Purchase cost is required")
    @DecimalMin(value = "0.00", message = "Purchase cost cannot be negative")
    private BigDecimal purchaseCost;

    private String status;
}
