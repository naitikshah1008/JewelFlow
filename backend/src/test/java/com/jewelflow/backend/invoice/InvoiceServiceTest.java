package com.jewelflow.backend.invoice;

import com.jewelflow.backend.customer.Customer;
import com.jewelflow.backend.customer.CustomerService;
import com.jewelflow.backend.inventory.JewelleryItem;
import com.jewelflow.backend.inventory.JewelleryItemRepository;
import com.jewelflow.backend.inventory.JewelleryItemService;
import com.jewelflow.backend.pricing.PricingResponse;
import com.jewelflow.backend.pricing.PricingService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InvoiceServiceTest {

    private final InvoiceRepository invoiceRepository = mock(InvoiceRepository.class);
    private final CustomerService customerService = mock(CustomerService.class);
    private final JewelleryItemService jewelleryItemService = mock(JewelleryItemService.class);
    private final JewelleryItemRepository jewelleryItemRepository = mock(JewelleryItemRepository.class);
    private final PricingService pricingService = mock(PricingService.class);

    private final InvoiceService invoiceService = new InvoiceService(
            invoiceRepository,
            customerService,
            jewelleryItemService,
            jewelleryItemRepository,
            pricingService
    );

    @Test
    void createInvoiceCreatesOrderNumberAndMarksItemSold() {
        Customer customer = customer();
        JewelleryItem item = availableItem();
        when(customerService.getCustomerById(1L)).thenReturn(customer);
        when(jewelleryItemService.getItemById(10L)).thenReturn(item);
        when(pricingService.calculatePrice(any())).thenReturn(pricing());
        when(invoiceRepository.existsByInvoiceNumber(any())).thenReturn(false);
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(invocation -> {
            Invoice invoice = invocation.getArgument(0);
            invoice.setId(100L);
            invoice.onCreate();
            return invoice;
        });

        InvoiceResponse response = invoiceService.createInvoice(request());

        assertThat(response.getInvoiceNumber()).startsWith("JF-ORDER-");
        assertThat(response.getFinalAmount()).isEqualByComparingTo("59868.75");
        assertThat(item.getStatus()).isEqualTo("SOLD");
        verify(jewelleryItemRepository).save(item);
        verify(invoiceRepository, never()).count();
    }

    @Test
    void createInvoiceRejectsSoldItem() {
        JewelleryItem item = availableItem();
        item.setStatus("SOLD");
        when(customerService.getCustomerById(1L)).thenReturn(customer());
        when(jewelleryItemService.getItemById(10L)).thenReturn(item);

        assertThatThrownBy(() -> invoiceService.createInvoice(request()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Item is not available for invoice. Current status: SOLD");

        verify(invoiceRepository, never()).save(any());
        verify(jewelleryItemRepository, never()).save(any());
    }

    private InvoiceRequest request() {
        InvoiceRequest request = new InvoiceRequest();
        request.setCustomerId(1L);
        request.setItemId(10L);
        request.setQuantity(1);
        request.setTaxPercentage(BigDecimal.valueOf(3));
        request.setDiscount(BigDecimal.valueOf(1000));
        request.setPaymentStatus("PAID");
        request.setPaymentMethod("CASH");
        request.setNotes("Demo invoice");
        return request;
    }

    private Customer customer() {
        return Customer.builder()
                .id(1L)
                .fullName("Rahul Mehta")
                .phoneNumber("9876543210")
                .build();
    }

    private JewelleryItem availableItem() {
        return JewelleryItem.builder()
                .id(10L)
                .itemName("Gold Ring")
                .category("Ring")
                .metalType("GOLD")
                .purity("22K")
                .grossWeight(BigDecimal.valueOf(10.50))
                .netWeight(BigDecimal.valueOf(9.80))
                .stoneWeight(BigDecimal.valueOf(0.70))
                .goldRatePerGram(BigDecimal.valueOf(6500))
                .stonePrice(BigDecimal.valueOf(1500))
                .makingCharges(BigDecimal.valueOf(2500))
                .status("AVAILABLE")
                .build();
    }

    private PricingResponse pricing() {
        return PricingResponse.builder()
                .goldRatePerGram(BigDecimal.valueOf(6500))
                .goldValue(BigDecimal.valueOf(58391.67))
                .stonePrice(BigDecimal.valueOf(1500))
                .makingCharges(BigDecimal.valueOf(2500))
                .subtotal(BigDecimal.valueOf(61391.67))
                .taxAmount(BigDecimal.valueOf(1841.75))
                .discount(BigDecimal.valueOf(1000))
                .finalPrice(BigDecimal.valueOf(59868.75))
                .build();
    }
}
