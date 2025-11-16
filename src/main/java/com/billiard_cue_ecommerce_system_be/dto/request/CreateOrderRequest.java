package com.billiard_cue_ecommerce_system_be.dto.request;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateOrderRequest {
    
    private List<OrderItemRequest> items;
    private String shippingName;
    private String shippingPhone;
    private String shippingAddress;
    private String shippingWard;
    private String shippingDistrict;
    private String shippingProvince;
    private String paymentMethod = "COD"; // Default to Cash on Delivery
    private String customerNotes;
    private BigDecimal shippingFee = BigDecimal.ZERO;
    private BigDecimal discountAmount = BigDecimal.ZERO;
    
    @Data
    public static class OrderItemRequest {
        private Long productId;
        private Integer quantity;
    }
}