package com.buyio.catalog.controller;

import com.buyio.catalog.domain.Category;
import com.buyio.catalog.repository.CategoryRepository;
import com.buyio.catalog.repository.ProductRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @GetMapping
    public ResponseEntity<List<Category>> getAll(@RequestParam(required = false, defaultValue = "false") boolean activeOnly) {
        if (activeOnly) {
            return ResponseEntity.ok(categoryRepository.findByIsActiveTrue());
        }
        return ResponseEntity.ok(categoryRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Category> create(@Valid @RequestBody CreateCategoryRequest req) {
        Category category = Category.builder()
                .name(req.name())
                .description(req.description())
                .isActive(true)
                .build();
        return ResponseEntity.ok(categoryRepository.save(category));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> toggleCategoryStatus(@PathVariable UUID id, @RequestParam boolean active) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        // Regla de Negocio: No inactivar si tiene productos asociados
        if (!active) {
            long associatedProductsCount = productRepository.countByCategoryId(id);
            if (associatedProductsCount > 0) {
                return ResponseEntity.badRequest().body(Map.of(
                    "message", "No se puede inactivar la categoría porque tiene " + associatedProductsCount + " producto(s) asociado(s)."
                ));
            }
        }

        category.setIsActive(active);
        return ResponseEntity.ok(categoryRepository.save(category));
    }

    public record CreateCategoryRequest(
        @NotBlank(message = "El nombre de la categoría es requerido") String name,
        String description
    ) {}
}