package com.hotelmanager.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.hotelmanager.model.RoomType;
import com.hotelmanager.util.Logger;

public class RoomTypeDAO extends BaseDAO<RoomType> {
    
    @Override
    public RoomType mapResultSet(ResultSet rs) throws SQLException {
        RoomType rt = new RoomType();
        rt.setId(rs.getInt("room_type_id"));
        rt.setTypeName(rs.getString("type_name"));
        rt.setDescription(rs.getString("description"));
        rt.setBasePrice(rs.getDouble("base_price"));
        rt.setMaxOccupancy(rs.getInt("max_occupancy"));
        rt.setAmenities(rs.getString("amenities"));
        
        // Safely handle nullable timestamps
        java.sql.Timestamp createdAtTs = rs.getTimestamp("created_at");
        if (createdAtTs != null) {
            rt.setCreatedAt(createdAtTs.toLocalDateTime());
        }
        
        java.sql.Timestamp updatedAtTs = rs.getTimestamp("updated_at");
        if (updatedAtTs != null) {
            rt.setUpdatedAt(updatedAtTs.toLocalDateTime());
        }
        
        return rt;
    }
    
@Override
    public String getTableName() {
        return "room_types";
    }
    
    @Override
    public String getPrimaryKeyColumn() {
        return "room_type_id";
    }
    
    @Override
    public String getInsertSQL() {
        return "INSERT INTO room_types (type_name, description, base_price, max_occupancy, amenities) VALUES (?, ?, ?, ?, ?)";
    }
    
    @Override
    public String getUpdateSQL() {
        return "UPDATE room_types SET type_name = ?, description = ?, base_price = ?, max_occupancy = ?, amenities = ? WHERE room_type_id = ?";
    }
    
    @Override
    public void setInsertParameters(PreparedStatement ps, RoomType rt) throws SQLException {
        ps.setString(1, rt.getTypeName());
        ps.setString(2, rt.getDescription());
        ps.setDouble(3, rt.getBasePrice());
        ps.setInt(4, rt.getMaxOccupancy());
        ps.setString(5, rt.getAmenities());
    }
    
    @Override
    public void setUpdateParameters(PreparedStatement ps, RoomType rt) throws SQLException {
        ps.setString(1, rt.getTypeName());
        ps.setString(2, rt.getDescription());
        ps.setDouble(3, rt.getBasePrice());
        ps.setInt(4, rt.getMaxOccupancy());
        ps.setString(5, rt.getAmenities());
        ps.setInt(6, rt.getId());
    }
    
    public RoomType findByName(String typeName) throws SQLException {
        String sql = "SELECT * FROM room_types WHERE type_name = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, typeName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        } catch (SQLException e) {
            Logger.error("Error finding room type by name", e);
            throw e;
        }
        return null;
    }
}
