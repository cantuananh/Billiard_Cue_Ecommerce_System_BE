package com.billiard_cue_ecommerce_system_be.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    
    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    public void sendVerificationEmail(String toEmail, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Xác thực tài khoản - Website Bán Gậy Bi A");
            message.setText(buildVerificationEmailContent(token));
            
            mailSender.send(message);
            log.info("Verification email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send verification email to: {}", toEmail, e);
            throw new RuntimeException("Không thể gửi email xác thực");
        }
    }
    
    public void sendPasswordResetEmail(String toEmail, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Đặt lại mật khẩu - Website Bán Gậy Bi A");
            message.setText(buildPasswordResetEmailContent(token));
            
            mailSender.send(message);
            log.info("Password reset email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}", toEmail, e);
            throw new RuntimeException("Không thể gửi email đặt lại mật khẩu");
        }
    }
    
    private String buildVerificationEmailContent(String token) {
        return String.format(
                "Chào bạn,\n\n" +
                "Cảm ơn bạn đã đăng ký tài khoản tại Website Bán Gậy Bi A.\n\n" +
                "Để hoàn tất quá trình đăng ký, vui lòng click vào link sau để xác thực tài khoản:\n" +
                "http://localhost:5173/verify-email?token=%s\n\n" +
                "Link này sẽ hết hạn sau 24 giờ.\n\n" +
                "Nếu bạn không thực hiện đăng ký này, vui lòng bỏ qua email này.\n\n" +
                "Trân trọng,\n" +
                "Đội ngũ Website Bán Gậy Bi A",
                token
        );
    }
    
    private String buildPasswordResetEmailContent(String token) {
        return String.format(
                "Chào bạn,\n\n" +
                "Chúng tôi nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn.\n\n" +
                "Để đặt lại mật khẩu, vui lòng click vào link sau:\n" +
                "http://localhost:5173/reset-password?token=%s\n\n" +
                "Link này sẽ hết hạn sau 1 giờ.\n\n" +
                "Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.\n\n" +
                "Trân trọng,\n" +
                "Đội ngũ Website Bán Gậy Bi A",
                token
        );
    }
}