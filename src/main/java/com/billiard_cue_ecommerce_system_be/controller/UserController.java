package com.billiard_cue_ecommerce_system_be.controller;

import com.billiard_cue_ecommerce_system_be.dto.request.AddAddressRequest;
import com.billiard_cue_ecommerce_system_be.dto.request.ChangePasswordRequest;
import com.billiard_cue_ecommerce_system_be.dto.request.UpdateProfileRequest;
import com.billiard_cue_ecommerce_system_be.dto.response.UserResponse;
import com.billiard_cue_ecommerce_system_be.entity.User;
import com.billiard_cue_ecommerce_system_be.entity.UserAddress;
import com.billiard_cue_ecommerce_system_be.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            UserResponse userResponse = userService.getUserProfile(user.getId());
            return ResponseEntity.ok(userResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }
    
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody UpdateProfileRequest request,
                                         Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            UserResponse userResponse = userService.updateProfile(user.getId(), request);
            return ResponseEntity.ok(userResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }
    
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request,
                                          Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            String message = userService.changePassword(user.getId(), request);
            return ResponseEntity.ok(Map.of("message", message));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }
    
    @PostMapping("/upload-avatar")
    public ResponseEntity<?> uploadAvatar(@RequestParam("file") MultipartFile file,
                                        Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            String avatarUrl = userService.uploadAvatar(user.getId(), file);
            return ResponseEntity.ok(Map.of("avatarUrl", avatarUrl));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }
    
    // Address Management
    @GetMapping("/addresses")
    public ResponseEntity<?> getAddresses(Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            List<UserAddress> addresses = userService.getUserAddresses(user.getId());
            return ResponseEntity.ok(addresses);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }
    
    @PostMapping("/addresses")
    public ResponseEntity<?> addAddress(@Valid @RequestBody AddAddressRequest request,
                                      Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            UserAddress address = userService.addAddress(user.getId(), request);
            return ResponseEntity.ok(address);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }
    
    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<?> updateAddress(@PathVariable Long addressId,
                                         @Valid @RequestBody AddAddressRequest request,
                                         Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            UserAddress address = userService.updateAddress(user.getId(), addressId, request);
            return ResponseEntity.ok(address);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }
    
    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<?> deleteAddress(@PathVariable Long addressId,
                                         Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            userService.deleteAddress(user.getId(), addressId);
            return ResponseEntity.ok(Map.of("message", "Đã xóa địa chỉ thành công"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }
    
    @PostMapping("/addresses/{addressId}/set-default")
    public ResponseEntity<?> setDefaultAddress(@PathVariable Long addressId,
                                             Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            userService.setDefaultAddress(user.getId(), addressId);
            return ResponseEntity.ok(Map.of("message", "Đã đặt làm địa chỉ mặc định"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }
}