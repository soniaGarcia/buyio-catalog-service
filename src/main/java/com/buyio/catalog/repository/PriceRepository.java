package com.buyio.catalog.repository;

import com.buyio.catalog.domain.Price;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PriceRepository extends JpaRepository<Price, UUID> {

    Optional<Price> findByProductIdAndIsActiveTrue(UUID productId);

    List<Price> findByProductIdOrderByValidFromDesc(UUID productId);

    @Modifying
    @Query("UPDATE Price p SET p.isActive = false, p.validTo = :now WHERE p.product.id = :productId AND p.isActive = true")
    void deactivateActivePrices(@Param("productId") UUID productId, @Param("now") OffsetDateTime now);
}