package com.billiard_cue_ecommerce_system_be.dto.response;

import lombok.Data;
import lombok.Builder;

import java.time.LocalDateTime;

@Data
@Builder
public class CategoryResponse {
    private Long id;
    private String name;
    private String description;
    private Boolean isActive;
    private Long productCount; // Số lượng sản phẩm trong danh mục
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}