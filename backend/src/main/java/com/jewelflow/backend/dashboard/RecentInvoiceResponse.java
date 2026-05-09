package com.jewelflow.backend.dashboard;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class RecentInvoiceResponse {

    private Long id;
    private String invoiceNumber;
    private String customerName;
    private String itemName;
    private BigDecimal finalAmount;
    private String paymentStatus;
    private String orderStatus;
    private LocalDateTime invoiceDate;
}
