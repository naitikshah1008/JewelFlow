package com.jewelflow.backend.goldrate;

import com.jewelflow.backend.common.PageResponse;
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

    @GetMapping("/page")
    public PageResponse<GoldRateResponse> getGoldRatesPage(
            @RequestParam(required = false) String metalType,
            @RequestParam(required = false) String purity,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String direction
    ) {
        return goldRateService.getGoldRatesPage(metalType, purity, page, size, sortBy, direction);
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
