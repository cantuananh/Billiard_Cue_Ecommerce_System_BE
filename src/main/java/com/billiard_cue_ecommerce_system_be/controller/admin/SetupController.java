package com.billiard_cue_ecommerce_system_be.controller.admin;

import com.billiard_cue_ecommerce_system_be.entity.Category;
import com.billiard_cue_ecommerce_system_be.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/admin/setup")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SetupController {
    
    private final CategoryRepository categoryRepository;

    @PostMapping("/categories")
    public ResponseEntity<String> createSampleCategories() {
        // Check if categories already exist
        if (categoryRepository.count() > 0) {
            return ResponseEntity.ok("Categories already exist");
        }

        List<Category> categories = Arrays.asList(
            createCategory("Cơ bi-a Pool", "Cơ bi-a dành cho bàn Pool 8 bi, 9 bi"),
            createCategory("Cơ bi-a Carom", "Cơ bi-a dành cho bàn Carom 3 băng"),
            createCategory("Cơ bi-a Snooker", "Cơ bi-a dành cho bàn Snooker"),
            createCategory("Phụ kiện", "Các phụ kiện bi-a như phấn, găng tay, hộp đựng cơ"),
            createCategory("Bàn bi-a", "Các loại bàn bi-a chuyên nghiệp"),
            createCategory("Cơ Cao Cấp", "Cơ bi-a cao cấp nhập khẩu"),
            createCategory("Cơ Giá Rẻ", "Cơ bi-a giá rẻ cho người mới chơi"),
            createCategory("Cơ Handmade", "Cơ bi-a thủ công đặc biệt"),
            createCategory("Tip Cơ", "Đầu cơ bi-a các loại"),
            createCategory("Chalk Phấn", "Phấn bi-a chính hãng"),
            createCategory("Case Hộp", "Hộp đựng cơ bi-a"),
            createCategory("Glove Găng", "Găng tay chơi bi-a")
        );

        categoryRepository.saveAll(categories);
        
        return ResponseEntity.ok("Created " + categories.size() + " sample categories");
    }
    
    private Category createCategory(String name, String description) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        category.setIsActive(true);
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        return category;
    }
}