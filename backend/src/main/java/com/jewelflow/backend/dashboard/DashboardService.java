package com.jewelflow.backend.dashboard;

import com.jewelflow.backend.common.ItemStatus;
import com.jewelflow.backend.customer.CustomerRepository;
import com.jewelflow.backend.inventory.JewelleryItemRepository;
import com.jewelflow.backend.invoice.Invoice;
import com.jewelflow.backend.invoice.InvoiceRepository;
import com.jewelflow.backend.sales.Sale;
import com.jewelflow.backend.sales.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final CustomerRepository customerRepository;
    private final JewelleryItemRepository jewelleryItemRepository;
    private final SaleRepository saleRepository;
    private final InvoiceRepository invoiceRepository;

    public DashboardSummaryResponse getSummary() {
        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime tomorrowStart = today.plusDays(1).atStartOfDay();
        LocalDateTime monthStart = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime nextMonthStart = today.plusMonths(1).withDayOfMonth(1).atStartOfDay();

        long totalSalesCount = saleRepository.count();
        long totalInvoicesCount = invoiceRepository.count();
        BigDecimal totalSalesRevenue = saleRepository.sumTotalRevenue();
        BigDecimal totalInvoiceRevenue = invoiceRepository.sumTotalRevenue();
        BigDecimal todaySalesRevenue = saleRepository.sumRevenueBetween(todayStart, tomorrowStart);
        BigDecimal todayInvoiceRevenue = invoiceRepository.sumRevenueBetween(todayStart, tomorrowStart);
        BigDecimal monthlySalesRevenue = saleRepository.sumRevenueBetween(monthStart, nextMonthStart);
        BigDecimal monthlyInvoiceRevenue = invoiceRepository.sumRevenueBetween(monthStart, nextMonthStart);

        return DashboardSummaryResponse.builder()
                .totalCustomers(customerRepository.count())
                .totalInventoryItems(jewelleryItemRepository.count())
                .availableItems(jewelleryItemRepository.countByStatusIgnoreCase(ItemStatus.AVAILABLE.name()))
                .reservedItems(jewelleryItemRepository.countByStatusIgnoreCase(ItemStatus.RESERVED.name()))
                .soldItems(jewelleryItemRepository.countByStatusIgnoreCase(ItemStatus.SOLD.name()))
                .activeInventoryValue(jewelleryItemRepository.sumActiveInventoryValue())
                .availableInventoryValue(jewelleryItemRepository.sumInventoryValueByStatus(ItemStatus.AVAILABLE.name()))
                .reservedInventoryValue(jewelleryItemRepository.sumInventoryValueByStatus(ItemStatus.RESERVED.name()))
                .totalSalesCount(totalSalesCount)
                .totalInvoicesCount(totalInvoicesCount)
                .totalBillingCount(totalSalesCount + totalInvoicesCount)
                .totalRevenue(totalSalesRevenue.add(totalInvoiceRevenue))
                .totalSalesRevenue(totalSalesRevenue)
                .totalInvoiceRevenue(totalInvoiceRevenue)
                .todaySalesCount(saleRepository.countBySaleDateGreaterThanEqualAndSaleDateLessThan(todayStart, tomorrowStart))
                .todayInvoicesCount(invoiceRepository.countByInvoiceDateGreaterThanEqualAndInvoiceDateLessThan(todayStart, tomorrowStart))
                .todayRevenue(todaySalesRevenue.add(todayInvoiceRevenue))
                .todaySalesRevenue(todaySalesRevenue)
                .todayInvoiceRevenue(todayInvoiceRevenue)
                .monthlySalesCount(saleRepository.countBySaleDateGreaterThanEqualAndSaleDateLessThan(monthStart, nextMonthStart))
                .monthlyInvoicesCount(invoiceRepository.countByInvoiceDateGreaterThanEqualAndInvoiceDateLessThan(monthStart, nextMonthStart))
                .monthlyRevenue(monthlySalesRevenue.add(monthlyInvoiceRevenue))
                .monthlySalesRevenue(monthlySalesRevenue)
                .monthlyInvoiceRevenue(monthlyInvoiceRevenue)
                .recentSales(getRecentSales())
                .recentInvoices(getRecentInvoices())
                .build();
    }

    private List<RecentSaleResponse> getRecentSales() {
        return saleRepository.findTop5ByOrderBySaleDateDesc()
                .stream()
                .map(this::toRecentSaleResponse)
                .toList();
    }

    private List<RecentInvoiceResponse> getRecentInvoices() {
        return invoiceRepository.findTop5ByOrderByInvoiceDateDesc()
                .stream()
                .map(this::toRecentInvoiceResponse)
                .toList();
    }

    private RecentSaleResponse toRecentSaleResponse(Sale sale) {
        return RecentSaleResponse.builder()
                .id(sale.getId())
                .invoiceNumber(sale.getInvoiceNumber())
                .customerName(sale.getCustomerName())
                .itemName(sale.getItemName())
                .finalAmount(sale.getFinalAmount())
                .paymentStatus(sale.getPaymentStatus())
                .saleDate(sale.getSaleDate())
                .build();
    }

    private RecentInvoiceResponse toRecentInvoiceResponse(Invoice invoice) {
        return RecentInvoiceResponse.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .customerName(invoice.getCustomerName())
                .itemName(invoice.getItemName())
                .finalAmount(invoice.getFinalAmount())
                .paymentStatus(invoice.getPaymentStatus())
                .orderStatus(invoice.getOrderStatus())
                .invoiceDate(invoice.getInvoiceDate())
                .build();
    }
}
