package com.billiard_cue_ecommerce_system_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    
    private Long id;
    private String name;
    private String description;
    private String sku;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer stockQuantity;
    private String imageUrl; // Keep for backward compatibility
    private Boolean isActive;
    private BigDecimal rating;
    private Integer reviewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Category information
    private Long categoryId;
    private String categoryName;
    
    // Images
    private List<ProductImageResponse> images;
    
    // Computed field for primary image URL
    public String getPrimaryImageUrl() {
        if (images != null && !images.isEmpty()) {
            return images.stream()
                    .filter(ProductImageResponse::getIsPrimary)
                    .findFirst()
                    .map(ProductImageResponse::getImageUrl)
                    .orElse(images.get(0).getImageUrl()); // Fallback to first image
        }
        return imageUrl; // Fallback to old field
    }
    
    // Alias for frontend compatibility
    public Integer getStock() {
        return stockQuantity;
    }
}