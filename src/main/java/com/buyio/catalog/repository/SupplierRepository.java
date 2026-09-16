package com.buyio.catalog.repository;

import com.buyio.catalog.domain.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SupplierRepository extends JpaRepository<Supplier, UUID> {
    boolean existsByTaxId(String taxId);
}