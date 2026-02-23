package com.hotelmanager.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.hotelmanager.model.Payment;
import com.hotelmanager.util.Logger;

public class PaymentDAO extends BaseDAO<Payment> {
    
    @Override
    public Payment mapResultSet(ResultSet rs) throws SQLException {
        Payment p = new Payment();
        p.setId(rs.getInt("payment_id"));
        p.setReservationId(rs.getInt("reservation_id"));
        p.setPaymentMethod(rs.getString("payment_method"));
        p.setAmount(rs.getDouble("amount"));
        p.setPaymentStatusCode(rs.getString("payment_status"));
        p.setTransactionId(rs.getString("transaction_id"));
        
        // Safely handle nullable timestamps
        java.sql.Timestamp paymentDateTs = rs.getTimestamp("payment_date");
        if (paymentDateTs != null) {
            p.setPaymentDate(paymentDateTs.toLocalDateTime());
        }
        
        p.setNotes(rs.getString("notes"));
        
        java.sql.Timestamp createdAtTs = rs.getTimestamp("created_at");
        if (createdAtTs != null) {
            p.setCreatedAt(createdAtTs.toLocalDateTime());
        }
        
        java.sql.Timestamp updatedAtTs = rs.getTimestamp("updated_at");
        if (updatedAtTs != null) {
            p.setUpdatedAt(updatedAtTs.toLocalDateTime());
        }
        
        return p;
    }
    
@Override
    public String getTableName() { return "payments"; }
    
    @Override
    public String getPrimaryKeyColumn() {
        return "payment_id";
    }
    
    @Override
    public String getInsertSQL() {
        return "INSERT INTO payments (reservation_id, payment_method, amount, payment_status, transaction_id, notes) VALUES (?, ?, ?, ?, ?, ?)";
    }
    
    @Override
    public String getUpdateSQL() {
        return "UPDATE payments SET reservation_id = ?, payment_method = ?, amount = ?, payment_status = ?, transaction_id = ?, notes = ? WHERE payment_id = ?";
    }
    
    @Override
    public void setInsertParameters(PreparedStatement ps, Payment p) throws SQLException {
        ps.setInt(1, p.getReservationId());
        ps.setString(2, p.getPaymentMethod());
        ps.setDouble(3, p.getAmount());
        ps.setString(4, p.getPaymentStatusCode());
        ps.setString(5, p.getTransactionId());
        ps.setString(6, p.getNotes());
    }
    
    @Override
    public void setUpdateParameters(PreparedStatement ps, Payment p) throws SQLException {
        ps.setInt(1, p.getReservationId());
        ps.setString(2, p.getPaymentMethod());
        ps.setDouble(3, p.getAmount());
        ps.setString(4, p.getPaymentStatusCode());
        ps.setString(5, p.getTransactionId());
        ps.setString(6, p.getNotes());
        ps.setInt(7, p.getId());
    }
    
    public List<Payment> findByReservation(int reservationId) throws SQLException {
        String sql = "SELECT * FROM payments WHERE reservation_id = ? ORDER BY payment_date DESC";
        return executeQuery(sql, this::mapResultSet, reservationId);
    }
    
    public boolean updateStatus(int paymentId, String status) throws SQLException {
        String sql = "UPDATE payments SET payment_status = ? WHERE payment_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, paymentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            Logger.error("Error updating payment status", e);
            throw e;
        }
    }
    
    public double getTotalRevenue() throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM payments WHERE payment_status = 'COMPLETED'";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        }
        return 0.0;
    }
    
    public double getTotalRevenueByDateRange(java.time.LocalDate startDate, java.time.LocalDate endDate) throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM payments WHERE payment_status = 'COMPLETED' AND payment_date BETWEEN ? AND ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(startDate));
            ps.setDate(2, Date.valueOf(endDate));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        }
        return 0.0;
    }
}
