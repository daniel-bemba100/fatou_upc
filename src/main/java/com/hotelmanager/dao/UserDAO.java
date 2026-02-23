package com.hotelmanager.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;

import com.hotelmanager.model.User;
import com.hotelmanager.util.Logger;
import com.hotelmanager.util.PasswordUtil;

public class UserDAO extends BaseDAO<User> {
    
    @Override
    public User mapResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setEmail(rs.getString("email"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setPhone(rs.getString("phone"));
        user.setRoleId(rs.getInt("role_id"));
        user.setActive(rs.getBoolean("is_active"));
        
        // Safely handle nullable timestamps
        java.sql.Timestamp lastLoginTs = rs.getTimestamp("last_login");
        if (lastLoginTs != null) {
            user.setLastLogin(lastLoginTs.toLocalDateTime());
        }
        
        java.sql.Timestamp createdAtTs = rs.getTimestamp("created_at");
        if (createdAtTs != null) {
            user.setCreatedAt(createdAtTs.toLocalDateTime());
        }
        
        java.sql.Timestamp updatedAtTs = rs.getTimestamp("updated_at");
        if (updatedAtTs != null) {
            user.setUpdatedAt(updatedAtTs.toLocalDateTime());
        }
        
        // Security question fields
        user.setSecurityQuestion(rs.getString("security_question"));
        user.setSecurityAnswerHash(rs.getString("security_answer_hash"));
        
        // Recovery token fields
        user.setRecoveryToken(rs.getString("recovery_token"));
        java.sql.Timestamp recoveryTokenExpiresTs = rs.getTimestamp("recovery_token_expires");
        if (recoveryTokenExpiresTs != null) {
            user.setRecoveryTokenExpires(recoveryTokenExpiresTs.toLocalDateTime());
        }
        
        return user;
    }
    
@Override
    public String getTableName() {
        return "users";
    }
    
    @Override
    public String getPrimaryKeyColumn() {
        return "user_id";
    }
    
    @Override
    public String getInsertSQL() {
        return "INSERT INTO users (username, password_hash, email, first_name, last_name, phone, role_id, is_active, security_question, security_answer_hash) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }
    
    @Override
    public String getUpdateSQL() {
        return "UPDATE users SET username = ?, email = ?, first_name = ?, last_name = ?, phone = ?, role_id = ?, is_active = ?, security_question = ?, security_answer_hash = ? WHERE user_id = ?";
    }
    
    @Override
    public void setInsertParameters(PreparedStatement ps, User user) throws SQLException {
        ps.setString(1, user.getUsername());
        ps.setString(2, user.getPasswordHash());
        ps.setString(3, user.getEmail());
        ps.setString(4, user.getFirstName());
        ps.setString(5, user.getLastName());
        ps.setString(6, user.getPhone());
        ps.setInt(7, user.getRoleId());
        ps.setBoolean(8, user.isActive());
        ps.setString(9, user.getSecurityQuestion());
        ps.setString(10, user.getSecurityAnswerHash());
    }
    
    @Override
    public void setUpdateParameters(PreparedStatement ps, User user) throws SQLException {
        ps.setString(1, user.getUsername());
        ps.setString(2, user.getEmail());
        ps.setString(3, user.getFirstName());
        ps.setString(4, user.getLastName());
        ps.setString(5, user.getPhone());
        ps.setInt(6, user.getRoleId());
        ps.setBoolean(7, user.isActive());
        ps.setString(8, user.getSecurityQuestion());
        ps.setString(9, user.getSecurityAnswerHash());
        ps.setInt(10, user.getId());
    }
    
    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        } catch (SQLException e) {
            Logger.error("Error finding user by username", e);
            throw e;
        }
        return null;
    }
    
