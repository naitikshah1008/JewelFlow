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

import java.util.List;

@Service
@RequiredArgsConstructor
public class SaleService {

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
        if (ItemStatus.from(item.getStatus()) != ItemStatus.AVAILABLE) {
            throw new IllegalArgumentException("Item is not available for sale. Current status: " + item.getStatus());
        }
        if (item.getSellingPrice() == null) {
            throw new IllegalArgumentException("Item selling price is missing. Update item pricing before creating a sale.");
        }
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
