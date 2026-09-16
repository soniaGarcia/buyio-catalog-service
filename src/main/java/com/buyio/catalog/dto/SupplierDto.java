package com.buyio.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class SupplierDto {
    private UUID id;
    
    @NotBlank(message = "El identificador fiscal (taxId) es obligatorio")
    private String taxId;

    @NotBlank(message = "El nombre del proveedor es obligatorio")
    private String name;

    private String contactEmail;
    private String phone;
    private String status;
}