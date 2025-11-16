package com.billiard_cue_ecommerce_system_be.controller;

import com.billiard_cue_ecommerce_system_be.dto.request.CreateOrderRequest;
import com.billiard_cue_ecommerce_system_be.dto.response.OrderResponse;
import com.billiard_cue_ecommerce_system_be.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
public class CustomerOrderController {
    
    private final OrderService orderService;
    
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @RequestBody CreateOrderRequest request,
            Authentication authentication) {
        
        // You would typically get userId from the UserDetails or JWT token
        // For now, assuming the email is used to find user ID
        // In a real implementation, you should have a method to get userId from authentication
        Long userId = getUserIdFromAuthentication(authentication);
        
        OrderResponse order = orderService.createOrder(request, userId);
        return ResponseEntity.ok(order);
    }
    
    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getMyOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuthentication(authentication);
        Page<OrderResponse> orders = orderService.getOrdersByUser(userId, page, size);
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable Long orderId,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuthentication(authentication);
        OrderResponse order = orderService.getOrderById(orderId);
        
        // Check if the order belongs to the current user
        if (!order.getUserId().equals(userId)) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(order);
    }
    
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable Long orderId,
            @RequestParam(required = false) String reason,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuthentication(authentication);
        OrderResponse order = orderService.cancelOrder(orderId, userId, reason);
        return ResponseEntity.ok(order);
    }
    
    @GetMapping("/order-number/{orderNumber}")
    public ResponseEntity<OrderResponse> getOrderByOrderNumber(
            @PathVariable String orderNumber,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuthentication(authentication);
        OrderResponse order = orderService.getOrderByOrderNumber(orderNumber);
        
        // Check if the order belongs to the current user
        if (!order.getUserId().equals(userId)) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(order);
    }
    
    // Helper method to extract user ID from authentication
    // This should be implemented based on your JWT token structure
    private Long getUserIdFromAuthentication(Authentication authentication) {
        // This is a placeholder implementation
        // In a real scenario, you would extract the user ID from JWT token
        // or use a service to find user by email/username
        
        // You should implement a method in UserService to find user ID by email
        // For now, returning a placeholder
        // TODO: Implement proper user ID extraction from JWT or authentication
        return 1L; // This should be replaced with actual user ID
    }
}