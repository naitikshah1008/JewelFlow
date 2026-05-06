package com.jewelflow.backend.goldrate;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "gold_rates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoldRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String metalType;

    @Column(nullable = false)
    private String purity;

    @Column(nullable = false)
    private BigDecimal ratePerGram;

    @Column(nullable = false)
    private LocalDate rateDate;

    private String source;
    private String notes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
