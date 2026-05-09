package com.jewelflow.backend.invoice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    boolean existsByInvoiceNumber(String invoiceNumber);

    List<Invoice> findAllByOrderByInvoiceDateDesc();

    List<Invoice> findTop5ByOrderByInvoiceDateDesc();

    long countByInvoiceDateGreaterThanEqualAndInvoiceDateLessThan(LocalDateTime start, LocalDateTime end);

    @Query("select coalesce(sum(invoice.finalAmount), 0) from Invoice invoice")
    BigDecimal sumTotalRevenue();

    @Query("""
            select coalesce(sum(invoice.finalAmount), 0)
            from Invoice invoice
            where invoice.invoiceDate >= :start and invoice.invoiceDate < :end
            """)
    BigDecimal sumRevenueBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("""
            select invoice
            from Invoice invoice
              where (:customerName is null or lower(invoice.customerName) like :customerName)
              and (:paymentStatus is null or upper(invoice.paymentStatus) = :paymentStatus)
              and (:orderStatus is null or upper(invoice.orderStatus) = :orderStatus)
              and (
                    :keyword is null
                    or lower(invoice.invoiceNumber) like :keyword
                    or lower(invoice.customerName) like :keyword
                    or lower(invoice.customerPhoneNumber) like :keyword
                    or lower(invoice.itemName) like :keyword
                    or lower(invoice.paymentStatus) like :keyword
                    or lower(invoice.orderStatus) like :keyword
              )
            order by invoice.invoiceDate desc
            """)
    List<Invoice> searchInvoices(
            @Param("customerName") String customerName,
            @Param("paymentStatus") String paymentStatus,
            @Param("orderStatus") String orderStatus,
            @Param("keyword") String keyword
    );
}
