package com.hotelmanager.model;

/**
 * Enum representing payment status
 */
public enum PaymentStatus {
    PENDING("PENDING", "Pending"),
    COMPLETED("COMPLETED", "Completed"),
    FAILED("FAILED", "Failed"),
    REFUNDED("REFUNDED", "Refunded");

    private final String code;
    private final String displayName;

    PaymentStatus(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static PaymentStatus fromCode(String code) {
        for (PaymentStatus status : values()) {
            if (status.code.equalsIgnoreCase(code)) {
                return status;
            }
        }
        return PENDING;
    }
}

