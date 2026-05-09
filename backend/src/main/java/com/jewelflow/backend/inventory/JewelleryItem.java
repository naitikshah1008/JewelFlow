package com.jewelflow.backend.inventory;

import com.jewelflow.backend.common.ItemStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "jewellery_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JewelleryItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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
    private BigDecimal goldValue;
    private BigDecimal taxAmount;
    private BigDecimal purchaseCost;
    private BigDecimal sellingPrice;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null || this.status.isBlank()) {
            this.status = ItemStatus.AVAILABLE.name();
        }
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
