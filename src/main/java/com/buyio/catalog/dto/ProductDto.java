package com.buyio.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class ProductDto {
    private UUID id;

    @NotNull(message = "El ID del proveedor es obligatorio")
    private UUID supplierId;

    @NotBlank(message = "El SKU es obligatorio")
    private String sku;

    @NotBlank(message = "El nombre del producto es obligatorio")
    private String name;

    private String description;
    private String category;
    private String unitOfMeasure;
    private String status;

    private BigDecimal currentPrice;
    private String currency;
}