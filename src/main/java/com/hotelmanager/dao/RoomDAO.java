package com.hotelmanager.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.hotelmanager.model.Room;
import com.hotelmanager.model.RoomStatus;
import com.hotelmanager.util.Logger;

public class RoomDAO extends BaseDAO<Room> {
    
    @Override
    public Room mapResultSet(ResultSet rs) throws SQLException {
        Room room = new Room();
        room.setId(rs.getInt("room_id"));
        room.setRoomNumber(rs.getString("room_number"));
        room.setFloor(rs.getInt("floor"));
        room.setRoomTypeId(rs.getInt("room_type_id"));
        room.setStatusCode(rs.getString("status"));
        room.setDescription(rs.getString("description"));
        
        // Handle price field - may not exist in older databases
        try {
            double price = rs.getDouble("price");
            if (!rs.wasNull()) {
                room.setPrice(price);
            }
        } catch (SQLException e) {
            // Column doesn't exist, use default price
            room.setPrice(0.0);
        }
        
        // Safely handle nullable timestamps
        java.sql.Timestamp createdAtTs = rs.getTimestamp("created_at");
        if (createdAtTs != null) {
            room.setCreatedAt(createdAtTs.toLocalDateTime());
        }
        
        java.sql.Timestamp updatedAtTs = rs.getTimestamp("updated_at");
        if (updatedAtTs != null) {
            room.setUpdatedAt(updatedAtTs.toLocalDateTime());
        }
        
        return room;
    }
    
@Override
    public String getTableName() {
        return "rooms";
    }
    
    @Override
    public String getPrimaryKeyColumn() {
        return "room_id";
    }
    
    @Override
    public String getInsertSQL() {
        // Price column may not exist in older databases
        return "INSERT INTO rooms (room_number, floor, room_type_id, status, description) VALUES (?, ?, ?, ?, ?)";
    }
    
    @Override
    public String getUpdateSQL() {
        return "UPDATE rooms SET room_number = ?, floor = ?, room_type_id = ?, status = ?, description = ? WHERE room_id = ?";
    }
    
    @Override
    public void setInsertParameters(PreparedStatement ps, Room room) throws SQLException {
        ps.setString(1, room.getRoomNumber());
        ps.setInt(2, room.getFloor());
        ps.setInt(3, room.getRoomTypeId());
        ps.setString(4, room.getStatusCode());
        ps.setString(5, room.getDescription());
    }
    
    @Override
    public void setUpdateParameters(PreparedStatement ps, Room room) throws SQLException {
        ps.setString(1, room.getRoomNumber());
        ps.setInt(2, room.getFloor());
        ps.setInt(3, room.getRoomTypeId());
        ps.setString(4, room.getStatusCode());
        ps.setString(5, room.getDescription());
        ps.setInt(6, room.getId());
    }
    
    /**
     * Save a room (insert if new, update if exists)
     */
    public int save(Room room) throws SQLException {
        if (room.getId() > 0) {
            update(room);
            return room.getId();
        } else {
            return insert(room);
        }
    }
    
    public Room findByRoomNumber(String roomNumber) throws SQLException {
        String sql = "SELECT * FROM rooms WHERE room_number = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roomNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        } catch (SQLException e) {
            Logger.error("Error finding room by number", e);
            throw e;
        }
        return null;
    }
    
    public List<Room> findByStatus(RoomStatus status) throws SQLException {
        String sql = "SELECT * FROM rooms WHERE status = ?";
        return executeQuery(sql, rs -> {
            Room r = new Room();
            r.setId(rs.getInt("room_id"));
            r.setRoomNumber(rs.getString("room_number"));
            r.setFloor(rs.getInt("floor"));
            r.setRoomTypeId(rs.getInt("room_type_id"));
            r.setStatusCode(rs.getString("status"));
            return r;
        });
    }
    
    public List<Room> findAvailableRooms() throws SQLException {
        String sql = "SELECT * FROM rooms WHERE status = 'AVAILABLE' ORDER BY floor, room_number";
        return executeQuery(sql, this::mapResultSet);
    }
    
    public boolean updateStatus(int roomId, RoomStatus status) throws SQLException {
        String sql = "UPDATE rooms SET status = ? WHERE room_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.getCode());
            ps.setInt(2, roomId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            Logger.error("Error updating room status", e);
            throw e;
        }
    }
    
    /**
     * Get total count of rooms
     */
    public int getTotalCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM rooms";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
    
    /**
     * Get count of available rooms
     */
    public int getAvailableCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM rooms WHERE status = 'AVAILABLE'";
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
