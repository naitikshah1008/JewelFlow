package com.jewelflow.backend.invoice;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class InvoiceResponse {

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

    private Integer quantity;
    private BigDecimal grossWeight;
    private BigDecimal netWeight;
    private BigDecimal stoneWeight;
    private BigDecimal goldRatePerGram;
    private BigDecimal goldValue;
    private BigDecimal stonePrice;
    private BigDecimal makingCharges;
    private BigDecimal subtotal;
    private BigDecimal taxPercentage;
    private BigDecimal taxAmount;
    private BigDecimal discount;
    private BigDecimal unitFinalAmount;
    private BigDecimal finalAmount;

    private String orderStatus;
    private String paymentStatus;
    private String paymentMethod;
    private String notes;

    private LocalDateTime invoiceDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
