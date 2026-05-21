package com.jewelflow.backend.invoice;

import com.jewelflow.backend.common.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping
    public InvoiceResponse createInvoice(@Valid @RequestBody InvoiceRequest request) {
        return invoiceService.createInvoice(request);
    }

    @GetMapping
    public List<InvoiceResponse> getAllInvoices(
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String paymentStatus,
            @RequestParam(required = false) String orderStatus,
            @RequestParam(required = false) String keyword
    ) {
        return invoiceService.getAllInvoices(customerName, paymentStatus, orderStatus, keyword);
    }

    @GetMapping("/page")
    public PageResponse<InvoiceResponse> getInvoicesPage(
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String paymentStatus,
            @RequestParam(required = false) String orderStatus,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String direction
    ) {
        return invoiceService.getInvoicesPage(
                customerName,
                paymentStatus,
                orderStatus,
                keyword,
                page,
                size,
                sortBy,
                direction
        );
    }

    @GetMapping("/{id}")
    public InvoiceResponse getInvoiceById(@PathVariable Long id) {
        return invoiceService.getInvoiceById(id);
    }
}
