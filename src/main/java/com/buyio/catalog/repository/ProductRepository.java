package com.buyio.catalog.repository;

import com.buyio.catalog.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    boolean existsBySku(String sku);
    Optional<Product> findBySku(String sku);

    long countByCategoryId(UUID categoryId);
    boolean existsByCategoryId(UUID categoryId);
}