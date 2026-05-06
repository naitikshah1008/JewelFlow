package com.jewelflow.backend.sales;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "sales")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String invoiceNumber;

    @Column(nullable = false)
    private Long customerId;

    private String customerName;
    private String customerPhoneNumber;

    @Column(nullable = false)
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

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (this.saleDate == null) {
            this.saleDate = now;
        }
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
