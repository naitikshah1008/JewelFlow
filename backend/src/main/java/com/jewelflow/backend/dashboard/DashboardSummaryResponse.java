package com.jewelflow.backend.dashboard;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class DashboardSummaryResponse {

    private long totalCustomers;
    private long totalInventoryItems;
    private long availableItems;
    private long reservedItems;
    private long soldItems;

    private BigDecimal activeInventoryValue;
    private BigDecimal availableInventoryValue;
    private BigDecimal reservedInventoryValue;

    private long totalSalesCount;
    private long totalInvoicesCount;
    private long totalBillingCount;
    private BigDecimal totalRevenue;
    private BigDecimal totalSalesRevenue;
    private BigDecimal totalInvoiceRevenue;

    private long todaySalesCount;
    private long todayInvoicesCount;
    private BigDecimal todayRevenue;
    private BigDecimal todaySalesRevenue;
    private BigDecimal todayInvoiceRevenue;

    private long monthlySalesCount;
    private long monthlyInvoicesCount;
    private BigDecimal monthlyRevenue;
    private BigDecimal monthlySalesRevenue;
    private BigDecimal monthlyInvoiceRevenue;

    private List<RecentSaleResponse> recentSales;
    private List<RecentInvoiceResponse> recentInvoices;
}
