# buyio-catalog-service

buyio-catalog-service/
├── pom.xml
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── buyio/
        │           └── catalog/
        │               ├── BuyioCatalogServiceApplication.java
        │               ├── config/
        │               │   └── SecurityConfig.java
        │               ├── security/
        │               │   └── JwtAuthenticationFilter.java
        │               ├── domain/
        │               │   ├── Supplier.java
        │               │   ├── Product.java
        │               │   └── Price.java
        │               ├── dto/
        │               │   ├── SupplierDto.java
        │               │   ├── ProductDto.java
        │               │   ├── PriceDto.java
        │               │   └── CatalogEvent.java
        │               ├── repository/
        │               │   ├── SupplierRepository.java
        │               │   ├── ProductRepository.java
        │               │   └── PriceRepository.java
        │               ├── service/
        │               │   ├── SupplierService.java
        │               │   ├── ProductService.java
        │               │   └── CatalogEventPublisher.java
        │               └── web/
        │                   ├── SupplierController.java
        │                   └── ProductController.java
        └── resources/
            └── application.yml