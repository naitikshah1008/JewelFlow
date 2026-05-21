package com.jewelflow.backend.sales;

import com.jewelflow.backend.common.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SaleController {

    private final SaleService saleService;

    @PostMapping
    public SaleResponse createSale(@Valid @RequestBody SaleRequest request) {
        return saleService.createSale(request);
    }

    @GetMapping
    public List<SaleResponse> getAllSales(
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String paymentStatus,
            @RequestParam(required = false) String keyword
    ) {
        return saleService.getAllSales(customerName, paymentStatus, keyword);
    }

    @GetMapping("/page")
    public PageResponse<SaleResponse> getSalesPage(
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String paymentStatus,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String direction
    ) {
        return saleService.getSalesPage(customerName, paymentStatus, keyword, page, size, sortBy, direction);
    }

    @GetMapping("/{id}")
    public SaleResponse getSaleById(@PathVariable Long id) {
        return saleService.getSaleById(id);
    }
}
