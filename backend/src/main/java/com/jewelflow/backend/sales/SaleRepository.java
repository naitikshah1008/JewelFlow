package com.jewelflow.backend.sales;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    boolean existsByInvoiceNumber(String invoiceNumber);
}
