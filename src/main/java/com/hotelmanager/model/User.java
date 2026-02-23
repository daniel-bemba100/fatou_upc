package com.hotelmanager.model;

import java.time.LocalDateTime;

/**
 * User entity representing system users (admin, managers, staff)
 */
public class User extends BaseEntity {
    private String username;
    private String passwordHash;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private UserRole role;
    private boolean isActive;
    private LocalDateTime lastLogin;
    
    // Security question and answer for password recovery
    private String securityQuestion;
    private String securityAnswerHash;
    
    // Recovery token for password reset
    private String recoveryToken;
    private LocalDateTime recoveryTokenExpires;

    public User() {
        super();
        this.isActive = true;
    }

    public User(int id) {
        super(id);
        this.isActive = true;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public void setRoleId(int roleId) {
        this.role = UserRole.fromId(roleId);
    }

    public int getRoleId() {
        return role != null ? role.getId() : 0;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }
    
    // Security question getters and setters
    public String getSecurityQuestion() {
        return securityQuestion;
    }
    
    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }
    
    public String getSecurityAnswerHash() {
        return securityAnswerHash;
    }
    
    public void setSecurityAnswerHash(String securityAnswerHash) {
        this.securityAnswerHash = securityAnswerHash;
    }
    
    // Recovery token getters and setters
    public String getRecoveryToken() {
        return recoveryToken;
    }
    
    public void setRecoveryToken(String recoveryToken) {
        this.recoveryToken = recoveryToken;
    }
    
    public LocalDateTime getRecoveryTokenExpires() {
        return recoveryTokenExpires;
    }
    
    public void setRecoveryTokenExpires(LocalDateTime recoveryTokenExpires) {
        this.recoveryTokenExpires = recoveryTokenExpires;
    }
    
    /**
     * Check if recovery token is valid and not expired
     */
    public boolean isRecoveryTokenValid() {
        return recoveryToken != null && 
               !recoveryToken.isEmpty() && 
               recoveryTokenExpires != null && 
               LocalDateTime.now().isBefore(recoveryTokenExpires);
    }

    @Override
    public String toString() {
        return getFullName() + " (" + username + ")";
    }
}

