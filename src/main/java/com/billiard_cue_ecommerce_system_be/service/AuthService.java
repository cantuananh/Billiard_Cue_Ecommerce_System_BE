package com.billiard_cue_ecommerce_system_be.service;

import com.billiard_cue_ecommerce_system_be.dto.request.LoginRequest;
import com.billiard_cue_ecommerce_system_be.dto.request.RegisterRequest;
import com.billiard_cue_ecommerce_system_be.dto.response.JwtResponse;
import com.billiard_cue_ecommerce_system_be.dto.response.UserResponse;
import com.billiard_cue_ecommerce_system_be.entity.User;
import com.billiard_cue_ecommerce_system_be.repository.UserRepository;
import com.billiard_cue_ecommerce_system_be.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    // private final EmailService emailService; // Temporarily disabled for development
    
    public JwtResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        
        User user = (User) authentication.getPrincipal();
        
        if (!user.getIsEnabled()) {
            throw new RuntimeException("Tài khoản chưa được kích hoạt. Vui lòng kiểm tra email để kích hoạt tài khoản.");
        }
        
        String accessToken = jwtUtils.generateToken(user);
        String refreshToken = jwtUtils.generateRefreshToken(user);
        
        UserResponse userResponse = mapToUserResponse(user);
        
        return new JwtResponse(accessToken, refreshToken, userResponse);
    }
    
    public String register(RegisterRequest request) {
        // Validate password confirmation
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Mật khẩu xác nhận không khớp");
        }
        
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã được sử dụng");
        }
        
        // Create new user
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setDateOfBirth(request.getDateOfBirth());
        // Temporarily disable email verification for development
        // user.setEmailVerificationToken(UUID.randomUUID().toString());
        user.setIsEnabled(true); // Enable user immediately for development
        
        userRepository.save(user);
        
        // Send verification email (disabled for development)
        // emailService.sendVerificationEmail(user.getEmail(), user.getEmailVerificationToken());
        
        return "Đăng ký thành công!";
    }
    
    public String verifyEmail(String token) {
        User user = userRepository.findByEmailVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Token xác thực không hợp lệ"));
        
        user.setIsEnabled(true);
        user.setEmailVerificationToken(null);
        userRepository.save(user);
        
        return "Kích hoạt tài khoản thành công!";
    }
    
    public String forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email không tồn tại trong hệ thống"));
        
        String resetToken = UUID.randomUUID().toString();
        user.setPasswordResetToken(resetToken);
        user.setPasswordResetTokenExpiry(java.time.LocalDateTime.now().plusHours(1)); // 1 hour expiry
        
        userRepository.save(user);
        
        // emailService.sendPasswordResetEmail(email, resetToken); // Disabled for development
        
        return "Link đặt lại mật khẩu đã được gửi đến email của bạn. (Development: Token = " + resetToken + ")";
    }
    
    public String resetPassword(String token, String newPassword) {
        User user = userRepository.findByPasswordResetToken(token)
                .orElseThrow(() -> new RuntimeException("Token đặt lại mật khẩu không hợp lệ"));
        
        if (user.getPasswordResetTokenExpiry().isBefore(java.time.LocalDateTime.now())) {
            throw new RuntimeException("Token đặt lại mật khẩu đã hết hạn");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiry(null);
        
        userRepository.save(user);
        
        return "Đặt lại mật khẩu thành công!";
    }
    
    public JwtResponse refreshToken(String refreshToken) {
        String email = jwtUtils.extractUsername(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!jwtUtils.isRefreshToken(refreshToken) || !jwtUtils.validateToken(refreshToken, user)) {
            throw new RuntimeException("Invalid refresh token");
        }
        
        String newAccessToken = jwtUtils.generateToken(user);
        String newRefreshToken = jwtUtils.generateRefreshToken(user);
        
        UserResponse userResponse = mapToUserResponse(user);
        
        return new JwtResponse(newAccessToken, newRefreshToken, userResponse);
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