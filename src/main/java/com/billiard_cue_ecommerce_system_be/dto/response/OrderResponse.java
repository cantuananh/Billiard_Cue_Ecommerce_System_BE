package com.billiard_cue_ecommerce_system_be.dto.response;

import com.billiard_cue_ecommerce_system_be.entity.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private Long userId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private OrderStatus status;
    private String statusDisplayName;
    private BigDecimal totalAmount;
    private BigDecimal shippingFee;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    
    // Shipping information
    private String shippingName;
    private String shippingPhone;
    private String shippingAddress;
    private String shippingWard;
    private String shippingDistrict;
    private String shippingProvince;
    private String fullShippingAddress;
    
    // Payment information
    private String paymentMethod;
    private String paymentStatus;
    private LocalDateTime paidAt;
    
    // Notes
    private String customerNotes;
    private String adminNotes;
    
    // Tracking
    private String trackingNumber;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime cancelledAt;
    private String cancelReason;
    
    // Order items
    private List<OrderItemResponse> orderItems;
    private int totalItems;
    
    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Status flags
    private boolean canBeCancelled;
    private boolean canBeShipped;
    private boolean isCompleted;
}