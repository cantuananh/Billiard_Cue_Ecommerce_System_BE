package com.billiard_cue_ecommerce_system_be.config;

import com.billiard_cue_ecommerce_system_be.entity.User;
import com.billiard_cue_ecommerce_system_be.entity.Role;
import com.billiard_cue_ecommerce_system_be.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Tạo admin account nếu chưa tồn tại
        if (!userRepository.existsByEmail("admin@gmail.com")) {
            User admin = new User();
            admin.setFullName("Admin System");
            admin.setEmail("admin@gmail.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setPhoneNumber("0123456789");
            admin.setDateOfBirth(LocalDate.of(1990, 1, 1));
            admin.setRole(Role.ADMIN);
            admin.setIsEnabled(true);
            admin.setCreatedAt(LocalDateTime.now());
            admin.setUpdatedAt(LocalDateTime.now());

            userRepository.save(admin);
            System.out.println("✅ Admin account created successfully!");
            System.out.println("📧 Email: admin@gmail.com");
            System.out.println("🔑 Password: admin123");
        }

        // Tạo user thường để test nếu chưa tồn tại
        if (!userRepository.existsByEmail("user@gmail.com")) {
            User user = new User();
            user.setFullName("Test User");
            user.setEmail("user@gmail.com");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setPhoneNumber("0987654321");
            user.setDateOfBirth(LocalDate.of(1995, 5, 15));
            user.setRole(Role.CUSTOMER);
            user.setIsEnabled(true);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());

            userRepository.save(user);
            System.out.println("✅ Test user account created successfully!");
            System.out.println("📧 Email: user@gmail.com");
            System.out.println("🔑 Password: user123");
        }
    }
}