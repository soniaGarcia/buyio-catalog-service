package com.buyio.catalog.controller;

import com.buyio.catalog.domain.Category;
import com.buyio.catalog.repository.CategoryRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryRepository categoryRepository;

    @GetMapping
    public ResponseEntity<List<Category>> getAll() {
        return ResponseEntity.ok(categoryRepository.findByIsActiveTrue());
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

    public record CreateCategoryRequest(
        @NotBlank String name,
        String description
    ) {}
}