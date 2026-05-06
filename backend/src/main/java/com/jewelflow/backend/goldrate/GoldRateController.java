package com.jewelflow.backend.goldrate;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gold-rates")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GoldRateController {

    private final GoldRateService goldRateService;

    @PostMapping
    public GoldRateResponse createGoldRate(@Valid @RequestBody GoldRateRequest request) {
        return goldRateService.createGoldRate(request);
    }

    @GetMapping
    public List<GoldRateResponse> getAllGoldRates(
            @RequestParam(required = false) String metalType,
            @RequestParam(required = false) String purity
    ) {
        if (metalType != null && purity != null) {
            return goldRateService.getGoldRatesByMetalAndPurity(metalType, purity);
        }
        return goldRateService.getAllGoldRates();
    }

    @GetMapping("/latest")
    public GoldRateResponse getLatestGoldRate(
            @RequestParam String metalType,
            @RequestParam String purity
    ) {
        return goldRateService.getLatestGoldRate(metalType, purity);
    }

    @GetMapping("/{id}")
    public GoldRateResponse getGoldRateById(@PathVariable Long id) {
        return goldRateService.getGoldRateById(id);
    }
}
