package com.billiard_cue_ecommerce_system_be.entity;

public enum Role {
    ADMIN("Quản trị viên"),
    CUSTOMER("Khách hàng"),
    STAFF("Nhân viên");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}