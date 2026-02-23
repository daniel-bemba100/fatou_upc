package com.hotelmanager.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import com.hotelmanager.model.Reservation;
import com.hotelmanager.util.Logger;

public class ReservationDAO extends BaseDAO<Reservation> {
    
    @Override
    public Reservation mapResultSet(ResultSet rs) throws SQLException {
        Reservation res = new Reservation();
        res.setId(rs.getInt("reservation_id"));
        res.setCustomerId(rs.getInt("customer_id"));
        res.setRoomId(rs.getInt("room_id"));
        res.setUserId(rs.getInt("user_id"));
        
        // Safely handle nullable dates
        java.sql.Date checkInDate = rs.getDate("check_in_date");
        if (checkInDate != null) {
            res.setCheckInDate(checkInDate.toLocalDate());
        }
        
        java.sql.Date checkOutDate = rs.getDate("check_out_date");
        if (checkOutDate != null) {
            res.setCheckOutDate(checkOutDate.toLocalDate());
        }
        
        res.setNumberOfGuests(rs.getInt("number_of_guests"));
        res.setTotalAmount(rs.getDouble("total_amount"));
        res.setStatusCode(rs.getString("status"));
        res.setNotes(rs.getString("notes"));
        
        // Safely handle nullable timestamps
        java.sql.Timestamp createdAtTs = rs.getTimestamp("created_at");
        if (createdAtTs != null) {
            res.setCreatedAt(createdAtTs.toLocalDateTime());
        }
        
        java.sql.Timestamp updatedAtTs = rs.getTimestamp("updated_at");
        if (updatedAtTs != null) {
            res.setUpdatedAt(updatedAtTs.toLocalDateTime());
        }
        
        return res;
    }
    
@Override
    public String getTableName() { return "reservations"; }
    
    @Override
    public String getPrimaryKeyColumn() {
        return "reservation_id";
    }
    
    @Override
    public String getInsertSQL() {
        return "INSERT INTO reservations (customer_id, room_id, user_id, check_in_date, check_out_date, number_of_guests, total_amount, status, notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }
    
    @Override
    public String getUpdateSQL() {
        return "UPDATE reservations SET customer_id = ?, room_id = ?, user_id = ?, check_in_date = ?, check_out_date = ?, number_of_guests = ?, total_amount = ?, status = ?, notes = ? WHERE reservation_id = ?";
    }
    
    @Override
    public void setInsertParameters(PreparedStatement ps, Reservation res) throws SQLException {
        ps.setInt(1, res.getCustomerId());
        ps.setInt(2, res.getRoomId());
        ps.setInt(3, res.getUserId());
        ps.setDate(4, Date.valueOf(res.getCheckInDate()));
        ps.setDate(5, Date.valueOf(res.getCheckOutDate()));
        ps.setInt(6, res.getNumberOfGuests());
        ps.setDouble(7, res.getTotalAmount());
        ps.setString(8, res.getStatusCode());
        ps.setString(9, res.getNotes());
    }
    
    @Override
    public void setUpdateParameters(PreparedStatement ps, Reservation res) throws SQLException {
        ps.setInt(1, res.getCustomerId());
        ps.setInt(2, res.getRoomId());
        ps.setInt(3, res.getUserId());
        ps.setDate(4, Date.valueOf(res.getCheckInDate()));
        ps.setDate(5, Date.valueOf(res.getCheckOutDate()));
        ps.setInt(6, res.getNumberOfGuests());
        ps.setDouble(7, res.getTotalAmount());
        ps.setString(8, res.getStatusCode());
        ps.setString(9, res.getNotes());
        ps.setInt(10, res.getId());
    }
    
    public boolean updateStatus(int reservationId, String status) throws SQLException {
        String sql = "UPDATE reservations SET status = ? WHERE reservation_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, reservationId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            Logger.error("Error updating reservation status", e);
            throw e;
        }
    }
    
    public boolean isRoomAvailable(int roomId, LocalDate checkIn, LocalDate checkOut) throws SQLException {
        String sql = "SELECT COUNT(*) FROM reservations WHERE room_id = ? AND status IN ('PENDING', 'CONFIRMED', 'CHECKED_IN') AND NOT (check_out_date <= ? OR check_in_date >= ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ps.setDate(2, Date.valueOf(checkIn));
            ps.setDate(3, Date.valueOf(checkOut));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0;
                }
            }
        }
        return false;
    }
    
    public List<Reservation> findByStatus(com.hotelmanager.model.ReservationStatus status) throws SQLException {
        String sql = "SELECT * FROM reservations WHERE status = ? ORDER BY created_at DESC";
        return executeQuery(sql, this::mapResultSet, status.getCode());
    }
    
    public List<Reservation> findActiveReservations() throws SQLException {
        String sql = "SELECT * FROM reservations WHERE status IN ('PENDING', 'CONFIRMED', 'CHECKED_IN') ORDER BY check_in_date DESC";
        return executeQuery(sql, this::mapResultSet);
    }
    
    public List<Reservation> findByDateRange(LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = "SELECT * FROM reservations WHERE check_in_date BETWEEN ? AND ? OR check_out_date BETWEEN ? AND ? ORDER BY check_in_date DESC";
        return executeQuery(sql, this::mapResultSet, startDate, endDate);
    }
    
    /**
     * Get total count of reservations
     */
    public int getTotalCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM reservations";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
}
