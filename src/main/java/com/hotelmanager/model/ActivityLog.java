package com.hotelmanager.model;

/**
 * ActivityLog entity for tracking user actions in the system
 */
public class ActivityLog extends BaseEntity {
    private int userId;
    private String action;
    private String entityType;
    private Integer entityId;
    private String details;
    private String ipAddress;

    public ActivityLog() {
        super();
    }

    public ActivityLog(int userId, String action, String entityType, Integer entityId) {
        super();
        this.userId = userId;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public String toString() {
        return "[" + getCreatedAt() + "] " + action + " on " + entityType + (entityId != null ? " #" + entityId : "");
    }
}
