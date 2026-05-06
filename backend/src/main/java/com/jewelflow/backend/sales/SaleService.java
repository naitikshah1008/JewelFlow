package com.jewelflow.backend.sales;

import com.jewelflow.backend.customer.Customer;
import com.jewelflow.backend.customer.CustomerService;
import com.jewelflow.backend.exception.ResourceNotFoundException;
import com.jewelflow.backend.inventory.JewelleryItem;
import com.jewelflow.backend.inventory.JewelleryItemRepository;
import com.jewelflow.backend.inventory.JewelleryItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SaleService {

    private static final String ITEM_STATUS_AVAILABLE = "AVAILABLE";
    private static final String ITEM_STATUS_SOLD = "SOLD";
    private static final Set<String> SUPPORTED_PAYMENT_STATUSES = Set.of("PAID", "UNPAID", "PARTIAL");
    private static final Set<String> SUPPORTED_PAYMENT_METHODS = Set.of("CASH", "CARD", "UPI", "BANK_TRANSFER", "OTHER");

    private final SaleRepository saleRepository;
    private final CustomerService customerService;
    private final JewelleryItemService jewelleryItemService;
    private final JewelleryItemRepository jewelleryItemRepository;

    @Transactional
    public SaleResponse createSale(SaleRequest request) {
        Customer customer = customerService.getCustomerById(request.getCustomerId());
        JewelleryItem item = jewelleryItemService.getItemById(request.getItemId());

        ensureItemCanBeSold(item);

        String paymentStatus = normalizePaymentStatus(request.getPaymentStatus());
        String paymentMethod = normalizePaymentMethod(request.getPaymentMethod());

        Sale sale = Sale.builder()
                .invoiceNumber(generateInvoiceNumber())
                .customerId(customer.getId())
                .customerName(customer.getFullName())
                .customerPhoneNumber(customer.getPhoneNumber())
                .itemId(item.getId())
                .itemName(item.getItemName())
                .category(item.getCategory())
                .metalType(item.getMetalType())
                .purity(item.getPurity())
                .grossWeight(item.getGrossWeight())
                .netWeight(item.getNetWeight())
                .stoneWeight(item.getStoneWeight())
                .goldRatePerGram(item.getGoldRatePerGram())
                .goldValue(item.getGoldValue())
                .stonePrice(item.getStonePrice())
                .makingCharges(item.getMakingCharges())
                .taxPercentage(item.getTaxPercentage())
                .taxAmount(item.getTaxAmount())
                .discount(item.getDiscount())
                .finalAmount(item.getSellingPrice())
                .paymentStatus(paymentStatus)
                .paymentMethod(paymentMethod)
                .notes(request.getNotes())
                .build();

        Sale savedSale = saleRepository.save(sale);

        item.setStatus(ITEM_STATUS_SOLD);
        jewelleryItemRepository.save(item);

        return toResponse(savedSale);
    }

    public List<SaleResponse> getAllSales() {
        return saleRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public SaleResponse getSaleById(Long id) {
        return toResponse(getSaleEntityById(id));
    }

    private Sale getSaleEntityById(Long id) {
        return saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found with id: " + id));
    }

    private void ensureItemCanBeSold(JewelleryItem item) {
        if (!ITEM_STATUS_AVAILABLE.equalsIgnoreCase(item.getStatus())) {
            throw new IllegalArgumentException("Item is not available for sale. Current status: " + item.getStatus());
        }
        if (item.getSellingPrice() == null) {
            throw new IllegalArgumentException("Item selling price is missing. Update item pricing before creating a sale.");
        }
    }

    private String normalizePaymentStatus(String paymentStatus) {
        String normalizedStatus = normalizeRequiredValue(paymentStatus, "Payment status");
        if (!SUPPORTED_PAYMENT_STATUSES.contains(normalizedStatus)) {
            throw new IllegalArgumentException("Unsupported payment status: " + paymentStatus);
        }
        return normalizedStatus;
    }

    private String normalizePaymentMethod(String paymentMethod) {
        String normalizedMethod = normalizeRequiredValue(paymentMethod, "Payment method");
        if (!SUPPORTED_PAYMENT_METHODS.contains(normalizedMethod)) {
            throw new IllegalArgumentException("Unsupported payment method: " + paymentMethod);
        }
        return normalizedMethod;
    }

    private String normalizeRequiredValue(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
        return value.trim().toUpperCase().replace(" ", "_");
    }

    private String generateInvoiceNumber() {
        long nextInvoiceNumber = saleRepository.count() + 1;
        String invoiceNumber = formatInvoiceNumber(nextInvoiceNumber);

        while (saleRepository.existsByInvoiceNumber(invoiceNumber)) {
            nextInvoiceNumber++;
            invoiceNumber = formatInvoiceNumber(nextInvoiceNumber);
        }

        return invoiceNumber;
    }

    private String formatInvoiceNumber(long invoiceNumber) {
        return String.format("JF-INV-%06d", invoiceNumber);
    }

    private SaleResponse toResponse(Sale sale) {
        return SaleResponse.builder()
                .id(sale.getId())
                .invoiceNumber(sale.getInvoiceNumber())
                .customerId(sale.getCustomerId())
                .customerName(sale.getCustomerName())
                .customerPhoneNumber(sale.getCustomerPhoneNumber())
                .itemId(sale.getItemId())
                .itemName(sale.getItemName())
                .category(sale.getCategory())
                .metalType(sale.getMetalType())
                .purity(sale.getPurity())
                .grossWeight(sale.getGrossWeight())
                .netWeight(sale.getNetWeight())
                .stoneWeight(sale.getStoneWeight())
                .goldRatePerGram(sale.getGoldRatePerGram())
                .goldValue(sale.getGoldValue())
                .stonePrice(sale.getStonePrice())
                .makingCharges(sale.getMakingCharges())
                .taxPercentage(sale.getTaxPercentage())
                .taxAmount(sale.getTaxAmount())
                .discount(sale.getDiscount())
                .finalAmount(sale.getFinalAmount())
                .paymentStatus(sale.getPaymentStatus())
                .paymentMethod(sale.getPaymentMethod())
                .notes(sale.getNotes())
                .saleDate(sale.getSaleDate())
                .createdAt(sale.getCreatedAt())
                .updatedAt(sale.getUpdatedAt())
                .build();
    }
}
