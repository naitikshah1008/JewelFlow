package com.jewelflow.backend.pricing;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pricing")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PricingController {

    private final PricingService pricingService;

    @PostMapping("/calculate")
    public PricingResponse calculatePrice(@Valid @RequestBody PricingRequest request) {
        return pricingService.calculatePrice(request);
    }
}
