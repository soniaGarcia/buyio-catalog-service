## 🗄️ Diccionario de Datos (`buyio_catalog_db`)

### Tabla: `suppliers`
Almacena el catálogo de proveedores habilitados para emitir órdenes de compra.

| Campo | Tipo (Java / SQL) | Restricciones | Propósito Funcional |
| :--- | :--- | :--- | :--- |
| `id` | `Long` / `BIGINT` | PK, Auto-increment | Identificador único del proveedor. |
| `name` | `String` / `VARCHAR(100)` | NOT NULL | Razón social o nombre comercial del proveedor. |
| `email` | `String` / `VARCHAR(100)` | NOT NULL, Email Validated | Correo electrónico principal de contacto. |
| `phone` | `String` / `VARCHAR(20)` | Nullable | Teléfono de contacto. |
| `status` 🔴 | `SupplierStatus` / `VARCHAR(20)` | NOT NULL | **[ESTADO]** Estado operativo del proveedor (`ACTIVE`, `INACTIVE`). |
| `created_at` 🟡 | `LocalDateTime` / `TIMESTAMP` | NOT NULL | **[BITÁCORA]** Fecha de registro del proveedor. |
| `updated_at` 🟡 | `LocalDateTime` / `TIMESTAMP` | NOT NULL | **[BITÁCORA]** Fecha de última modificación. |

### Tabla: `products`
Almacena el catálogo principal de productos con sus precios e información base.

| Campo | Tipo (Java / SQL) | Restricciones | Propósito Funcional |
| :--- | :--- | :--- | :--- |
| `id` | `Long` / `BIGINT` | PK, Auto-increment | Identificador único del producto. |
| `code` | `String` / `VARCHAR(50)` | UNIQUE, NOT NULL | Código SKU único del producto. |
| `name` | `String` / `VARCHAR(100)` | NOT NULL | Nombre comercial del producto. |
| `description` | `String` / `TEXT` | Nullable | Descripción detallada y especificaciones. |
| `price` | `BigDecimal` / `NUMERIC(12,2)` | NOT NULL, >= 0 | Precio de venta unitario. |
| `supplier_id` | `Long` / `BIGINT` | FK (`suppliers.id`), NOT NULL | Proveedor asociado que suministra el producto. |
| `category_id` | `Long` / `BIGINT` | FK (`categories.id`), Nullable | Categoría a la que pertenece el producto. |
| `status` 🔴 | `ProductStatus` / `VARCHAR(20)` | NOT NULL | **[ESTADO]** Estado del producto en catálogo (`ACTIVE`, `INACTIVE`). |
| `created_at` 🟡 | `LocalDateTime` / `TIMESTAMP` | NOT NULL | **[BITÁCORA]** Fecha de alta del producto. |
| `updated_at` 🟡 | `LocalDateTime` / `TIMESTAMP` | NOT NULL | **[BITÁCORA]** Fecha de actualización de precio o datos. |

### Tabla: `categories`
Catálogo secundario para la clasificación general de productos.

| Campo | Tipo (Java / SQL) | Restricciones | Propósito Funcional |
| :--- | :--- | :--- | :--- |
| `id` | `Long` / `BIGINT` | PK, Auto-increment | Identificador de la categoría. |
| `name` | `String` / `VARCHAR(50)` | UNIQUE, NOT NULL | Nombre de la categoría. |
| `description` | `String` / `VARCHAR(255)` | Nullable | Descripción breve de la categoría. |
| `created_at` 🟡 | `LocalDateTime` / `TIMESTAMP` | NOT NULL | **[BITÁCORA]** Fecha de creación del registro. |
| `updated_at` 🟡 | `LocalDateTime` / `TIMESTAMP` | NOT NULL | **[BITÁCORA]** Fecha de última modificación. |