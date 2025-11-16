package com.billiard_cue_ecommerce_system_be.service;

import com.billiard_cue_ecommerce_system_be.dto.request.CreateOrderRequest;
import com.billiard_cue_ecommerce_system_be.dto.request.UpdateOrderStatusRequest;
import com.billiard_cue_ecommerce_system_be.dto.response.OrderResponse;
import com.billiard_cue_ecommerce_system_be.dto.response.OrderItemResponse;
import com.billiard_cue_ecommerce_system_be.entity.*;
import com.billiard_cue_ecommerce_system_be.repository.OrderRepository;
import com.billiard_cue_ecommerce_system_be.repository.ProductRepository;
import com.billiard_cue_ecommerce_system_be.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    
    // Create new order (for customers)
    public OrderResponse createOrder(CreateOrderRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        
        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setUser(user);
        order.setShippingName(request.getShippingName());
        order.setShippingPhone(request.getShippingPhone());
        order.setShippingAddress(request.getShippingAddress());
        order.setShippingWard(request.getShippingWard());
        order.setShippingDistrict(request.getShippingDistrict());
        order.setShippingProvince(request.getShippingProvince());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setCustomerNotes(request.getCustomerNotes());
        order.setShippingFee(request.getShippingFee());
        order.setDiscountAmount(request.getDiscountAmount());
        
        // Create order items
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CreateOrderRequest.OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm ID: " + itemRequest.getProductId()));
            
            // Check stock availability
            if (product.getStockQuantity() < itemRequest.getQuantity()) {
                throw new RuntimeException("Sản phẩm " + product.getName() + " không đủ hàng trong kho");
            }
            
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.updateProductSnapshot(); // Copy product info to order item
            orderItem.calculateTotalPrice();
            
            order.getOrderItems().add(orderItem);
            totalAmount = totalAmount.add(orderItem.getTotalPrice());
            
            // Reduce stock quantity
            product.setStockQuantity(product.getStockQuantity() - itemRequest.getQuantity());
            productRepository.save(product);
        }
        
        order.setTotalAmount(totalAmount);
        order.setFinalAmount(totalAmount.add(order.getShippingFee()).subtract(order.getDiscountAmount()));
        
        Order savedOrder = orderRepository.save(order);
        return mapToOrderResponse(savedOrder);
    }
    
    // Get all orders with pagination and filtering (for admin)
    public Page<OrderResponse> getAllOrders(int page, int size, String status, String search) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders;
        
        if (search != null && !search.trim().isEmpty()) {
            orders = orderRepository.searchOrders(search.trim(), pageable);
        } else if (status != null && !status.isEmpty()) {
            try {
                OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
                orders = orderRepository.findByStatusOrderByCreatedAtDesc(orderStatus, pageable);
            } catch (IllegalArgumentException e) {
                orders = orderRepository.findAll(pageable);
            }
        } else {
            orders = orderRepository.findAll(pageable);
        }
        
        return orders.map(this::mapToOrderResponse);
    }
    
    // Get orders by user
    public Page<OrderResponse> getOrdersByUser(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return orders.map(this::mapToOrderResponse);
    }
    
    // Get order by ID
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        return mapToOrderResponse(order);
    }
    
    // Get order by order number
    public OrderResponse getOrderByOrderNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        return mapToOrderResponse(order);
    }
    
    // Update order status (for admin)
    public OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        
        OrderStatus newStatus;
        try {
            newStatus = OrderStatus.valueOf(request.getStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Trạng thái đơn hàng không hợp lệ: " + request.getStatus());
        }
        
        order.setStatus(newStatus);
        order.setAdminNotes(request.getAdminNotes());
        
        // Update timestamps based on status
        LocalDateTime now = LocalDateTime.now();
        switch (newStatus) {
            case PENDING:
            case CONFIRMED:
            case PROCESSING:
                // No special action needed
                break;
            case SHIPPING:
                if (request.getTrackingNumber() != null) {
                    order.setTrackingNumber(request.getTrackingNumber());
                }
                order.setShippedAt(now);
                break;
            case DELIVERED:
                order.setDeliveredAt(now);
                break;
            case COMPLETED:
                if (order.getDeliveredAt() == null) {
                    order.setDeliveredAt(now);
                }
                break;
            case CANCELLED:
                order.setCancelledAt(now);
                order.setCancelReason(request.getCancelReason());
                // Restore stock quantities when order is cancelled
                restoreStockQuantities(order);
                break;
            case RETURNED:
                order.setCancelledAt(now);
                order.setCancelReason(request.getCancelReason());
                // Restore stock quantities when order is returned
                restoreStockQuantities(order);
                break;
        }
        
        Order savedOrder = orderRepository.save(order);
        return mapToOrderResponse(savedOrder);
    }
    
    // Cancel order (for customers - only if status allows)
    public OrderResponse cancelOrder(Long orderId, Long userId, String reason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        
        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền hủy đơn hàng này");
        }
        
        if (!order.canBeCancelled()) {
            throw new RuntimeException("Không thể hủy đơn hàng ở trạng thái: " + order.getStatus().getDisplayName());
        }
        
        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelledAt(LocalDateTime.now());
        order.setCancelReason(reason);
        
        // Restore stock quantities
        restoreStockQuantities(order);
        
        Order savedOrder = orderRepository.save(order);
        return mapToOrderResponse(savedOrder);
    }
    
    // Get revenue statistics
    public BigDecimal getTotalRevenue() {
        List<OrderStatus> completedStatuses = List.of(OrderStatus.COMPLETED, OrderStatus.DELIVERED);
        return orderRepository.getTotalRevenue(completedStatuses);
    }
    
    public BigDecimal getRevenueByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<OrderStatus> completedStatuses = List.of(OrderStatus.COMPLETED, OrderStatus.DELIVERED);
        return orderRepository.getRevenueByDateRange(completedStatuses, startDate, endDate);
    }
    
    // Get order statistics
    public Long getTotalOrdersCount() {
        return orderRepository.count();
    }
    
    public Long getOrdersCountByStatus(OrderStatus status) {
        return orderRepository.countOrdersByStatus(status);
    }
    
    // Get recent orders for dashboard
    public List<OrderResponse> getRecentOrders(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Order> orders = orderRepository.findRecentOrders(pageable);
        return orders.stream().map(this::mapToOrderResponse).collect(Collectors.toList());
    }
    
    // Helper methods
    private String generateOrderNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "ORD" + timestamp + String.format("%03d", (int)(Math.random() * 1000));
    }
    
    private void restoreStockQuantities(Order order) {
        for (OrderItem orderItem : order.getOrderItems()) {
            Product product = orderItem.getProduct();
            if (product != null) {
                product.setStockQuantity(product.getStockQuantity() + orderItem.getQuantity());
                productRepository.save(product);
            }
        }
    }
    
    private OrderResponse mapToOrderResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderNumber(order.getOrderNumber());
        response.setUserId(order.getUser().getId());
        response.setCustomerName(order.getUser().getFullName());
        response.setCustomerEmail(order.getUser().getEmail());
        response.setCustomerPhone(order.getUser().getPhoneNumber());
        response.setStatus(order.getStatus());
        response.setStatusDisplayName(order.getStatus().getDisplayName());
        response.setTotalAmount(order.getTotalAmount());
        response.setShippingFee(order.getShippingFee());
        response.setDiscountAmount(order.getDiscountAmount());
        response.setFinalAmount(order.getFinalAmount());
        
        // Shipping info
        response.setShippingName(order.getShippingName());
        response.setShippingPhone(order.getShippingPhone());
        response.setShippingAddress(order.getShippingAddress());
        response.setShippingWard(order.getShippingWard());
        response.setShippingDistrict(order.getShippingDistrict());
        response.setShippingProvince(order.getShippingProvince());
        response.setFullShippingAddress(order.getFullShippingAddress());
        
        // Payment info
        response.setPaymentMethod(order.getPaymentMethod());
        response.setPaymentStatus(order.getPaymentStatus());
        response.setPaidAt(order.getPaidAt());
        
        // Notes and tracking
        response.setCustomerNotes(order.getCustomerNotes());
        response.setAdminNotes(order.getAdminNotes());
        response.setTrackingNumber(order.getTrackingNumber());
        response.setShippedAt(order.getShippedAt());
        response.setDeliveredAt(order.getDeliveredAt());
        response.setCancelledAt(order.getCancelledAt());
        response.setCancelReason(order.getCancelReason());
        
        // Order items
        List<OrderItemResponse> itemResponses = order.getOrderItems().stream()
                .map(this::mapToOrderItemResponse)
                .collect(Collectors.toList());
        response.setOrderItems(itemResponses);
        response.setTotalItems(order.getTotalItems());
        
        // Timestamps
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());
        
        // Status flags
        response.setCanBeCancelled(order.canBeCancelled());
        response.setCanBeShipped(order.canBeShipped());
        response.setCompleted(order.isCompleted());
        
        return response;
    }
    
    private OrderItemResponse mapToOrderItemResponse(OrderItem orderItem) {
        OrderItemResponse response = new OrderItemResponse();
        response.setId(orderItem.getId());
        response.setProductId(orderItem.getProduct().getId());
        response.setProductName(orderItem.getProductName());
        response.setProductSku(orderItem.getProductSku());
        response.setProductImageUrl(orderItem.getProductImageUrl());
        response.setQuantity(orderItem.getQuantity());
        response.setUnitPrice(orderItem.getUnitPrice());
        response.setTotalPrice(orderItem.getTotalPrice());
        response.setCreatedAt(orderItem.getCreatedAt());
        response.setUpdatedAt(orderItem.getUpdatedAt());
        return response;
    }
}