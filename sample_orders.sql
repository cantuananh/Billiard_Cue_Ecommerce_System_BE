-- Sample orders data for testing the order management system
-- Run this after the application creates the tables

-- Insert sample orders
INSERT INTO orders (
    order_number, user_id, status, total_amount, shipping_fee, discount_amount, final_amount,
    shipping_name, shipping_phone, shipping_address, shipping_ward, shipping_district, shipping_province,
    payment_method, payment_status, customer_notes, created_at, updated_at
) VALUES 
(
    'ORD202501160001', 2, 'PENDING', 2500000.00, 50000.00, 0.00, 2550000.00,
    'Nguyễn Văn A', '0901234567', '123 Đường ABC', 'Phường 1', 'Quận 1', 'TP.HCM',
    'COD', 'UNPAID', 'Giao hàng giờ hành chính', NOW(), NOW()
),
(
    'ORD202501160002', 2, 'CONFIRMED', 3200000.00, 30000.00, 100000.00, 3130000.00,
    'Trần Thị B', '0912345678', '456 Đường DEF', 'Phường 2', 'Quận 2', 'TP.HCM',
    'BANK_TRANSFER', 'PAID', NULL, DATE_SUB(NOW(), INTERVAL 1 DAY), NOW()
),
(
    'ORD202501160003', 2, 'SHIPPING', 1800000.00, 25000.00, 50000.00, 1775000.00,
    'Lê Văn C', '0923456789', '789 Đường GHI', 'Phường 3', 'Quận 3', 'TP.HCM',
    'COD', 'UNPAID', 'Giao hàng cuối tuần được', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()
),
(
    'ORD202501160004', 2, 'DELIVERED', 4500000.00, 0.00, 200000.00, 4300000.00,
    'Phạm Thị D', '0934567890', '321 Đường JKL', 'Phường 4', 'Quận 4', 'TP.HCM',
    'CREDIT_CARD', 'PAID', NULL, DATE_SUB(NOW(), INTERVAL 3 DAY), NOW()
),
(
    'ORD202501160005', 2, 'CANCELLED', 1200000.00, 20000.00, 0.00, 1220000.00,
    'Hoàng Văn E', '0945678901', '654 Đường MNO', 'Phường 5', 'Quận 5', 'TP.HCM',
    'COD', 'UNPAID', NULL, DATE_SUB(NOW(), INTERVAL 4 DAY), NOW()
);

-- Get the order IDs for inserting order items
SET @order1_id = (SELECT id FROM orders WHERE order_number = 'ORD202501160001');
SET @order2_id = (SELECT id FROM orders WHERE order_number = 'ORD202501160002');
SET @order3_id = (SELECT id FROM orders WHERE order_number = 'ORD202501160003');
SET @order4_id = (SELECT id FROM orders WHERE order_number = 'ORD202501160004');
SET @order5_id = (SELECT id FROM orders WHERE order_number = 'ORD202501160005');

-- Insert sample order items (assuming we have products with IDs 1-5)
INSERT INTO order_items (
    order_id, product_id, quantity, unit_price, total_price,
    product_name, product_sku, product_image_url, created_at, updated_at
) VALUES 
-- Order 1 items
(@order1_id, 1, 1, 2500000.00, 2500000.00, 'Cơ bi Pool Premium', 'POOL001', '/uploads/products/pool-premium.jpg', NOW(), NOW()),

-- Order 2 items  
(@order2_id, 2, 2, 1600000.00, 3200000.00, 'Cơ bi Snooker Pro', 'SNOOK001', '/uploads/products/snooker-pro.jpg', NOW(), NOW()),

-- Order 3 items
(@order3_id, 3, 1, 1800000.00, 1800000.00, 'Cơ bi Carrom Classic', 'CARR001', '/uploads/products/carrom-classic.jpg', NOW(), NOW()),

-- Order 4 items
(@order4_id, 1, 1, 2500000.00, 2500000.00, 'Cơ bi Pool Premium', 'POOL001', '/uploads/products/pool-premium.jpg', NOW(), NOW()),
(@order4_id, 4, 1, 2000000.00, 2000000.00, 'Cơ bi 3-Cushion Elite', '3CUSH001', '/uploads/products/3cushion-elite.jpg', NOW(), NOW()),

-- Order 5 items (cancelled)
(@order5_id, 5, 1, 1200000.00, 1200000.00, 'Cơ bi Tournament', 'TOUR001', '/uploads/products/tournament.jpg', NOW(), NOW());

-- Update orders with tracking and delivery information for shipped/delivered orders
UPDATE orders SET 
    tracking_number = 'TRK001234567',
    shipped_at = DATE_SUB(NOW(), INTERVAL 1 DAY),
    admin_notes = 'Đã giao cho đơn vị vận chuyển'
WHERE order_number = 'ORD202501160003';

UPDATE orders SET 
    tracking_number = 'TRK001234568',
    shipped_at = DATE_SUB(NOW(), INTERVAL 2 DAY),
    delivered_at = DATE_SUB(NOW(), INTERVAL 1 HOUR),
    admin_notes = 'Giao hàng thành công'
WHERE order_number = 'ORD202501160004';

UPDATE orders SET 
    cancelled_at = DATE_SUB(NOW(), INTERVAL 3 DAY),
    cancel_reason = 'Khách hàng thay đổi ý định',
    admin_notes = 'Đã hoàn lại stock'
WHERE order_number = 'ORD202501160005';