INSERT INTO categories (name, description, is_active, created_at, updated_at) VALUES
('Cơ bi Pool', 'Cơ bi dành cho Pool 8, Pool 9', true, NOW(), NOW()),
('Cơ bi Snooker', 'Cơ bi dành cho Snooker', true, NOW(), NOW()),
('Cơ bi 3-Cushion', 'Cơ bi dành cho 3-Cushion Billiards', true, NOW(), NOW()),
('Phụ kiện', 'Các phụ kiện khác như giá đỡ, phấn...', true, NOW(), NOW());

INSERT INTO products (name, description, price, original_price, stock_quantity, sku, image_url, is_active, category_id, created_at, updated_at) VALUES
('Cơ bi Pool Premium', 'Bộ cơ bi Pool chất lượng cao, độ bền tốt', 450000, 500000, 45, 'CB-POOL-001', 'https://example.com/pool-premium.jpg', true, 1, NOW(), NOW()),
('Cơ bi Snooker Pro', 'Bộ cơ bi Snooker chuyên nghiệp', 380000, 420000, 32, 'CB-SNOOK-001', 'https://example.com/snooker-pro.jpg', true, 2, NOW(), NOW()),
('Cơ bi Carrom Classic', 'Bộ cơ bi Carrom truyền thống', 280000, 320000, 28, 'CB-CARROM-001', 'https://example.com/carrom-classic.jpg', true, 3, NOW(), NOW()),
('Cơ bi 3-Cushion Elite', 'Bộ cơ bi 3-Cushion cao cấp', 520000, 580000, 21, 'CB-3CUSH-001', 'https://example.com/3cushion-elite.jpg', true, 3, NOW(), NOW());