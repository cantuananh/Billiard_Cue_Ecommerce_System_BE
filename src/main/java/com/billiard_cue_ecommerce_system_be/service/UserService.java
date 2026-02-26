package com.billiard_cue_ecommerce_system_be.service;

import com.billiard_cue_ecommerce_system_be.dto.request.AddAddressRequest;
import com.billiard_cue_ecommerce_system_be.dto.request.ChangePasswordRequest;
import com.billiard_cue_ecommerce_system_be.dto.request.UpdateProfileRequest;
import com.billiard_cue_ecommerce_system_be.dto.response.UserResponse;
import com.billiard_cue_ecommerce_system_be.entity.User;
import com.billiard_cue_ecommerce_system_be.entity.UserAddress;
import com.billiard_cue_ecommerce_system_be.repository.UserAddressRepository;
import com.billiard_cue_ecommerce_system_be.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final UserAddressRepository userAddressRepository;
    private final PasswordEncoder passwordEncoder;
    
    private final String UPLOAD_DIR = "uploads/avatars/";
    
    public UserResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToUserResponse(user);
    }
    
    public UserResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setDateOfBirth(request.getDateOfBirth());
        
        userRepository.save(user);
        return mapToUserResponse(user);
    }
    
    public String changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu hiện tại không đúng");
        }
        
        // Verify new password confirmation
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new RuntimeException("Xác nhận mật khẩu mới không khớp");
        }
        
        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        
        return "Đổi mật khẩu thành công";
    }
    
    public String uploadAvatar(Long userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (file.isEmpty()) {
            throw new RuntimeException("File không được để trống");
        }
        
        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("Chỉ chấp nhận file ảnh");
        }
        
        // Validate file size (5MB max)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new RuntimeException("File size không được vượt quá 5MB");
        }
        
        try {
            // Create upload directory if not exists
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = UUID.randomUUID().toString() + fileExtension;
            
            // Save file
            Path filePath = uploadPath.resolve(newFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            // Update user avatar URL
            String avatarUrl = "/uploads/avatars/" + newFilename;
            user.setAvatarUrl(avatarUrl);
            userRepository.save(user);
            
            return avatarUrl;
        } catch (IOException e) {
            throw new RuntimeException("Không thể upload file: " + e.getMessage());
        }
    }
    
    // Address Management
    public List<UserAddress> getUserAddresses(Long userId) {
        return userAddressRepository.findByUserIdOrderByIsDefaultDescCreatedAtDesc(userId);
    }
    
    public UserAddress addAddress(Long userId, AddAddressRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // If this is the first address or explicitly set as default, make it default
        List<UserAddress> existingAddresses = userAddressRepository.findByUserIdOrderByIsDefaultDescCreatedAtDesc(userId);
        boolean shouldBeDefault = existingAddresses.isEmpty() || request.getIsDefault();
        
        // If setting as default, reset other default addresses
        if (shouldBeDefault) {
            existingAddresses.forEach(addr -> {
                if (addr.getIsDefault()) {
                    addr.setIsDefault(false);
                    userAddressRepository.save(addr);
                }
            });
        }
        
        UserAddress address = new UserAddress();
        address.setUser(user);
        address.setRecipientName(request.getRecipientName());
        address.setPhoneNumber(request.getPhoneNumber());
        address.setStreetAddress(request.getStreetAddress());
        address.setWard(request.getWard());
        address.setDistrict(request.getDistrict());
        address.setProvince(request.getProvince());
        address.setPostalCode(request.getPostalCode());
        address.setIsDefault(shouldBeDefault);
        
        return userAddressRepository.save(address);
    }
    
    public UserAddress updateAddress(Long userId, Long addressId, AddAddressRequest request) {
        UserAddress address = userAddressRepository.findByUserIdAndId(userId, addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));
        
        address.setRecipientName(request.getRecipientName());
        address.setPhoneNumber(request.getPhoneNumber());
        address.setStreetAddress(request.getStreetAddress());
        address.setWard(request.getWard());
        address.setDistrict(request.getDistrict());
        address.setProvince(request.getProvince());
        address.setPostalCode(request.getPostalCode());
        
        // Handle default setting
        if (request.getIsDefault() && !address.getIsDefault()) {
            setDefaultAddress(userId, addressId);
        }
        
        return userAddressRepository.save(address);
    }
    
    public void deleteAddress(Long userId, Long addressId) {
        UserAddress address = userAddressRepository.findByUserIdAndId(userId, addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));
        
        // If deleting default address, set another one as default
        if (address.getIsDefault()) {
            List<UserAddress> otherAddresses = userAddressRepository.findByUserIdOrderByIsDefaultDescCreatedAtDesc(userId);
            UserAddress nextDefault = otherAddresses.stream()
                    .filter(addr -> !addr.getId().equals(addressId))
                    .findFirst()
                    .orElse(null);
            
            if (nextDefault != null) {
                nextDefault.setIsDefault(true);
                userAddressRepository.save(nextDefault);
            }
        }
        
        userAddressRepository.delete(address);
    }
    
    public void setDefaultAddress(Long userId, Long addressId) {
        // Reset all addresses to non-default
        List<UserAddress> addresses = userAddressRepository.findByUserIdOrderByIsDefaultDescCreatedAtDesc(userId);
        addresses.forEach(addr -> {
            addr.setIsDefault(addr.getId().equals(addressId));
            userAddressRepository.save(addr);
        });
    }
    
    private UserResponse mapToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setDateOfBirth(user.getDateOfBirth());
        response.setRole(user.getRole());
        response.setAvatarUrl(user.getAvatarUrl());
        response.setIsEnabled(user.getIsEnabled());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }
}