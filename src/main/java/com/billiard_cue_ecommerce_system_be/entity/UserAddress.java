package com.billiard_cue_ecommerce_system_be.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAddress {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @NotBlank(message = "Tên người nhận không được để trống")
    @Column(name = "recipient_name", nullable = false)
    private String recipientName;
    
    @NotBlank(message = "Số điện thoại không được để trống")
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;
    
    @NotBlank(message = "Địa chỉ chi tiết không được để trống")
    @Column(name = "street_address", nullable = false)
    private String streetAddress;
    
    @NotBlank(message = "Phường/Xã không được để trống")
    @Column(nullable = false)
    private String ward;
    
    @NotBlank(message = "Quận/Huyện không được để trống")
    @Column(nullable = false)
    private String district;
    
    @NotBlank(message = "Tỉnh/Thành phố không được để trống")
    @Column(nullable = false)
    private String province;
    
    @Column(name = "postal_code")
    private String postalCode;
    
    @Column(name = "is_default")
    private Boolean isDefault = false;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public String getFullAddress() {
        return String.format("%s, %s, %s, %s", streetAddress, ward, district, province);
    }
}