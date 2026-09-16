package com.buyio.catalog.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class PriceDto {
    private UUID id;
    private UUID productId;

    @NotNull(message = "El precio unitario es obligatorio")
    @Positive(message = "El precio debe ser un valor positivo")
    private BigDecimal unitPrice;

    private String currency;
    private OffsetDateTime validFrom;
    private OffsetDateTime validTo;
    private Boolean isActive;
}