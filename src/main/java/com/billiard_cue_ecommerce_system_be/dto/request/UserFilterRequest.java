package com.billiard_cue_ecommerce_system_be.dto.request;

import lombok.Data;

@Data
public class UserFilterRequest {
    private String search;
    private String role;
    private Boolean status;
    private int page = 0;
    private int size = 10;
    private String sortBy = "createdAt";
    private String sortDir = "desc";
}