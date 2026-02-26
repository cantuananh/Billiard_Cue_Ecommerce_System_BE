package com.billiard_cue_ecommerce_system_be.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddAddressRequest {
    
    @NotBlank(message = "Tên người nhận không được để trống")
    private String recipientName;
    
    @NotBlank(message = "Số điện thoại không được để trống")
    private String phoneNumber;
    
    @NotBlank(message = "Địa chỉ chi tiết không được để trống")
    private String streetAddress;
    
    @NotBlank(message = "Phường/Xã không được để trống")
    private String ward;
    
    @NotBlank(message = "Quận/Huyện không được để trống")
    private String district;
    
    @NotBlank(message = "Tỉnh/Thành phố không được để trống")
    private String province;
    
    private String postalCode;
    
    private Boolean isDefault = false;
}