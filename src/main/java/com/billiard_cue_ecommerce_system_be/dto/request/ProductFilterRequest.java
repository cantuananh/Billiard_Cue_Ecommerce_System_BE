package com.billiard_cue_ecommerce_system_be.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductFilterRequest {
    
    private String search;
    private Long categoryId;
    private Boolean isActive;
    
    @Builder.Default
    private String sortBy = "id";
    
    @Builder.Default
    private String sortDir = "desc";
}