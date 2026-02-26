package com.billiard_cue_ecommerce_system_be.dto.request;

import com.billiard_cue_ecommerce_system_be.entity.Role;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateUserRequest {
    private String fullName;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private Role role;
    private Boolean isEnabled;
}