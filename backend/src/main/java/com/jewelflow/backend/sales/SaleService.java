package com.jewelflow.backend.sales;

import com.jewelflow.backend.common.ItemStatus;
import com.jewelflow.backend.common.PaymentMethod;
import com.jewelflow.backend.common.PaymentStatus;
import com.jewelflow.backend.customer.Customer;
import com.jewelflow.backend.customer.CustomerService;
import com.jewelflow.backend.exception.ResourceNotFoundException;
import com.jewelflow.backend.inventory.JewelleryItem;
import com.jewelflow.backend.inventory.JewelleryItemRepository;
import com.jewelflow.backend.inventory.JewelleryItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SaleService {

    private static final DateTimeFormatter SALE_NUMBER_TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    private final SaleRepository saleRepository;
    private final CustomerService customerService;
    private final JewelleryItemService jewelleryItemService;
    private final JewelleryItemRepository jewelleryItemRepository;

    @Transactional
    public SaleResponse createSale(SaleRequest request) {
        Customer customer = customerService.getCustomerById(request.getCustomerId());
        JewelleryItem item = jewelleryItemService.getItemById(request.getItemId());

        ensureItemCanBeSold(item);

        PaymentStatus paymentStatus = PaymentStatus.from(request.getPaymentStatus());
        PaymentMethod paymentMethod = PaymentMethod.from(request.getPaymentMethod());

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
                .paymentStatus(paymentStatus.name())
                .paymentMethod(paymentMethod.name())
                .notes(request.getNotes())
                .build();

        Sale savedSale = saleRepository.save(sale);

        item.setStatus(ItemStatus.SOLD.name());
        jewelleryItemRepository.save(item);

        return toResponse(savedSale);
    }

    public List<SaleResponse> getAllSales() {
        return getAllSales(null, null, null);
    }

    public List<SaleResponse> getAllSales(String customerName, String paymentStatus, String keyword) {
        String normalizedCustomerName = normalizeLikeFilter(customerName);
        String normalizedPaymentStatus = normalizePaymentStatusFilter(paymentStatus);
        String normalizedKeyword = normalizeLikeFilter(keyword);
        return saleRepository.searchSales(normalizedCustomerName, normalizedPaymentStatus, normalizedKeyword)
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
        if (ItemStatus.from(item.getStatus()) != ItemStatus.AVAILABLE) {
            throw new IllegalArgumentException("Item is not available for sale. Current status: " + item.getStatus());
        }
        if (item.getSellingPrice() == null) {
            throw new IllegalArgumentException("Item selling price is missing. Update item pricing before creating a sale.");
        }
    }

    private String generateInvoiceNumber() {
        String invoiceNumber = formatInvoiceNumber();

        while (saleRepository.existsByInvoiceNumber(invoiceNumber)) {
            invoiceNumber = formatInvoiceNumber();
        }

        return invoiceNumber;
    }

    private String formatInvoiceNumber() {
        String timestamp = LocalDateTime.now().format(SALE_NUMBER_TIMESTAMP_FORMAT);
        String suffix = UUID.randomUUID().toString()
                .replace("-", "")
                .substring(0, 6)
                .toUpperCase(Locale.ROOT);
        return "JF-INV-" + timestamp + "-" + suffix;
    }

    private String normalizePaymentStatusFilter(String paymentStatus) {
        return isBlank(paymentStatus) ? null : PaymentStatus.from(paymentStatus).name();
    }

    private String normalizeLikeFilter(String value) {
        return isBlank(value) ? null : "%" + value.trim().toLowerCase() + "%";
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
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
