package com.hotelmanager.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.hotelmanager.util.DBConnection;
import com.hotelmanager.util.Logger;

public abstract class BaseDAO<T> {
    
    // Abstract method to get primary key column name - must be implemented by each DAO
    public abstract String getPrimaryKeyColumn();
    
    protected Connection getConnection() throws SQLException {
        return DBConnection.getInstance().getConnection();
    }
    
    public abstract T mapResultSet(ResultSet rs) throws SQLException;
    
    public abstract String getTableName();
    
    public abstract String getInsertSQL();
    
    public abstract String getUpdateSQL();
    
    public abstract void setInsertParameters(PreparedStatement ps, T entity) throws SQLException;
    
    public abstract void setUpdateParameters(PreparedStatement ps, T entity) throws SQLException;
    
    public int insert(T entity) throws SQLException {
        String sql = getInsertSQL();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            setInsertParameters(ps, entity);
            ps.executeUpdate();
            
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            Logger.error("Error inserting into " + getTableName(), e);
            throw e;
        }
        return 0;
    }
    
    public boolean update(T entity) throws SQLException {
        String sql = getUpdateSQL();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setUpdateParameters(ps, entity);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            Logger.error("Error updating " + getTableName(), e);
            throw e;
        }
    }
    
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM " + getTableName() + " WHERE " + getPrimaryKeyColumn() + " = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            Logger.error("Error deleting from " + getTableName(), e);
            throw e;
        }
    }
    
    public T findById(int id) throws SQLException {
        String sql = "SELECT * FROM " + getTableName() + " WHERE " + getPrimaryKeyColumn() + " = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        } catch (SQLException e) {
            Logger.error("Error finding by id in " + getTableName(), e);
            throw e;
        }
        return null;
    }
    
    public List<T> findAll() throws SQLException {
        List<T> list = new ArrayList<>();
        String sql = "SELECT * FROM " + getTableName();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            Logger.error("Error finding all in " + getTableName(), e);
            throw e;
        }
        return list;
    }
    
    public List<T> executeQuery(String sql, QueryCallback<T> callback) throws SQLException {
        List<T> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(callback.map(rs));
            }
        } catch (SQLException e) {
            Logger.error("Error executing query", e);
            throw e;
        }
        return list;
    }
    
    public List<T> executeQuery(String sql, QueryCallback<T> callback, Object... params) throws SQLException {
        List<T> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setParameters(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(callback.map(rs));
                }
            }
        } catch (SQLException e) {
            Logger.error("Error executing query", e);
            throw e;
        }
        return list;
    }
    
    private void setParameters(PreparedStatement ps, Object... params) throws SQLException {
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                if (params[i] instanceof String) {
                    ps.setString(i + 1, (String) params[i]);
                } else if (params[i] instanceof Integer) {
                    ps.setInt(i + 1, (Integer) params[i]);
                } else if (params[i] instanceof Double) {
                    ps.setDouble(i + 1, (Double) params[i]);
                } else if (params[i] instanceof java.util.Date) {
                    ps.setDate(i + 1, new Date(((java.util.Date) params[i]).getTime()));
                } else if (params[i] instanceof java.time.LocalDate) {
                    ps.setDate(i + 1, Date.valueOf((java.time.LocalDate) params[i]));
                } else {
                    ps.setObject(i + 1, params[i]);
                }
            }
        }
    }
    
    @FunctionalInterface
    public interface QueryCallback<T> {
        T map(ResultSet rs) throws SQLException;
    }
}
