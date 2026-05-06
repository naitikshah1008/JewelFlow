package com.jewelflow.backend.sales;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    boolean existsByInvoiceNumber(String invoiceNumber);

    long countBySaleDateGreaterThanEqualAndSaleDateLessThan(LocalDateTime start, LocalDateTime end);

    List<Sale> findTop5ByOrderBySaleDateDesc();

    @Query("select coalesce(sum(sale.finalAmount), 0) from Sale sale")
    BigDecimal sumTotalRevenue();

    @Query("""
            select coalesce(sum(sale.finalAmount), 0)
            from Sale sale
            where sale.saleDate >= :start and sale.saleDate < :end
            """)
    BigDecimal sumRevenueBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
