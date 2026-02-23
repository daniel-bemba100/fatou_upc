package com.hotelmanager.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.hotelmanager.model.Customer;
import com.hotelmanager.util.Logger;

public class CustomerDAO extends BaseDAO<Customer> {
    
    @Override
    public Customer mapResultSet(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setId(rs.getInt("customer_id"));
        customer.setFirstName(rs.getString("first_name"));
        customer.setLastName(rs.getString("last_name"));
        customer.setEmail(rs.getString("email"));
        customer.setPhone(rs.getString("phone"));
        customer.setIdType(rs.getString("id_type"));
        customer.setIdNumber(rs.getString("id_number"));
        customer.setAddress(rs.getString("address"));
        customer.setCity(rs.getString("city"));
        customer.setCountry(rs.getString("country"));
        
        // Safely handle nullable date
        java.sql.Date dobDate = rs.getDate("date_of_birth");
        if (dobDate != null) {
            customer.setDateOfBirth(dobDate.toLocalDate());
        }
        
        // Safely handle nullable timestamps
        java.sql.Timestamp createdAtTs = rs.getTimestamp("created_at");
        if (createdAtTs != null) {
            customer.setCreatedAt(createdAtTs.toLocalDateTime());
        }
        
        java.sql.Timestamp updatedAtTs = rs.getTimestamp("updated_at");
        if (updatedAtTs != null) {
            customer.setUpdatedAt(updatedAtTs.toLocalDateTime());
        }
        
        return customer;
    }
    
@Override
    public String getTableName() {
        return "customers";
    }
    
    @Override
    public String getPrimaryKeyColumn() {
        return "customer_id";
    }
    
    @Override
    public String getInsertSQL() {
        return "INSERT INTO customers (first_name, last_name, email, phone, id_type, id_number, address, city, country, date_of_birth) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }
    
    @Override
    public String getUpdateSQL() {
        return "UPDATE customers SET first_name = ?, last_name = ?, email = ?, phone = ?, id_type = ?, id_number = ?, address = ?, city = ?, country = ?, date_of_birth = ? WHERE customer_id = ?";
    }
    
    @Override
    public void setInsertParameters(PreparedStatement ps, Customer customer) throws SQLException {
        ps.setString(1, customer.getFirstName());
        ps.setString(2, customer.getLastName());
        ps.setString(3, customer.getEmail());
        ps.setString(4, customer.getPhone());
        ps.setString(5, customer.getIdType());
        ps.setString(6, customer.getIdNumber());
        ps.setString(7, customer.getAddress());
        ps.setString(8, customer.getCity());
        ps.setString(9, customer.getCountry());
        if (customer.getDateOfBirth() != null) {
            ps.setDate(10, java.sql.Date.valueOf(customer.getDateOfBirth()));
        } else {
            ps.setNull(10, java.sql.Types.DATE);
        }
    }
    
    @Override
    public void setUpdateParameters(PreparedStatement ps, Customer customer) throws SQLException {
        ps.setString(1, customer.getFirstName());
        ps.setString(2, customer.getLastName());
        ps.setString(3, customer.getEmail());
        ps.setString(4, customer.getPhone());
        ps.setString(5, customer.getIdType());
        ps.setString(6, customer.getIdNumber());
        ps.setString(7, customer.getAddress());
        ps.setString(8, customer.getCity());
        ps.setString(9, customer.getCountry());
        if (customer.getDateOfBirth() != null) {
            ps.setDate(10, java.sql.Date.valueOf(customer.getDateOfBirth()));
        } else {
            ps.setNull(10, java.sql.Types.DATE);
        }
        ps.setInt(11, customer.getId());
    }
    
    public Customer findByIdNumber(String idNumber) throws SQLException {
        String sql = "SELECT * FROM customers WHERE id_number = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        } catch (SQLException e) {
            Logger.error("Error finding customer by ID number", e);
            throw e;
        }
        return null;
    }
    
    public List<Customer> search(String keyword) throws SQLException {
        String sql = "SELECT * FROM customers WHERE first_name LIKE ? OR last_name LIKE ? OR email LIKE ? OR phone LIKE ?";
        final String searchPattern = "%" + keyword + "%";
        return executeQuery(sql, rs -> {
            Customer c = new Customer();
            c.setId(rs.getInt("customer_id"));
            c.setFirstName(rs.getString("first_name"));
            c.setLastName(rs.getString("last_name"));
            c.setEmail(rs.getString("email"));
            c.setPhone(rs.getString("phone"));
            c.setIdType(rs.getString("id_type"));
            c.setIdNumber(rs.getString("id_number"));
            return c;
        });
    }
    
    /**
     * Get total count of customers
     */
    public int getTotalCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM customers";
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
