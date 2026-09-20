package com.buyio.catalog.controller;

import com.buyio.catalog.domain.Price;
import com.buyio.catalog.domain.PriceRequest;
import com.buyio.catalog.domain.Product;
import com.buyio.catalog.dto.CatalogEvent;
import com.buyio.catalog.repository.PriceRepository;
import com.buyio.catalog.repository.ProductRepository;
import com.buyio.catalog.service.CatalogEventPublisher;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products/{productId}/prices")
@RequiredArgsConstructor
public class PriceController {

    private final ProductRepository productRepository;
    private final PriceRepository priceRepository;
    private final CatalogEventPublisher eventPublisher; // Inyección para auditoría

    public record PriceResponse(
        UUID id,
        BigDecimal unitPrice,
        String currency,
        Boolean isActive,
        OffsetDateTime validFrom,
        OffsetDateTime validTo
    ) {
        public static PriceResponse fromEntity(Price price) {
            return new PriceResponse(
                price.getId(),
                price.getUnitPrice(),
                price.getCurrency(),
                price.getIsActive(),
                price.getValidFrom(),
                price.getValidTo()
            );
        }
    }

    @PostMapping
    @Transactional
    public ResponseEntity<PriceResponse> updatePrice(
            @PathVariable UUID productId, 
            @Valid @RequestBody PriceRequest request) {
            
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        OffsetDateTime now = OffsetDateTime.now();

        // 1. Desactivar precios anteriores
        priceRepository.deactivateActivePrices(productId, now);

        // 2. Crear nuevo precio
        Price price = Price.builder()
                .product(product)
                .unitPrice(request.unitPrice())
                .currency(request.currency() != null ? request.currency() : "USD")
                .isActive(true)
                .validFrom(now)
                .build();

        Price saved = priceRepository.save(price);

        // PUBLICACIÓN DE EVENTO DE AUDITORÍA
        eventPublisher.publishEvent(CatalogEvent.builder()
                .eventType("PRICE_UPDATED")
                .productId(productId)
                .data(Map.of(
                    "priceId", saved.getId(),
                    "newUnitPrice", saved.getUnitPrice(),
                    "currency", saved.getCurrency()
                ))
                .timestamp(System.currentTimeMillis())
                .build());

        return ResponseEntity.ok(PriceResponse.fromEntity(saved));
    }

    @GetMapping
    public ResponseEntity<List<PriceResponse>> getPriceHistory(@PathVariable UUID productId) {
        List<PriceResponse> history = priceRepository.findByProductIdOrderByValidFromDesc(productId)
                .stream()
                .map(PriceResponse::fromEntity)
                .toList();

        return ResponseEntity.ok(history);
    }
}