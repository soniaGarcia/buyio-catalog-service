package com.buyio.catalog.service;

import com.buyio.catalog.domain.Supplier;
import com.buyio.catalog.dto.SupplierDto;
import com.buyio.catalog.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;

    @Transactional
    public SupplierDto createSupplier(SupplierDto dto) {
        if (supplierRepository.existsByTaxId(dto.getTaxId())) {
            throw new IllegalArgumentException("El proveedor con Tax ID ya existe.");
        }
        Supplier supplier = Supplier.builder()
                .taxId(dto.getTaxId())
                .name(dto.getName())
                .contactEmail(dto.getContactEmail())
                .phone(dto.getPhone())
                .build();

        Supplier saved = supplierRepository.save(supplier);
        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public List<SupplierDto> getAllSuppliers() {
        return supplierRepository.findAll().stream().map(this::mapToDto).toList();
    }

    private SupplierDto mapToDto(Supplier supplier) {
        return SupplierDto.builder()
                .id(supplier.getId())
                .taxId(supplier.getTaxId())
                .name(supplier.getName())
                .contactEmail(supplier.getContactEmail())
                .phone(supplier.getPhone())
                .status(supplier.getStatus())
                .build();
    }
}