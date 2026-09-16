package com.buyio.catalog.controller;

import com.buyio.catalog.dto.PriceDto;
import com.buyio.catalog.dto.ProductDto;
import com.buyio.catalog.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody ProductDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(dto));
    }

    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping("/{id}/prices")
    public ResponseEntity<PriceDto> updatePrice(@PathVariable UUID id, @Valid @RequestBody PriceDto priceDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.updatePrice(id, priceDto));
    }

    @GetMapping("/{id}/prices/history")
    public ResponseEntity<List<PriceDto>> getPriceHistory(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.getPriceHistory(id));
    }
}