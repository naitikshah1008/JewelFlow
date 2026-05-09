package com.jewelflow.backend.dashboard;

import com.jewelflow.backend.customer.CustomerRepository;
import com.jewelflow.backend.inventory.JewelleryItemRepository;
import com.jewelflow.backend.invoice.InvoiceRepository;
import com.jewelflow.backend.sales.SaleRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DashboardServiceTest {

    private final CustomerRepository customerRepository = mock(CustomerRepository.class);
    private final JewelleryItemRepository jewelleryItemRepository = mock(JewelleryItemRepository.class);
    private final SaleRepository saleRepository = mock(SaleRepository.class);
    private final InvoiceRepository invoiceRepository = mock(InvoiceRepository.class);

    private final DashboardService dashboardService = new DashboardService(
            customerRepository,
            jewelleryItemRepository,
            saleRepository,
            invoiceRepository
    );

    @Test
    void getSummaryCombinesSaleAndInvoiceRevenue() {
        when(customerRepository.count()).thenReturn(3L);
        when(jewelleryItemRepository.count()).thenReturn(7L);
        when(jewelleryItemRepository.countByStatusIgnoreCase("AVAILABLE")).thenReturn(4L);
        when(jewelleryItemRepository.countByStatusIgnoreCase("RESERVED")).thenReturn(1L);
        when(jewelleryItemRepository.countByStatusIgnoreCase("SOLD")).thenReturn(2L);
        when(jewelleryItemRepository.sumActiveInventoryValue()).thenReturn(BigDecimal.valueOf(100000));
        when(jewelleryItemRepository.sumInventoryValueByStatus("AVAILABLE")).thenReturn(BigDecimal.valueOf(80000));
        when(jewelleryItemRepository.sumInventoryValueByStatus("RESERVED")).thenReturn(BigDecimal.valueOf(20000));
        when(saleRepository.count()).thenReturn(2L);
        when(invoiceRepository.count()).thenReturn(5L);
        when(saleRepository.sumTotalRevenue()).thenReturn(BigDecimal.valueOf(25000));
        when(invoiceRepository.sumTotalRevenue()).thenReturn(BigDecimal.valueOf(75000));
        when(saleRepository.sumRevenueBetween(any(), any())).thenReturn(BigDecimal.valueOf(10000));
        when(invoiceRepository.sumRevenueBetween(any(), any())).thenReturn(BigDecimal.valueOf(15000));
        when(saleRepository.findTop5ByOrderBySaleDateDesc()).thenReturn(List.of());
        when(invoiceRepository.findTop5ByOrderByInvoiceDateDesc()).thenReturn(List.of());

        DashboardSummaryResponse summary = dashboardService.getSummary();

        assertThat(summary.getTotalCustomers()).isEqualTo(3);
        assertThat(summary.getAvailableItems()).isEqualTo(4);
        assertThat(summary.getTotalSalesCount()).isEqualTo(2);
        assertThat(summary.getTotalInvoicesCount()).isEqualTo(5);
        assertThat(summary.getTotalBillingCount()).isEqualTo(7);
        assertThat(summary.getTotalRevenue()).isEqualByComparingTo("100000");
        assertThat(summary.getTodayRevenue()).isEqualByComparingTo("25000");
        assertThat(summary.getMonthlyRevenue()).isEqualByComparingTo("25000");
    }
}