    /**
     * Find user by email address
     */
    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        } catch (SQLException e) {
            Logger.error("Error finding user by email", e);
            throw e;
        }
        return null;
    }
    
    /**
     * Authenticate user with username and password
     */
    public User authenticate(String username, String password) throws SQLException {
        User user = findByUsername(username);
        if (user != null && user.isActive()) {
            if (PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
                return user;
            }
        }
        return null;
    }
    
    /**
     * Change password for a user
     */
    public boolean changePassword(int userId, String newPassword) throws SQLException {
        String sql = "UPDATE users SET password_hash = ? WHERE user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, PasswordUtil.hashPassword(newPassword));
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            Logger.error("Error changing password", e);
            throw e;
        }
    }
    
    /**
     * Update security question and answer for a user
     */
    public boolean updateSecurityQuestion(int userId, String question, String answer) throws SQLException {
        String sql = "UPDATE users SET security_question = ?, security_answer_hash = ? WHERE user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, question);
            ps.setString(2, PasswordUtil.hashPassword(answer.toLowerCase().trim()));
            ps.setInt(3, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            Logger.error("Error updating security question", e);
            throw e;
        }
    }
    
    /**
     * Verify the security answer for a user
     */
    public boolean verifySecurityAnswer(int userId, String answer) throws SQLException {
        String sql = "SELECT security_answer_hash FROM users WHERE user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("security_answer_hash");
                    if (storedHash != null && !storedHash.isEmpty()) {
                        return PasswordUtil.verifyPassword(answer.toLowerCase().trim(), storedHash);
                    }
                }
            }
        } catch (SQLException e) {
            Logger.error("Error verifying security answer", e);
            throw e;
        }
        return false;
    }
    
    /**
     * Generate a recovery token for a user
     * @return the generated token, or null if failed
     */
    public String generateRecoveryToken(int userId) throws SQLException {
        // Generate a secure random token
        String token = UUID.randomUUID().toString().replace("-", "");
        // Token expires in 24 hours
        LocalDateTime expires = LocalDateTime.now().plusHours(24);
        
        String sql = "UPDATE users SET recovery_token = ?, recovery_token_expires = ? WHERE user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, token);
            ps.setTimestamp(2, java.sql.Timestamp.valueOf(expires));
            ps.setInt(3, userId);
            
            if (ps.executeUpdate() > 0) {
                Logger.info("Recovery token generated for user ID: " + userId);
                return token;
            }
        } catch (SQLException e) {
            Logger.error("Error generating recovery token", e);
            throw e;
        }
        return null;
    }
    
    /**
     * Verify if a recovery token is valid for a user
     */
    public User verifyRecoveryToken(String token) throws SQLException {
        String sql = "SELECT * FROM users WHERE recovery_token = ? AND recovery_token_expires > NOW()";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, token);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        } catch (SQLException e) {
            Logger.error("Error verifying recovery token", e);
            throw e;
        }
        return null;
    }
    
    /**
     * Reset password using recovery token
     */
    public boolean resetPasswordWithToken(String token, String newPassword) throws SQLException {
        User user = verifyRecoveryToken(token);
        if (user == null) {
            return false;
        }
        
        String sql = "UPDATE users SET password_hash = ?, recovery_token = NULL, recovery_token_expires = NULL WHERE user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, PasswordUtil.hashPassword(newPassword));
            ps.setInt(2, user.getId());
            
            if (ps.executeUpdate() > 0) {
                Logger.info("Password reset successfully for user: " + user.getUsername());
                return true;
            }
        } catch (SQLException e) {
            Logger.error("Error resetting password with token", e);
            throw e;
        }
        return false;
    }
    
    /**
     * Clear recovery token for a user
     */
    public boolean clearRecoveryToken(int userId) throws SQLException {
        String sql = "UPDATE users SET recovery_token = NULL, recovery_token_expires = NULL WHERE user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            Logger.error("Error clearing recovery token", e);
            throw e;
        }
    }
    
    /**
     * Check if any admin user exists in the database
     * @return true if admin exists, false otherwise
     */
    public boolean adminExists() throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE role_id = 1";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            Logger.error("Error checking if admin exists", e);
            throw e;
        }
        return false;
    }
    
    /**
     * Count total users in the database
     * @return total number of users
     */
    public int countUsers() throws SQLException {
        String sql = "SELECT COUNT(*) FROM users";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            Logger.error("Error counting users", e);
            throw e;
        }
        return 0;
    }
    
    /**
     * Count users by role
     * @param roleId the role ID to count
     * @return number of users with the specified role
     */
    public int countUsersByRole(int roleId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE role_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roleId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            Logger.error("Error counting users by role", e);
            throw e;
        }
        return 0;
    }
}
