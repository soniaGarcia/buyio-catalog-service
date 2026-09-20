package com.buyio.catalog.controller;

import com.buyio.catalog.domain.Category;
import com.buyio.catalog.dto.CatalogEvent;
import com.buyio.catalog.repository.CategoryRepository;
import com.buyio.catalog.repository.ProductRepository;
import com.buyio.catalog.service.CatalogEventPublisher;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final CatalogEventPublisher eventPublisher; // Inyección para auditoría

    @GetMapping
    public ResponseEntity<List<Category>> getAll(@RequestParam(required = false, defaultValue = "false") boolean activeOnly) {
        if (activeOnly) {
            return ResponseEntity.ok(categoryRepository.findByIsActiveTrue());
        }
        return ResponseEntity.ok(categoryRepository.findAll());
    }

    @PostMapping
    @Transactional
    public ResponseEntity<Category> create(@Valid @RequestBody CreateCategoryRequest req) {
        Category category = Category.builder()
                .name(req.name())
                .description(req.description())
                .isActive(true)
                .build();
        Category saved = categoryRepository.save(category);

        // PUBLICACIÓN DE EVENTO DE AUDITORÍA
        eventPublisher.publishEvent(CatalogEvent.builder()
                .eventType("CATEGORY_CREATED")
                .productId(saved.getId()) // Se envía en el campo id de auditoría
                .data(Map.of("name", saved.getName(), "description", saved.getDescription()))
                .timestamp(System.currentTimeMillis())
                .build());

        return ResponseEntity.ok(saved);
    }

    @PatchMapping("/{id}/status")
    @Transactional
    public ResponseEntity<?> toggleCategoryStatus(@PathVariable UUID id, @RequestParam boolean active) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        if (!active) {
            long associatedProductsCount = productRepository.countByCategoryId(id);
            if (associatedProductsCount > 0) {
                return ResponseEntity.badRequest().body(Map.of(
                    "message", "No se puede inactivar la categoría porque tiene " + associatedProductsCount + " producto(s) asociado(s)."
                ));
            }
        }

        category.setIsActive(active);
        Category saved = categoryRepository.save(category);

        // PUBLICACIÓN DE EVENTO DE AUDITORÍA
        eventPublisher.publishEvent(CatalogEvent.builder()
                .eventType("CATEGORY_STATUS_UPDATED")
                .productId(saved.getId())
                .data(Map.of("name", saved.getName(), "isActive", active))
                .timestamp(System.currentTimeMillis())
                .build());

        return ResponseEntity.ok(saved);
    }

    public record CreateCategoryRequest(
        @NotBlank(message = "El nombre de la categoría es requerido") String name,
        String description
    ) {}
}