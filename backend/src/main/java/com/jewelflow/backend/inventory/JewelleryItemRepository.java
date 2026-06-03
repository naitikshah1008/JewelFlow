package com.jewelflow.backend.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface JewelleryItemRepository extends JpaRepository<JewelleryItem, Long> {

    long countByStatusIgnoreCase(String status);

    long countByArchivedFalse();

    long countByStatusIgnoreCaseAndArchivedFalse(String status);

    @Query("select coalesce(sum(item.sellingPrice), 0) from JewelleryItem item where item.archived = false and (item.status is null or upper(item.status) <> 'SOLD')")
    BigDecimal sumActiveInventoryValue();

    @Query("select coalesce(sum(item.sellingPrice), 0) from JewelleryItem item where item.archived = false and upper(item.status) = upper(:status)")
    BigDecimal sumInventoryValueByStatus(@Param("status") String status);

    @Query("""
            select item
            from JewelleryItem item
            where (:includeArchived = true or item.archived = false)
              and (:status is null or upper(item.status) = :status)
              and (:category is null or upper(item.category) = :category)
              and (:metalType is null or upper(item.metalType) = :metalType)
              and (:purity is null or upper(item.purity) = :purity)
              and (
                    :keyword is null
                    or lower(item.itemName) like :keyword
                    or lower(item.category) like :keyword
                    or lower(item.metalType) like :keyword
                    or lower(item.purity) like :keyword
                    or lower(item.status) like :keyword
              )
            order by item.createdAt desc
            """)
    List<JewelleryItem> searchItems(
            @Param("status") String status,
            @Param("category") String category,
            @Param("metalType") String metalType,
            @Param("purity") String purity,
            @Param("keyword") String keyword,
            @Param("includeArchived") boolean includeArchived
    );

    @Query("""
            select item
            from JewelleryItem item
            where (:includeArchived = true or item.archived = false)
              and (:status is null or upper(item.status) = :status)
              and (:category is null or upper(item.category) = :category)
              and (:metalType is null or upper(item.metalType) = :metalType)
              and (:purity is null or upper(item.purity) = :purity)
              and (
                    :keyword is null
                    or lower(item.itemName) like :keyword
                    or lower(item.category) like :keyword
                    or lower(item.metalType) like :keyword
                    or lower(item.purity) like :keyword
                    or lower(item.status) like :keyword
              )
            """)
    Page<JewelleryItem> searchItemsPage(
            @Param("status") String status,
            @Param("category") String category,
            @Param("metalType") String metalType,
            @Param("purity") String purity,
            @Param("keyword") String keyword,
            @Param("includeArchived") boolean includeArchived,
            Pageable pageable
    );
}
