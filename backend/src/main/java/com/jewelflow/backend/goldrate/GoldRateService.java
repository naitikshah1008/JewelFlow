package com.jewelflow.backend.goldrate;

import com.jewelflow.backend.common.MetalType;
import com.jewelflow.backend.common.Purity;
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
        MetalType metalType = MetalType.from(request.getMetalType());
        Purity purity = Purity.from(request.getPurity());
        GoldRate goldRate = GoldRate.builder()
                .metalType(metalType.name())
                .purity(purity.getCode())
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
                        MetalType.from(metalType).name(),
                        Purity.from(purity).getCode()
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
        String normalizedMetalType = MetalType.from(metalType).name();
        String normalizedPurity = Purity.from(purity).getCode();

        return goldRateRepository
                .findTopByMetalTypeIgnoreCaseAndPurityIgnoreCaseOrderByRateDateDescCreatedAtDesc(
                        normalizedMetalType,
                        normalizedPurity
                )
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No gold rate found for metal type " + normalizedMetalType + " and purity " + normalizedPurity
                ));
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
