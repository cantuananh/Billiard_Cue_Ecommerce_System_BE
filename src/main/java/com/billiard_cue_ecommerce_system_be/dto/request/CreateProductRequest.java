package com.billiard_cue_ecommerce_system_be.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequest {
    
    @NotBlank(message = "Tên sản phẩm là bắt buộc")
    private String name;
    
    private String description;
    
    @NotBlank(message = "SKU là bắt buộc")
    private String sku;
    
    @NotNull(message = "Giá sản phẩm là bắt buộc")
    @Positive(message = "Giá sản phẩm phải lớn hơn 0")
    private BigDecimal price;
    
    private BigDecimal originalPrice;
    
    @NotNull(message = "Số lượng tồn kho là bắt buộc")
    @Positive(message = "Số lượng tồn kho phải lớn hơn 0")
    private Integer stockQuantity;
    
    @NotNull(message = "Danh mục là bắt buộc")
    private Long categoryId;
    
    @Deprecated // Keep for backward compatibility
    private String imageUrl;
    
    private List<ProductImageRequest> images;
    
    @Builder.Default
    private Boolean isActive = true;
}