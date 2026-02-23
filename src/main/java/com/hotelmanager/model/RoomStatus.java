package com.hotelmanager.model;

/**
 * Enum representing room status
 */
public enum RoomStatus {
    AVAILABLE("AVAILABLE", "Available"),
    OCCUPIED("OCCUPIED", "Occupied"),
    MAINTENANCE("MAINTENANCE", "Under Maintenance"),
    CLEANING("CLEANING", "Being Cleaned");

    private final String code;
    private final String displayName;

    RoomStatus(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static RoomStatus fromCode(String code) {
        for (RoomStatus status : values()) {
            if (status.code.equalsIgnoreCase(code)) {
                return status;
            }
        }
        return AVAILABLE;
    }
}

