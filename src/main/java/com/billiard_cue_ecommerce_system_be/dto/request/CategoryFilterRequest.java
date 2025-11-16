package com.billiard_cue_ecommerce_system_be.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryFilterRequest {
    private String search;
    private Boolean isActive;
    private String sortBy;
    private String sortDir;
}