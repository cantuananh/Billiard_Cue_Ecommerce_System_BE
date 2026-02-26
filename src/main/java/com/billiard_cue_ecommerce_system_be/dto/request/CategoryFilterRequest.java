package com.billiard_cue_ecommerce_system_be.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryFilterRequest {
    private String search;
    private Boolean isActive;
    private String sortBy;
    private String sortDir;
}