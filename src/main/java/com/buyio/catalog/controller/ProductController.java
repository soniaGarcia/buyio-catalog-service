package com.buyio.catalog.controller;

import com.buyio.catalog.domain.Price;
import com.buyio.catalog.domain.Product;
import com.buyio.catalog.domain.Supplier;
import com.buyio.catalog.repository.PriceRepository;
import com.buyio.catalog.repository.ProductRepository;
import com.buyio.catalog.repository.SupplierRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final PriceRepository priceRepository;

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAll() {
        List<ProductResponse> list = productRepository.findAll().stream().map(p -> {
            BigDecimal activePrice = p.getPrices().stream()
                    .filter(Price::getIsActive)
                    .map(Price::getUnitPrice)
                    .findFirst().orElse(BigDecimal.ZERO);
            return new ProductResponse(p.getId(), p.getSku(), p.getName(), p.getDescription(), 
                                       p.getCategory(), p.getStatus(), p.getSupplier().getTaxId(), activePrice);
        }).toList();
        return ResponseEntity.ok(list);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<Product> create(@Valid @RequestBody CreateProductRequest req) {
        Supplier supplier = supplierRepository.findById(req.supplierId())
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));

        Product product = Product.builder()
                .sku(req.sku())
                .name(req.name())
                .description(req.description())
                .category(req.category())
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

        return ResponseEntity.ok(saved);
    }

    public record CreateProductRequest(
        @NotBlank String sku,
        @NotBlank String name,
        String description,
        String category,
        @NotNull UUID supplierId,
        @NotNull @DecimalMin("0.01") BigDecimal price
    ) {}

    public record ProductResponse(UUID id, String sku, String name, String description, 
                                  String category, String status, String supplierTaxId, BigDecimal currentPrice) {}
}