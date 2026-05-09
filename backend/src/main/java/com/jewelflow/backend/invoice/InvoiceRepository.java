package com.jewelflow.backend.invoice;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    boolean existsByInvoiceNumber(String invoiceNumber);

    List<Invoice> findAllByOrderByInvoiceDateDesc();
}
