package com.billiard_cue_ecommerce_system_be.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductRequest {
    
    private String name;
    private String description;
    private String sku;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer stockQuantity;
    private Long categoryId;
    
    @Deprecated // Keep for backward compatibility
    private String imageUrl;
    
    private List<ProductImageRequest> images;
    private Boolean isActive;
}