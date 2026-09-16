package com.buyio.catalog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CatalogEvent {
    private String eventType; // PRODUCT_CREATED, PRODUCT_UPDATED, PRICE_UPDATED
    private UUID productId;
    private String sku;
    private String name;
    private BigDecimal price;
    private String currency;
    private UUID supplierId;
    private OffsetDateTime timestamp;
}
