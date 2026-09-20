package com.buyio.catalog.controller;

import com.buyio.catalog.domain.Category;
import com.buyio.catalog.domain.Price;
import com.buyio.catalog.domain.Product;
import com.buyio.catalog.domain.Supplier;
import com.buyio.catalog.dto.CatalogEvent;
import com.buyio.catalog.repository.CategoryRepository;
import com.buyio.catalog.repository.PriceRepository;
import com.buyio.catalog.repository.ProductRepository;
import com.buyio.catalog.repository.SupplierRepository;
import com.buyio.catalog.service.CatalogEventPublisher;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
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
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final CategoryRepository categoryRepository;
    private final PriceRepository priceRepository;
    private final CatalogEventPublisher eventPublisher; // Inyección para auditoría

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAll() {
        List<ProductResponse> list = productRepository.findAll().stream().map(p -> {
            BigDecimal activePrice = p.getPrices().stream()
                    .filter(Price::getIsActive)
                    .map(Price::getUnitPrice)
                    .findFirst().orElse(BigDecimal.ZERO);
            String categoryName = p.getCategory() != null ? p.getCategory().getName() : "Sin Categoría";
            return new ProductResponse(p.getId(), p.getSku(), p.getName(), p.getDescription(), 
                                       categoryName, p.getStatus(), p.getSupplier().getTaxId(), activePrice);
        }).toList();
        return ResponseEntity.ok(list);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<Product> create(@Valid @RequestBody CreateProductRequest req) {
        Supplier supplier = supplierRepository.findById(req.supplierId())
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));

        Category category = categoryRepository.findById(req.categoryId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        Product product = Product.builder()
                .sku(req.sku())
                .name(req.name())
                .description(req.description())
                .category(category)
                .supplier(supplier)
                .status("ACTIVE")
                .build();
        Product saved = productRepository.save(product);

        Price price = Price.builder()
                .product(saved)
                .unitPrice(req.price())
                .currency("USD")
                .isActive(true)
                .validFrom(OffsetDateTime.now())
                .build();
        priceRepository.save(price);

        // PUBLICACIÓN DE EVENTO DE AUDITORÍA
        eventPublisher.publishEvent(CatalogEvent.builder()
                .eventType("PRODUCT_CREATED")
                .productId(saved.getId())
                .data(Map.of(
                    "sku", saved.getSku(),
                    "name", saved.getName(),
                    "initialPrice", req.price(),
                    "categoryId", category.getId(),
                    "supplierId", supplier.getId()
                ))
                .timestamp(System.currentTimeMillis())
                .build());

        return ResponseEntity.ok(saved);
    }

    @PatchMapping("/{id}/status")
    @Transactional
    public ResponseEntity<?> updateStatus(@PathVariable UUID id, @RequestParam String status) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        String oldStatus = product.getStatus();
        product.setStatus(status.toUpperCase());
        Product updated = productRepository.save(product);

        // PUBLICACIÓN DE EVENTO DE AUDITORÍA
        eventPublisher.publishEvent(CatalogEvent.builder()
                .eventType("PRODUCT_STATUS_UPDATED")
                .productId(updated.getId())
                .data(Map.of("oldStatus", oldStatus, "newStatus", updated.getStatus()))
                .timestamp(System.currentTimeMillis())
                .build());

        return ResponseEntity.ok(updated);
    }

    public record CreateProductRequest(
        @NotBlank String sku,
        @NotBlank String name,
        String description,
        @NotNull UUID categoryId,
        @NotNull UUID supplierId,
        @NotNull @DecimalMin("0.01") BigDecimal price
    ) {}

    public record ProductResponse(UUID id, String sku, String name, String description, 
                                  String category, String status, String supplierTaxId, BigDecimal currentPrice) {}
}