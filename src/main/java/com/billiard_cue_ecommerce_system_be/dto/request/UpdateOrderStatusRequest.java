package com.billiard_cue_ecommerce_system_be.dto.request;

import lombok.Data;

@Data
public class UpdateOrderStatusRequest {
    private String status;
    private String adminNotes;
    private String trackingNumber;
    private String cancelReason;
}