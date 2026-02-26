package com.billiard_cue_ecommerce_system_be.controller;

import com.billiard_cue_ecommerce_system_be.dto.request.UpdateOrderStatusRequest;
import com.billiard_cue_ecommerce_system_be.dto.response.OrderResponse;
import com.billiard_cue_ecommerce_system_be.entity.OrderStatus;
import com.billiard_cue_ecommerce_system_be.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
// @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')") // Temporarily commented for testing
public class AdminOrderController {
    
    private final OrderService orderService;
    
    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search) {
        
        Page<OrderResponse> orders = orderService.getAllOrders(page, size, status, search);
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long orderId) {
        OrderResponse order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }
    
    @GetMapping("/order-number/{orderNumber}")
    public ResponseEntity<OrderResponse> getOrderByOrderNumber(@PathVariable String orderNumber) {
        OrderResponse order = orderService.getOrderByOrderNumber(orderNumber);
        return ResponseEntity.ok(order);
    }
    
    @PutMapping("/{orderId}/status")
    // @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')") // Temporarily commented for testing
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody UpdateOrderStatusRequest request) {
        
        OrderResponse updatedOrder = orderService.updateOrderStatus(orderId, request);
        return ResponseEntity.ok(updatedOrder);
    }
    
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getOrderStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // Total revenue
        BigDecimal totalRevenue = orderService.getTotalRevenue();
        statistics.put("totalRevenue", totalRevenue != null ? totalRevenue : BigDecimal.ZERO);
        
        // Total orders count
        Long totalOrders = orderService.getTotalOrdersCount();
        statistics.put("totalOrders", totalOrders);
        
        // Orders by status
        Map<String, Long> ordersByStatus = new HashMap<>();
        for (OrderStatus status : OrderStatus.values()) {
            Long count = orderService.getOrdersCountByStatus(status);
            ordersByStatus.put(status.name(), count);
        }
        statistics.put("ordersByStatus", ordersByStatus);
        
        return ResponseEntity.ok(statistics);
    }
    
    @GetMapping("/statistics/revenue")
    public ResponseEntity<Map<String, Object>> getRevenueStatistics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        Map<String, Object> revenueStats = new HashMap<>();
        
        if (startDate != null && endDate != null) {
            BigDecimal revenue = orderService.getRevenueByDateRange(startDate, endDate);
            revenueStats.put("revenue", revenue != null ? revenue : BigDecimal.ZERO);
            revenueStats.put("startDate", startDate);
            revenueStats.put("endDate", endDate);
        } else {
            BigDecimal totalRevenue = orderService.getTotalRevenue();
            revenueStats.put("totalRevenue", totalRevenue != null ? totalRevenue : BigDecimal.ZERO);
        }
        
        return ResponseEntity.ok(revenueStats);
    }
    
    @GetMapping("/recent")
    public ResponseEntity<List<OrderResponse>> getRecentOrders(
            @RequestParam(defaultValue = "10") int limit) {
        
        List<OrderResponse> recentOrders = orderService.getRecentOrders(limit);
        return ResponseEntity.ok(recentOrders);
    }
    
    @GetMapping("/dashboard-stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> dashboardStats = new HashMap<>();
        
        // Basic statistics
        Long totalOrders = orderService.getTotalOrdersCount();
        BigDecimal totalRevenue = orderService.getTotalRevenue();
        Long pendingOrders = orderService.getOrdersCountByStatus(OrderStatus.PENDING);
        Long shippingOrders = orderService.getOrdersCountByStatus(OrderStatus.SHIPPING);
        
        dashboardStats.put("totalOrders", totalOrders);
        dashboardStats.put("totalRevenue", totalRevenue != null ? totalRevenue : BigDecimal.ZERO);
        dashboardStats.put("pendingOrders", pendingOrders);
        dashboardStats.put("shippingOrders", shippingOrders);
        
        // Recent orders
        List<OrderResponse> recentOrders = orderService.getRecentOrders(5);
        dashboardStats.put("recentOrders", recentOrders);
        
        // Orders by status for chart
        Map<String, Long> ordersByStatus = new HashMap<>();
        for (OrderStatus status : OrderStatus.values()) {
            Long count = orderService.getOrdersCountByStatus(status);
            if (count > 0) {
                ordersByStatus.put(status.getDisplayName(), count);
            }
        }
        dashboardStats.put("ordersByStatus", ordersByStatus);
        
        return ResponseEntity.ok(dashboardStats);
    }
}