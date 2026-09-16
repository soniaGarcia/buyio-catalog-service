package com.buyio.catalog.controller;

import com.buyio.catalog.domain.Supplier;
import com.buyio.catalog.repository.SupplierRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierRepository supplierRepository;

    @GetMapping
    public ResponseEntity<List<Supplier>> getAll() {
        return ResponseEntity.ok(supplierRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Supplier> create(@Valid @RequestBody SupplierDTO dto) {
        Supplier supplier = Supplier.builder()
                .taxId(dto.taxId())
                .name(dto.name())
                .contactEmail(dto.contactEmail())
                .phone(dto.phone())
                .status("ACTIVE")
                .build();
        return ResponseEntity.ok(supplierRepository.save(supplier));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Supplier> update(@PathVariable UUID id, @Valid @RequestBody SupplierDTO dto) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));
        supplier.setTaxId(dto.taxId());
        supplier.setName(dto.name());
        supplier.setContactEmail(dto.contactEmail());
        supplier.setPhone(dto.phone());
        return ResponseEntity.ok(supplierRepository.save(supplier));
    }

    public record SupplierDTO(
        @NotBlank(message = "El NIT/TaxID es obligatorio") String taxId,
        @NotBlank(message = "El nombre es obligatorio") String name,
        @Email(message = "Formato de email inválido") @NotBlank(message = "El correo es obligatorio") String contactEmail,
        String phone
    ) {}
}