package com.buyio.catalog.controller;

import com.buyio.catalog.domain.Supplier;
import com.buyio.catalog.dto.CatalogEvent;
import com.buyio.catalog.repository.SupplierRepository;
import com.buyio.catalog.service.CatalogEventPublisher;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierRepository supplierRepository;
    private final CatalogEventPublisher eventPublisher; // Inyección para auditoría

    @GetMapping
    public ResponseEntity<List<Supplier>> getAll() {
        return ResponseEntity.ok(supplierRepository.findAll());
    }

    @PostMapping
    @Transactional
    public ResponseEntity<Supplier> create(@Valid @RequestBody SupplierDTO dto) {
        Supplier supplier = Supplier.builder()
                .taxId(dto.taxId())
                .name(dto.name())
                .contactEmail(dto.contactEmail())
                .phone(dto.phone())
                .status("ACTIVE")
                .build();
        Supplier saved = supplierRepository.save(supplier);

        // PUBLICACIÓN DE EVENTO DE AUDITORÍA
        eventPublisher.publishEvent(CatalogEvent.builder()
                .eventType("SUPPLIER_CREATED")
                .productId(saved.getId())
                .data(Map.of("taxId", saved.getTaxId(), "name", saved.getName(), "email", saved.getContactEmail()))
                .timestamp(System.currentTimeMillis())
                .build());

        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<Supplier> update(@PathVariable UUID id, @Valid @RequestBody SupplierDTO dto) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));
        supplier.setTaxId(dto.taxId());
        supplier.setName(dto.name());
        supplier.setContactEmail(dto.contactEmail());
        supplier.setPhone(dto.phone());
        Supplier updated = supplierRepository.save(supplier);

        // PUBLICACIÓN DE EVENTO DE AUDITORÍA
        eventPublisher.publishEvent(CatalogEvent.builder()
                .eventType("SUPPLIER_UPDATED")
                .productId(updated.getId())
                .data(Map.of("taxId", updated.getTaxId(), "name", updated.getName(), "email", updated.getContactEmail()))
                .timestamp(System.currentTimeMillis())
                .build());

        return ResponseEntity.ok(updated);
    }

    public record SupplierDTO(
        @NotBlank(message = "El NIT/TaxID es obligatorio") String taxId,
        @NotBlank(message = "El nombre es obligatorio") String name,
        @Email(message = "Formato de email inválido") @NotBlank(message = "El correo es obligatorio") String contactEmail,
        String phone
    ) {}
}