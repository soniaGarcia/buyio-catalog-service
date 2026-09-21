package com.buyio.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class SupplierDto {
    private UUID id;
    
    @NotBlank(message = "El NIT es obligatorio")
    @Pattern(
        regexp = "^(\\d{4}-\\d{6}-\\d{3}-\\d{1}|\\d{8}-\\d{1})$",
        message = "El NIT debe tener un formato válido de El Salvador (ej: 0614-280389-101-1 o 01234567-8)"
    )
    private String taxId;

    @NotBlank(message = "El nombre del proveedor es obligatorio")
    private String name;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El correo electrónico debe ser una dirección válida")
    private String contactEmail;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(
        regexp = "^(\\+503\\s?)?[267]\\d{3}-\\d{4}$",
        message = "El teléfono debe ser un número válido de El Salvador (ej: 7890-1234 o +503 7890-1234)"
    )
    private String phone;
    private String status;
}