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
    private BigDecimal totalRevenue;

    private long todaySalesCount;
    private BigDecimal todayRevenue;

    private long monthlySalesCount;
    private BigDecimal monthlyRevenue;

    private List<RecentSaleResponse> recentSales;
}
