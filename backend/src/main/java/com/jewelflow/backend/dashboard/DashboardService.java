package com.jewelflow.backend.dashboard;

import com.jewelflow.backend.common.ItemStatus;
import com.jewelflow.backend.customer.CustomerRepository;
import com.jewelflow.backend.inventory.JewelleryItemRepository;
import com.jewelflow.backend.sales.Sale;
import com.jewelflow.backend.sales.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final CustomerRepository customerRepository;
    private final JewelleryItemRepository jewelleryItemRepository;
    private final SaleRepository saleRepository;

    public DashboardSummaryResponse getSummary() {
        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime tomorrowStart = today.plusDays(1).atStartOfDay();
        LocalDateTime monthStart = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime nextMonthStart = today.plusMonths(1).withDayOfMonth(1).atStartOfDay();

        return DashboardSummaryResponse.builder()
                .totalCustomers(customerRepository.count())
                .totalInventoryItems(jewelleryItemRepository.count())
                .availableItems(jewelleryItemRepository.countByStatusIgnoreCase(ItemStatus.AVAILABLE.name()))
                .reservedItems(jewelleryItemRepository.countByStatusIgnoreCase(ItemStatus.RESERVED.name()))
                .soldItems(jewelleryItemRepository.countByStatusIgnoreCase(ItemStatus.SOLD.name()))
                .activeInventoryValue(jewelleryItemRepository.sumActiveInventoryValue())
                .availableInventoryValue(jewelleryItemRepository.sumInventoryValueByStatus(ItemStatus.AVAILABLE.name()))
                .reservedInventoryValue(jewelleryItemRepository.sumInventoryValueByStatus(ItemStatus.RESERVED.name()))
                .totalSalesCount(saleRepository.count())
                .totalRevenue(saleRepository.sumTotalRevenue())
                .todaySalesCount(saleRepository.countBySaleDateGreaterThanEqualAndSaleDateLessThan(todayStart, tomorrowStart))
                .todayRevenue(saleRepository.sumRevenueBetween(todayStart, tomorrowStart))
                .monthlySalesCount(saleRepository.countBySaleDateGreaterThanEqualAndSaleDateLessThan(monthStart, nextMonthStart))
                .monthlyRevenue(saleRepository.sumRevenueBetween(monthStart, nextMonthStart))
                .recentSales(getRecentSales())
                .build();
    }

    private List<RecentSaleResponse> getRecentSales() {
        return saleRepository.findTop5ByOrderBySaleDateDesc()
                .stream()
                .map(this::toRecentSaleResponse)
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
}
