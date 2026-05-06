package com.jewelflow.backend.goldrate;

import com.jewelflow.backend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GoldRateService {

    private final GoldRateRepository goldRateRepository;

    public GoldRateResponse createGoldRate(GoldRateRequest request) {
        GoldRate goldRate = GoldRate.builder()
                .metalType(normalizeMetalType(request.getMetalType()))
                .purity(normalizePurity(request.getPurity()))
                .ratePerGram(request.getRatePerGram())
                .rateDate(request.getRateDate())
                .source(request.getSource())
                .notes(request.getNotes())
                .build();

        return toResponse(goldRateRepository.save(goldRate));
    }

    public List<GoldRateResponse> getAllGoldRates() {
        return goldRateRepository.findAllByOrderByRateDateDescCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<GoldRateResponse> getGoldRatesByMetalAndPurity(String metalType, String purity) {
        return goldRateRepository.findByMetalTypeIgnoreCaseAndPurityIgnoreCaseOrderByRateDateDescCreatedAtDesc(
                        normalizeMetalType(metalType),
                        normalizePurity(purity)
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public GoldRateResponse getGoldRateById(Long id) {
        return toResponse(getGoldRateEntityById(id));
    }

    public GoldRateResponse getLatestGoldRate(String metalType, String purity) {
        return toResponse(getLatestGoldRateEntity(metalType, purity));
    }

    public BigDecimal getLatestRatePerGram(String metalType, String purity) {
        return getLatestGoldRateEntity(metalType, purity).getRatePerGram();
    }

    private GoldRate getGoldRateEntityById(Long id) {
        return goldRateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gold rate not found with id: " + id));
    }

    private GoldRate getLatestGoldRateEntity(String metalType, String purity) {
        String normalizedMetalType = normalizeMetalType(metalType);
        String normalizedPurity = normalizePurity(purity);

        return goldRateRepository
                .findTopByMetalTypeIgnoreCaseAndPurityIgnoreCaseOrderByRateDateDescCreatedAtDesc(
                        normalizedMetalType,
                        normalizedPurity
                )
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No gold rate found for metal type " + normalizedMetalType + " and purity " + normalizedPurity
                ));
    }

    private String normalizeMetalType(String metalType) {
        if (metalType == null || metalType.isBlank()) {
            throw new IllegalArgumentException("Metal type is required");
        }
        return metalType.trim().toUpperCase();
    }

    private String normalizePurity(String purity) {
        if (purity == null || purity.isBlank()) {
            throw new IllegalArgumentException("Purity is required");
        }
        return purity.trim().toUpperCase();
    }

    private GoldRateResponse toResponse(GoldRate goldRate) {
        return GoldRateResponse.builder()
                .id(goldRate.getId())
                .metalType(goldRate.getMetalType())
                .purity(goldRate.getPurity())
                .ratePerGram(goldRate.getRatePerGram())
                .rateDate(goldRate.getRateDate())
                .source(goldRate.getSource())
                .notes(goldRate.getNotes())
                .createdAt(goldRate.getCreatedAt())
                .updatedAt(goldRate.getUpdatedAt())
                .build();
    }
}
