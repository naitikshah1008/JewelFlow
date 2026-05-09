package com.jewelflow.backend.sales;

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

    @GetMapping("/{id}")
    public SaleResponse getSaleById(@PathVariable Long id) {
        return saleService.getSaleById(id);
    }
}
