package com.billiard_cue_ecommerce_system_be.controller.admin;

import com.billiard_cue_ecommerce_system_be.entity.Category;
import com.billiard_cue_ecommerce_system_be.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/init")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InitDataController {
    
    private final CategoryRepository categoryRepository;

    @PostMapping("/categories")
    public ResponseEntity<String> initCategories() {
        try {
            // Check if categories already exist
            if (categoryRepository.count() > 0) {
                return ResponseEntity.ok("Categories already exist");
            }
            
            // Create sample categories
            Category[] categories = {
                createCategory("Cơ bi-a Pool", "Cơ bi-a dành cho bàn Pool 8 bi, 9 bi"),
                createCategory("Cơ bi-a Carom", "Cơ bi-a dành cho bàn Carom 3 băng"),
                createCategory("Cơ bi-a Snooker", "Cơ bi-a dành cho bàn Snooker"),
                createCategory("Phụ kiện", "Các phụ kiện bi-a như phấn, găng tay, hộp đựng cơ"),
                createCategory("Bàn bi-a", "Các loại bàn bi-a chuyên nghiệp"),
                createCategory("Đầu cơ (Tip)", "Đầu cơ bi-a các loại"),
                createCategory("Phấn", "Phấn đánh bi-a các loại"),
                createCategory("Găng tay", "Găng tay chơi bi-a"),
                createCategory("Hộp đựng cơ", "Hộp và túi đựng cơ bi-a"),
                createCategory("Đồ trang trí", "Đồ trang trí phòng bi-a")
            };
            
            categoryRepository.saveAll(List.of(categories));
            
            return ResponseEntity.ok("Categories initialized successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
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