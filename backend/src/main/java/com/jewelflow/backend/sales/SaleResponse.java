package com.jewelflow.backend.sales;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class SaleResponse {

    private Long id;
    private String invoiceNumber;

    private Long customerId;
    private String customerName;
    private String customerPhoneNumber;

    private Long itemId;
    private String itemName;
    private String category;
    private String metalType;
    private String purity;

    private BigDecimal grossWeight;
    private BigDecimal netWeight;
    private BigDecimal stoneWeight;
    private BigDecimal goldRatePerGram;
    private BigDecimal goldValue;
    private BigDecimal stonePrice;
    private BigDecimal makingCharges;
    private BigDecimal taxPercentage;
    private BigDecimal taxAmount;
    private BigDecimal discount;
    private BigDecimal finalAmount;

    private String paymentStatus;
    private String paymentMethod;
    private String notes;

    private LocalDateTime saleDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
