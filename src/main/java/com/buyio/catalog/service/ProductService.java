package com.buyio.catalog.service;

import com.buyio.catalog.domain.Category;
import com.buyio.catalog.domain.Price;
import com.buyio.catalog.domain.Product;
import com.buyio.catalog.domain.Supplier;
import com.buyio.catalog.dto.CatalogEvent;
import com.buyio.catalog.dto.PriceDto;
import com.buyio.catalog.dto.ProductDto;
import com.buyio.catalog.repository.PriceRepository;
import com.buyio.catalog.repository.ProductRepository;
import com.buyio.catalog.repository.SupplierRepository;
import com.buyio.catalog.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final CategoryRepository categoryRepository;
    private final PriceRepository priceRepository;
    private final CatalogEventPublisher eventPublisher;

    @Transactional
    public ProductDto createProduct(ProductDto dto) {
        if (productRepository.existsBySku(dto.getSku())) {
            throw new IllegalArgumentException("El SKU ya está registrado.");
        }
        Supplier supplier = supplierRepository.findById(dto.getSupplierId())
                .orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado."));
        
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada."));

        Product product = Product.builder()
                .supplier(supplier)
                .sku(dto.getSku())
                .name(dto.getName())
                .description(dto.getDescription())
                .category(category)
                .unitOfMeasure(dto.getUnitOfMeasure())
                .build();

        if (dto.getCurrentPrice() != null) {
            Price price = Price.builder()
                    .product(product)
                    .unitPrice(dto.getCurrentPrice())
                    .currency(dto.getCurrency() != null ? dto.getCurrency() : "USD")
                    .validFrom(OffsetDateTime.now())
                    .isActive(true)
                    .build();
            product.getPrices().add(price);
        }

        Product saved = productRepository.save(product);

        eventPublisher.publishEvent(CatalogEvent.builder()
                .eventType("PRODUCT_CREATED")
                .productId(saved.getId())
                .sku(saved.getSku())
                .name(saved.getName())
                .price(dto.getCurrentPrice())
                .currency(dto.getCurrency())
                .supplierId(supplier.getId())
                .timestamp(OffsetDateTime.now())
                .build());

        return mapToDto(saved);
    }

    @Transactional
    public PriceDto updatePrice(UUID productId, PriceDto priceDto) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado."));

        OffsetDateTime now = OffsetDateTime.now();
        priceRepository.deactivateActivePrices(productId, now);

        Price newPrice = Price.builder()
                .product(product)
                .unitPrice(priceDto.getUnitPrice())
                .currency(priceDto.getCurrency() != null ? priceDto.getCurrency() : "USD")
                .validFrom(now)
                .isActive(true)
                .build();

        Price saved = priceRepository.save(newPrice);

        eventPublisher.publishEvent(CatalogEvent.builder()
                .eventType("PRICE_UPDATED")
                .productId(product.getId())
                .sku(product.getSku())
                .name(product.getName())
                .price(saved.getUnitPrice())
                .currency(saved.getCurrency())
                .supplierId(product.getSupplier().getId())
                .timestamp(now)
                .build());

        return PriceDto.builder()
                .id(saved.getId())
                .productId(productId)
                .unitPrice(saved.getUnitPrice())
                .currency(saved.getCurrency())
                .validFrom(saved.getValidFrom())
                .isActive(saved.getIsActive())
                .build();
    }

    @Transactional(readOnly = true)
    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream().map(this::mapToDto).toList();
    }

    @Transactional(readOnly = true)
    public ProductDto getProductById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado."));
        return mapToDto(product);
    }

    @Transactional(readOnly = true)
    public List<PriceDto> getPriceHistory(UUID productId) {
        return priceRepository.findByProductIdOrderByValidFromDesc(productId).stream()
                .map(p -> PriceDto.builder()
                        .id(p.getId())
                        .productId(productId)
                        .unitPrice(p.getUnitPrice())
                        .currency(p.getCurrency())
                        .validFrom(p.getValidFrom())
                        .validTo(p.getValidTo())
                        .isActive(p.getIsActive())
                        .build())
                .toList();
    }

    private ProductDto mapToDto(Product product) {
        Price activePrice = priceRepository.findByProductIdAndIsActiveTrue(product.getId()).orElse(null);
        return ProductDto.builder()
                .id(product.getId())
                .supplierId(product.getSupplier().getId())
                .sku(product.getSku())
                .name(product.getName())
                .description(product.getDescription())
                .categoryId(product.getCategory().getId())
                .unitOfMeasure(product.getUnitOfMeasure())
                .status(product.getStatus())
                .currentPrice(activePrice != null ? activePrice.getUnitPrice() : null)
                .currency(activePrice != null ? activePrice.getCurrency() : null)
                .build();
    }
}