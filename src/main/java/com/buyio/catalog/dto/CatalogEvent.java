package com.buyio.catalog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CatalogEvent {
    private String eventType;
    private UUID productId; // Mantiene compatibilidad con el extractor del audit-service
    private Object data;
    private String actionUser;
    private Long timestamp;
}