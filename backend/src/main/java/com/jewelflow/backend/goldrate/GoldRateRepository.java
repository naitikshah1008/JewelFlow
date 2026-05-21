package com.jewelflow.backend.goldrate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Query("""
            select rate
            from GoldRate rate
            where (:metalType is null or upper(rate.metalType) = :metalType)
              and (:purity is null or upper(rate.purity) = :purity)
            """)
    Page<GoldRate> searchGoldRatesPage(
            @Param("metalType") String metalType,
            @Param("purity") String purity,
            Pageable pageable
    );
}
