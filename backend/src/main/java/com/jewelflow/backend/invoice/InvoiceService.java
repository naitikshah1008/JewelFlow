package com.jewelflow.backend.invoice;

import com.jewelflow.backend.common.ItemStatus;
import com.jewelflow.backend.common.OrderStatus;
import com.jewelflow.backend.common.PaymentMethod;
import com.jewelflow.backend.common.PaymentStatus;
import com.jewelflow.backend.customer.Customer;
import com.jewelflow.backend.customer.CustomerService;
import com.jewelflow.backend.exception.ResourceNotFoundException;
import com.jewelflow.backend.inventory.JewelleryItem;
import com.jewelflow.backend.inventory.JewelleryItemRepository;
import com.jewelflow.backend.inventory.JewelleryItemService;
import com.jewelflow.backend.pricing.PricingRequest;
import com.jewelflow.backend.pricing.PricingResponse;
import com.jewelflow.backend.pricing.PricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private static final DateTimeFormatter INVOICE_NUMBER_TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    private final InvoiceRepository invoiceRepository;
    private final CustomerService customerService;
    private final JewelleryItemService jewelleryItemService;
    private final JewelleryItemRepository jewelleryItemRepository;
    private final PricingService pricingService;

    @Transactional
    public InvoiceResponse createInvoice(InvoiceRequest request) {
        Customer customer = customerService.getCustomerById(request.getCustomerId());
        JewelleryItem item = jewelleryItemService.getItemById(request.getItemId());

        ensureItemCanBeInvoiced(item);

        PaymentStatus paymentStatus = PaymentStatus.from(request.getPaymentStatus());
        PaymentMethod paymentMethod = PaymentMethod.from(request.getPaymentMethod());
        PricingResponse pricing = calculateInvoicePricing(item, request);
        ensureFinalAmountIsValid(pricing.getFinalPrice());

        Invoice invoice = Invoice.builder()
                .invoiceNumber(generateInvoiceNumber())
                .customerId(customer.getId())
                .customerName(customer.getFullName())
                .customerPhoneNumber(customer.getPhoneNumber())
                .itemId(item.getId())
                .itemName(item.getItemName())
                .category(item.getCategory())
                .metalType(item.getMetalType())
                .purity(item.getPurity())
                .quantity(request.getQuantity())
                .grossWeight(scaleAmount(item.getGrossWeight(), request.getQuantity()))
                .netWeight(scaleAmount(item.getNetWeight(), request.getQuantity()))
                .stoneWeight(scaleAmount(item.getStoneWeight(), request.getQuantity()))
                .goldRatePerGram(pricing.getGoldRatePerGram())
                .goldValue(pricing.getGoldValue())
                .stonePrice(scaleAmount(item.getStonePrice(), request.getQuantity()))
                .makingCharges(scaleAmount(item.getMakingCharges(), request.getQuantity()))
                .subtotal(pricing.getSubtotal())
                .taxPercentage(request.getTaxPercentage())
                .taxAmount(pricing.getTaxAmount())
                .discount(request.getDiscount())
                .unitFinalAmount(calculateUnitFinalAmount(pricing.getFinalPrice(), request.getQuantity()))
                .finalAmount(pricing.getFinalPrice())
                .orderStatus(OrderStatus.ISSUED.name())
                .paymentStatus(paymentStatus.name())
                .paymentMethod(paymentMethod.name())
                .notes(request.getNotes())
                .build();

        Invoice savedInvoice = invoiceRepository.save(invoice);

        item.setStatus(ItemStatus.SOLD.name());
        jewelleryItemRepository.save(item);

        return toResponse(savedInvoice);
    }

    public List<InvoiceResponse> getAllInvoices() {
        return getAllInvoices(null, null, null, null);
    }

    public List<InvoiceResponse> getAllInvoices(String customerName, String paymentStatus, String orderStatus, String keyword) {
        String normalizedPaymentStatus = normalizePaymentStatusFilter(paymentStatus);
        String normalizedOrderStatus = normalizeOrderStatusFilter(orderStatus);
        String normalizedCustomerName = normalizeLikeFilter(customerName);
        String normalizedKeyword = normalizeLikeFilter(keyword);
        return invoiceRepository.searchInvoices(
                        normalizedCustomerName,
                        normalizedPaymentStatus,
                        normalizedOrderStatus,
                        normalizedKeyword
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public InvoiceResponse getInvoiceById(Long id) {
        return toResponse(getInvoiceEntityById(id));
    }

    private Invoice getInvoiceEntityById(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));
    }

    private void ensureItemCanBeInvoiced(JewelleryItem item) {
        if (ItemStatus.from(item.getStatus()) != ItemStatus.AVAILABLE) {
            throw new IllegalArgumentException("Item is not available for invoice. Current status: " + item.getStatus());
        }
    }

    private PricingResponse calculateInvoicePricing(JewelleryItem item, InvoiceRequest request) {
        PricingRequest pricingRequest = new PricingRequest();
        pricingRequest.setMetalType(item.getMetalType());
        pricingRequest.setPurity(item.getPurity());
        pricingRequest.setGoldRatePerGram(item.getGoldRatePerGram());
        pricingRequest.setNetWeight(scaleAmount(item.getNetWeight(), request.getQuantity()));
        pricingRequest.setStonePrice(scaleAmount(item.getStonePrice(), request.getQuantity()));
        pricingRequest.setMakingCharges(scaleAmount(item.getMakingCharges(), request.getQuantity()));
        pricingRequest.setTaxPercentage(request.getTaxPercentage());
        pricingRequest.setDiscount(request.getDiscount());
        return pricingService.calculatePrice(pricingRequest);
    }

    private BigDecimal scaleAmount(BigDecimal value, Integer quantity) {
        return defaultValue(value)
                .multiply(BigDecimal.valueOf(quantity))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateUnitFinalAmount(BigDecimal finalAmount, Integer quantity) {
        return finalAmount.divide(BigDecimal.valueOf(quantity), 2, RoundingMode.HALF_UP);
    }

    private void ensureFinalAmountIsValid(BigDecimal finalAmount) {
        if (finalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Final amount must be greater than zero after discount and tax calculation");
        }
    }

    private String generateInvoiceNumber() {
        String invoiceNumber = formatInvoiceNumber();

        while (invoiceRepository.existsByInvoiceNumber(invoiceNumber)) {
            invoiceNumber = formatInvoiceNumber();
        }

        return invoiceNumber;
    }

    private String formatInvoiceNumber() {
        String timestamp = LocalDateTime.now().format(INVOICE_NUMBER_TIMESTAMP_FORMAT);
        String suffix = UUID.randomUUID().toString()
                .replace("-", "")
                .substring(0, 6)
                .toUpperCase(Locale.ROOT);
        return "JF-ORDER-" + timestamp + "-" + suffix;
    }

    private BigDecimal defaultValue(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String normalizePaymentStatusFilter(String paymentStatus) {
        return isBlank(paymentStatus) ? null : PaymentStatus.from(paymentStatus).name();
    }

    private String normalizeOrderStatusFilter(String orderStatus) {
        return isBlank(orderStatus) ? null : OrderStatus.from(orderStatus).name();
    }

    private String normalizeLikeFilter(String value) {
        return isBlank(value) ? null : "%" + value.trim().toLowerCase() + "%";
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private InvoiceResponse toResponse(Invoice invoice) {
        return InvoiceResponse.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .customerId(invoice.getCustomerId())
                .customerName(invoice.getCustomerName())
                .customerPhoneNumber(invoice.getCustomerPhoneNumber())
                .itemId(invoice.getItemId())
                .itemName(invoice.getItemName())
                .category(invoice.getCategory())
                .metalType(invoice.getMetalType())
                .purity(invoice.getPurity())
                .quantity(invoice.getQuantity())
                .grossWeight(invoice.getGrossWeight())
                .netWeight(invoice.getNetWeight())
                .stoneWeight(invoice.getStoneWeight())
                .goldRatePerGram(invoice.getGoldRatePerGram())
                .goldValue(invoice.getGoldValue())
                .stonePrice(invoice.getStonePrice())
                .makingCharges(invoice.getMakingCharges())
                .subtotal(invoice.getSubtotal())
                .taxPercentage(invoice.getTaxPercentage())
                .taxAmount(invoice.getTaxAmount())
                .discount(invoice.getDiscount())
                .unitFinalAmount(invoice.getUnitFinalAmount())
                .finalAmount(invoice.getFinalAmount())
                .orderStatus(invoice.getOrderStatus())
                .paymentStatus(invoice.getPaymentStatus())
                .paymentMethod(invoice.getPaymentMethod())
                .notes(invoice.getNotes())
                .invoiceDate(invoice.getInvoiceDate())
                .createdAt(invoice.getCreatedAt())
                .updatedAt(invoice.getUpdatedAt())
                .build();
    }
}
