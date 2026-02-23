package com.hotelmanager.model;

/**
 * Enum representing reservation status
 */
public enum ReservationStatus {
    PENDING("PENDING", "Pending"),
    CONFIRMED("CONFIRMED", "Confirmed"),
    CHECKED_IN("CHECKED_IN", "Checked In"),
    CHECKED_OUT("CHECKED_OUT", "Checked Out"),
    CANCELLED("CANCELLED", "Cancelled");

    private final String code;
    private final String displayName;

    ReservationStatus(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ReservationStatus fromCode(String code) {
        for (ReservationStatus status : values()) {
            if (status.code.equalsIgnoreCase(code)) {
                return status;
            }
        }
        return PENDING;
    }
}

