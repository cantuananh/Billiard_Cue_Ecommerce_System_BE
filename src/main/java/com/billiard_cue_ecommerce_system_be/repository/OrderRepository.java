package com.billiard_cue_ecommerce_system_be.repository;

import com.billiard_cue_ecommerce_system_be.entity.Order;
import com.billiard_cue_ecommerce_system_be.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    // Find orders by user
    Page<Order> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    // Find orders by status
    Page<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status, Pageable pageable);
    
    // Find orders by multiple statuses
    Page<Order> findByStatusInOrderByCreatedAtDesc(List<OrderStatus> statuses, Pageable pageable);
    
    // Find order by order number
    Optional<Order> findByOrderNumber(String orderNumber);
    
    // Search orders by customer name or order number
    @Query("SELECT o FROM Order o JOIN o.user u WHERE " +
           "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(o.orderNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(o.shippingName) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "ORDER BY o.createdAt DESC")
    Page<Order> searchOrders(@Param("search") String search, Pageable pageable);
    
    // Find orders within date range
    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate ORDER BY o.createdAt DESC")
    Page<Order> findOrdersByDateRange(@Param("startDate") LocalDateTime startDate, 
                                    @Param("endDate") LocalDateTime endDate, 
                                    Pageable pageable);
    
    // Revenue statistics
    @Query("SELECT SUM(o.finalAmount) FROM Order o WHERE o.status IN :completedStatuses")
    BigDecimal getTotalRevenue(@Param("completedStatuses") List<OrderStatus> completedStatuses);
    
    @Query("SELECT SUM(o.finalAmount) FROM Order o WHERE o.status IN :completedStatuses AND " +
           "o.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal getRevenueByDateRange(@Param("completedStatuses") List<OrderStatus> completedStatuses,
                                   @Param("startDate") LocalDateTime startDate,
                                   @Param("endDate") LocalDateTime endDate);
    
    // Order count statistics
    @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    Long countOrdersByDateRange(@Param("startDate") LocalDateTime startDate, 
                              @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    Long countOrdersByStatus(@Param("status") OrderStatus status);
    
    // Monthly revenue statistics
    @Query("SELECT YEAR(o.createdAt) as year, MONTH(o.createdAt) as month, SUM(o.finalAmount) as revenue " +
           "FROM Order o WHERE o.status IN :completedStatuses AND " +
           "o.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY YEAR(o.createdAt), MONTH(o.createdAt) " +
           "ORDER BY year, month")
    List<Object[]> getMonthlyRevenue(@Param("completedStatuses") List<OrderStatus> completedStatuses,
                                   @Param("startDate") LocalDateTime startDate,
                                   @Param("endDate") LocalDateTime endDate);
    
    // Daily revenue statistics
    @Query("SELECT DATE(o.createdAt) as date, SUM(o.finalAmount) as revenue " +
           "FROM Order o WHERE o.status IN :completedStatuses AND " +
           "o.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY DATE(o.createdAt) " +
           "ORDER BY date")
    List<Object[]> getDailyRevenue(@Param("completedStatuses") List<OrderStatus> completedStatuses,
                                 @Param("startDate") LocalDateTime startDate,
                                 @Param("endDate") LocalDateTime endDate);
    
    // Recent orders for dashboard
    @Query("SELECT o FROM Order o ORDER BY o.createdAt DESC")
    List<Order> findRecentOrders(Pageable pageable);
    
    // Orders by status for dashboard
    @Query("SELECT o.status, COUNT(o) FROM Order o GROUP BY o.status")
    List<Object[]> getOrderStatusStatistics();
    
    // Top customers by order count
    @Query("SELECT u.fullName, COUNT(o) as orderCount, SUM(o.finalAmount) as totalSpent " +
           "FROM Order o JOIN o.user u WHERE o.status IN :completedStatuses " +
           "GROUP BY u.id, u.fullName " +
           "ORDER BY orderCount DESC")
    List<Object[]> getTopCustomers(@Param("completedStatuses") List<OrderStatus> completedStatuses, 
                                 Pageable pageable);
}