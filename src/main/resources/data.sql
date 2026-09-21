-- 1. Categorías Iniciales
INSERT INTO categories (id, name, description, is_active, created_at, updated_at)
VALUES 
('11111111-0000-0000-0000-111111111111', 'Electrónica', 'Equipos electrónicos y accesorios', true, NOW(), NOW()),
('22222222-0000-0000-0000-222222222222', 'Oficina', 'Insumos y mobiliario de oficina', true, NOW(), NOW())
ON CONFLICT (name) DO NOTHING;

-- 2. Inserción de Proveedor Inicial
INSERT INTO suppliers (id, tax_id, name, contact_email, phone, status, created_at, updated_at)
VALUES ('11111111-1111-1111-1111-111111111111', '0614-150220-101-1', 'Tech Supplies Inc.', 'contacto@techsupplies.com', ' +503 7890-1234', 'ACTIVE', NOW(), NOW())
ON CONFLICT (tax_id) DO NOTHING;

-- 3. Productos Iniciales
INSERT INTO products (id, supplier_id, category_id, sku, name, description, unit_of_measure, status, created_at, updated_at)
VALUES
('22222222-2222-2222-2222-222222222222', '11111111-1111-1111-1111-111111111111', '11111111-0000-0000-0000-111111111111', 'LAP-PRO-15', 'Laptop Pro 15', 'Laptop de alto rendimiento 16GB RAM 512GB SSD', 'UNIT', 'ACTIVE', NOW(), NOW()),
('33333333-3333-3333-3333-333333333333', '11111111-1111-1111-1111-111111111111', '11111111-0000-0000-0000-111111111111', 'TEC-MEC-01', 'Teclado Mecánico RGB', 'Teclado mecánico switches brown', 'UNIT', 'ACTIVE', NOW(), NOW())
ON CONFLICT (sku) DO NOTHING;

-- 4. Precios Iniciales
INSERT INTO prices (id, product_id, unit_price, currency, valid_from, is_active, created_at)
VALUES
('44444444-4444-4444-4444-444444444444', '22222222-2222-2222-2222-222222222222', 1299.99, 'USD', NOW(), TRUE, NOW()),
('55555555-5555-5555-5555-555555555555', '33333333-3333-3333-3333-333333333333', 89.90, 'USD', NOW(), TRUE, NOW())
ON CONFLICT (id) DO NOTHING;