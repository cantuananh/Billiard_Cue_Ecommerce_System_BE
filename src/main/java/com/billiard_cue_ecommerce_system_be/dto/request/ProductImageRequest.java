package com.billiard_cue_ecommerce_system_be.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageRequest {
    
    private String imageUrl;
    
    @Builder.Default
    private Boolean isPrimary = false;
    
    @Builder.Default
    private Integer displayOrder = 0;
}