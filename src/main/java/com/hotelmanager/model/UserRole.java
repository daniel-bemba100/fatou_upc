package com.hotelmanager.model;

/**
 * Enum representing user roles in the system
 */
public enum UserRole {
    ADMIN(1, "Administrator", "Full system access"),
    MANAGER(2, "Manager", "Hotel management and reporting"),
    RECEPTIONIST(3, "Receptionist", "Reservation and customer management");

    private final int id;
    private final String displayName;
    private final String description;

    UserRole(int id, String displayName, String description) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public static UserRole fromId(int id) {
        for (UserRole role : values()) {
            if (role.id == id) {
                return role;
            }
        }
        return null;
    }

    public static UserRole fromName(String name) {
        for (UserRole role : values()) {
            if (role.name().equalsIgnoreCase(name)) {
                return role;
            }
        }
        return null;
    }
}

