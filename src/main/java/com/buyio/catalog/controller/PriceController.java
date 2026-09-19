package com.buyio.catalog.controller;

import com.buyio.catalog.domain.Price;
import com.buyio.catalog.domain.PriceRequest;
import com.buyio.catalog.domain.Product;
import com.buyio.catalog.repository.PriceRepository;
import com.buyio.catalog.repository.ProductRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products/{productId}/prices")
@RequiredArgsConstructor
public class PriceController {

    private final ProductRepository productRepository;
    private final PriceRepository priceRepository;

    @PostMapping
    @Transactional
    public ResponseEntity<Price> updatePrice(
            @PathVariable UUID productId, 
            @Valid @RequestBody PriceRequest request) {
            
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Desactivar precios anteriores
        product.getPrices().forEach(p -> {
            p.setIsActive(false);
            p.setValidTo(OffsetDateTime.now());
        });

        // Crear nuevo registro de precio activo
        Price price = Price.builder()
                .product(product)
                .unitPrice(request.unitPrice())
                .currency(request.currency() != null ? request.currency() : "USD")
                .isActive(true)
                .validFrom(OffsetDateTime.now())
                .build();

        return ResponseEntity.ok(priceRepository.save(price));
    }
}