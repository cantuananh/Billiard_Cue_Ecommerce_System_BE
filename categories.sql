-- Create categories table if not exists
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Insert sample categories
INSERT IGNORE INTO categories (id, name, description, is_active, created_at, updated_at) VALUES
(1, 'Cơ bi-a Pool', 'Cơ bi-a dành cho bàn Pool 8 bi, 9 bi', true, NOW(), NOW()),
(2, 'Cơ bi-a Carom', 'Cơ bi-a dành cho bàn Carom 3 băng', true, NOW(), NOW()),
(3, 'Cơ bi-a Snooker', 'Cơ bi-a dành cho bàn Snooker', true, NOW(), NOW()),
(4, 'Phụ kiện bi-a', 'Các phụ kiện bi-a như phấn, găng tay, hộp đựng cơ', true, NOW(), NOW()),
(5, 'Bàn bi-a', 'Các loại bàn bi-a chuyên nghiệp', true, NOW(), NOW()),
(6, 'Cơ bi-a cao cấp', 'Các loại cơ bi-a cao cấp, handmade', true, NOW(), NOW()),
(7, 'Cơ bi-a giá rẻ', 'Cơ bi-a phù hợp với người mới chơi', true, NOW(), NOW()),
(8, 'Tip cơ bi-a', 'Các loại tip thay thế cho cơ bi-a', true, NOW(), NOW()),
(9, 'Đồ bảo dưỡng', 'Dụng cụ bảo dưỡng cơ và bàn bi-a', true, NOW(), NOW()),
(10, 'Phụ kiện khác', 'Các phụ kiện khác cho bi-a', true, NOW(), NOW());