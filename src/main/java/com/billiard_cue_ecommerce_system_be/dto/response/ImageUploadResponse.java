package com.billiard_cue_ecommerce_system_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageUploadResponse {
    private String imageUrl;
    private String fileName;
}