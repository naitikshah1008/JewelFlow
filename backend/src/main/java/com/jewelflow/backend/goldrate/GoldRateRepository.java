package com.jewelflow.backend.goldrate;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GoldRateRepository extends JpaRepository<GoldRate, Long> {

    List<GoldRate> findAllByOrderByRateDateDescCreatedAtDesc();

    List<GoldRate> findByMetalTypeIgnoreCaseAndPurityIgnoreCaseOrderByRateDateDescCreatedAtDesc(
            String metalType,
            String purity
    );

    Optional<GoldRate> findTopByMetalTypeIgnoreCaseAndPurityIgnoreCaseOrderByRateDateDescCreatedAtDesc(
            String metalType,
            String purity
    );
}
