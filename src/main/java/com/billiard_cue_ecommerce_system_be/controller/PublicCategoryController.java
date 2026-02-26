package com.billiard_cue_ecommerce_system_be.controller;

import com.billiard_cue_ecommerce_system_be.dto.response.CategoryResponse;
import com.billiard_cue_ecommerce_system_be.dto.response.PageResponse;
import com.billiard_cue_ecommerce_system_be.dto.request.CategoryFilterRequest;
import com.billiard_cue_ecommerce_system_be.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/categories")
@CrossOrigin(origins = "http://localhost:5173")
public class PublicCategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<PageResponse<CategoryResponse>> getAllCategories() {
        CategoryFilterRequest filterRequest = CategoryFilterRequest.builder()
                .sortBy("name")
                .sortDir("asc")
                .isActive(true)
                .build();
        
        PageResponse<CategoryResponse> categories = categoryService.getAllCategories(filterRequest, 0, 100);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/featured")
    public ResponseEntity<List<CategoryResponse>> getFeaturedCategories(
            @RequestParam(defaultValue = "6") int limit) {
        
        CategoryFilterRequest filter = CategoryFilterRequest.builder().build();
        PageResponse<CategoryResponse> response = categoryService.getAllCategories(filter, 0, limit);
        
        return ResponseEntity.ok(response.getContent());
    }
}