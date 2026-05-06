package com.jewelflow.backend.dashboard;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class RecentSaleResponse {

    private Long id;
    private String invoiceNumber;
    private String customerName;
    private String itemName;
    private BigDecimal finalAmount;
    private String paymentStatus;
    private LocalDateTime saleDate;
}
