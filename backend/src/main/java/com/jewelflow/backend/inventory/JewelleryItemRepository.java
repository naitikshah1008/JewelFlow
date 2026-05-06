package com.jewelflow.backend.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface JewelleryItemRepository extends JpaRepository<JewelleryItem, Long> {

    long countByStatusIgnoreCase(String status);

    @Query("select coalesce(sum(item.sellingPrice), 0) from JewelleryItem item where item.status is null or upper(item.status) <> 'SOLD'")
    BigDecimal sumActiveInventoryValue();

    @Query("select coalesce(sum(item.sellingPrice), 0) from JewelleryItem item where upper(item.status) = upper(:status)")
    BigDecimal sumInventoryValueByStatus(@Param("status") String status);
}
