-- ============================================
-- Hotel Manager Pro - Database Schema
-- MySQL/MariaDB
-- ============================================

-- Create database
CREATE DATABASE IF NOT EXISTS hotel_manager_db
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE hotel_manager_db;

-- ============================================
-- TABLES
-- ============================================

-- 1. ROLES TABLE
CREATE TABLE IF NOT EXISTS roles (
    role_id INT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. USERS TABLE
CREATE TABLE IF NOT EXISTS users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    role_id INT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    last_login TIMESTAMP NULL,
    -- Security question and answer for password recovery
    security_question VARCHAR(255),
    security_answer_hash VARCHAR(255),
    -- Recovery token for password reset
    recovery_token VARCHAR(64),
    recovery_token_expires TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE RESTRICT,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_role_id (role_id),
    INDEX idx_recovery_token (recovery_token)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. CUSTOMERS TABLE
CREATE TABLE IF NOT EXISTS customers (
    customer_id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20) NOT NULL,
    id_type VARCHAR(20) NOT NULL, -- PASSPORT, DRIVING_LICENSE, NATIONAL_ID
    id_number VARCHAR(50) NOT NULL,
    address VARCHAR(255),
    city VARCHAR(100),
    country VARCHAR(100),
    date_of_birth DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_phone (phone),
    INDEX idx_id_number (id_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. ROOM TYPES TABLE
CREATE TABLE IF NOT EXISTS room_types (
    room_type_id INT PRIMARY KEY AUTO_INCREMENT,
    type_name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    base_price DECIMAL(10, 2) NOT NULL,
    max_occupancy INT NOT NULL DEFAULT 2,
    amenities TEXT, -- JSON array of amenities
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_type_name (type_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. ROOMS TABLE
CREATE TABLE IF NOT EXISTS rooms (
    room_id INT PRIMARY KEY AUTO_INCREMENT,
    room_number VARCHAR(10) NOT NULL UNIQUE,
    floor INT NOT NULL,
    room_type_id INT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE', -- AVAILABLE, OCCUPIED, MAINTENANCE, CLEANING
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (room_type_id) REFERENCES room_types(room_type_id) ON DELETE RESTRICT,
    INDEX idx_room_number (room_number),
    INDEX idx_status (status),
    INDEX idx_room_type (room_type_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. SERVICES TABLE (Additional hotel services)
CREATE TABLE IF NOT EXISTS services (
    service_id INT PRIMARY KEY AUTO_INCREMENT,
    service_name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    price DECIMAL(10, 2) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_service_name (service_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7. RESERVATIONS TABLE
CREATE TABLE IF NOT EXISTS reservations (
    reservation_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT NOT NULL,
    room_id INT NOT NULL,
    user_id INT NOT NULL, -- Who made the reservation
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    number_of_guests INT NOT NULL DEFAULT 1,
    total_amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING, CONFIRMED, CHECKED_IN, CHECKED_OUT, CANCELLED
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE RESTRICT,
    FOREIGN KEY (room_id) REFERENCES rooms(room_id) ON DELETE RESTRICT,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE RESTRICT,
    INDEX idx_customer (customer_id),
    INDEX idx_room (room_id),
    INDEX idx_status (status),
    INDEX idx_check_in (check_in_date),
    INDEX idx_check_out (check_out_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 8. PAYMENTS TABLE
CREATE TABLE IF NOT EXISTS payments (
    payment_id INT PRIMARY KEY AUTO_INCREMENT,
    reservation_id INT NOT NULL,
    payment_method VARCHAR(20) NOT NULL, -- CASH, CREDIT_CARD, DEBIT_CARD, BANK_TRANSFER
    amount DECIMAL(10, 2) NOT NULL,
    payment_status VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING, COMPLETED, FAILED, REFUNDED
    transaction_id VARCHAR(100) UNIQUE,
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (reservation_id) REFERENCES reservations(reservation_id) ON DELETE CASCADE,
    INDEX idx_reservation (reservation_id),
    INDEX idx_payment_status (payment_status),
    INDEX idx_payment_date (payment_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 9. STAFF TABLE
CREATE TABLE IF NOT EXISTS staff (
    staff_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    department VARCHAR(50),
    position VARCHAR(50),
    hire_date DATE NOT NULL,
    salary DECIMAL(10, 2),
    emergency_contact VARCHAR(100),
    emergency_phone VARCHAR(20),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_department (department),
    INDEX idx_position (position)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 10. REPORTS TABLE
CREATE TABLE IF NOT EXISTS reports (
    report_id INT PRIMARY KEY AUTO_INCREMENT,
    report_type VARCHAR(50) NOT NULL, -- OCCUPANCY, REVENUE, STAFF_ACTIVITY
    generated_by INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    report_data TEXT, -- JSON or serialized data
    file_path VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (generated_by) REFERENCES users(user_id) ON DELETE RESTRICT,
    INDEX idx_report_type (report_type),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 11. ACTIVITY LOG TABLE
CREATE TABLE IF NOT EXISTS activity_logs (
    log_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50),
    entity_id INT,
    details TEXT,
    ip_address VARCHAR(45),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_user (user_id),
    INDEX idx_action (action),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- INITIAL DATA
-- ============================================

-- Insert Roles
INSERT INTO roles (role_name, description) VALUES
('ADMIN', 'Full system access and user management'),
('MANAGER', 'Hotel management and reporting access'),
('RECEPTIONIST', 'Reservation and customer management access');

-- Insert Room Types
INSERT INTO room_types (type_name, description, base_price, max_occupancy, amenities) VALUES
('STANDARD', 'Basic comfortable room with essential amenities', 100.00, 2, '["TV", "WiFi", "Air Conditioning", "Private Bathroom"]'),
('DELUXE', 'Spacious room with premium amenities', 180.00, 3, '["TV", "WiFi", "Air Conditioning", "Private Bathroom", "Mini Bar", "Safe"]'),
('SUITE', 'Luxury suite with living area and premium services', 300.00, 4, '["TV", "WiFi", "Air Conditioning", "Private Bathroom", "Mini Bar", "Safe", "Living Room", "Jacuzzi"]'),
('FAMILY', 'Large room for families', 220.00, 5, '["TV", "WiFi", "Air Conditioning", "Private Bathroom", "Extra Beds"]'),
('SINGLE', 'Compact room for solo travelers', 80.00, 1, '["TV", "WiFi", "Air Conditioning", "Private Bathroom"]');

-- Insert Rooms (Sample data)
INSERT INTO rooms (room_number, floor, room_type_id, status, description) VALUES
-- Floor 1 - Standard & Single
('101', 1, 1, 'AVAILABLE', 'Standard room with garden view'),
('102', 1, 1, 'AVAILABLE', 'Standard room with pool view'),
('103', 1, 5, 'AVAILABLE', 'Single room compact'),
('104', 1, 5, 'MAINTENANCE', 'Single room under maintenance'),
('105', 1, 1, 'OCCUPIED', 'Standard room'),
-- Floor 2 - Deluxe
('201', 2, 2, 'AVAILABLE', 'Deluxe room with city view'),
('202', 2, 2, 'AVAILABLE', 'Deluxe room with sea view'),
('203', 2, 2, 'AVAILABLE', 'Deluxe room'),
('204', 2, 2, 'OCCUPIED', 'Deluxe room'),
('205', 2, 2, 'CLEANING', 'Deluxe room being cleaned'),
-- Floor 3 - Suite
('301', 3, 3, 'AVAILABLE', 'Suite with panoramic view'),
('302', 3, 3, 'AVAILABLE', 'Suite with balcony'),
('303', 3, 3, 'AVAILABLE', 'Executive Suite'),
-- Floor 4 - Family
('401', 4, 4, 'AVAILABLE', 'Family room with two bedrooms'),
('402', 4, 4, 'AVAILABLE', 'Family room'),
('403', 4, 4, 'OCCUPIED', 'Family room'),
('404', 4, 4, 'AVAILABLE', 'Family room');

-- Insert Services
INSERT INTO services (service_name, description, price, is_active) VALUES
('Room Service', 'In-room dining service', 15.00, TRUE),
('Spa Treatment', 'Full body massage', 80.00, TRUE),
('Airport Transfer', 'Airport pickup/drop-off service', 50.00, TRUE),
('Laundry Service', 'Clothing cleaning and ironing', 25.00, TRUE),
('Breakfast Buffet', 'Daily breakfast inclusion', 20.00, TRUE),
('Gym Access', 'Hotel gym usage', 10.00, TRUE),
('Pool Access', 'Hotel pool usage', 15.00, TRUE),
('Parking', 'Secure parking per night', 20.00, TRUE);

-- Insert Sample Customers
INSERT INTO customers (first_name, last_name, email, phone, id_type, id_number, address, city, country) VALUES
('John', 'Smith', 'john.smith@email.com', '+1-555-0101', 'PASSPORT', 'P123456', '123 Main St', 'New York', 'USA'),
('Emma', 'Johnson', 'emma.j@email.com', '+1-555-0102', 'NATIONAL_ID', 'ID789012', '456 Oak Ave', 'Los Angeles', 'USA'),
('Michael', 'Brown', 'm.brown@email.com', '+1-555-0103', 'DRIVING_LICENSE', 'DL456789', '789 Pine Rd', 'Chicago', 'USA'),
('Sophie', 'Davis', 'sophie.d@email.com', '+44-20-1234-5678', 'PASSPORT', 'P987654', '10 Downing St', 'London', 'UK'),
('Carlos', 'Garcia', 'c.garcia@email.com', '+34-91-123-4567', 'NATIONAL_ID', 'ID321654', 'Calle Gran Via 25', 'Madrid', 'Spain');

-- Insert Admin User (password: admin123 - bcrypt hash)
INSERT INTO users (username, password_hash, email, first_name, last_name, phone, role_id) VALUES
('admin', '$2a$10$8f7jkZqN5pXj8N9K0vXyZOZqF5vJ8vJ0vJ0vJ0vJ0vJ0vJ0vJ0u', 'admin@hotel.com', 'System', 'Administrator', '+1-555-0001', 1),
('manager', '$2a$10$8f7jkZqN5pXj8N9K0vXyZOZqF5vJ8vJ0vJ0vJ0vJ0vJ0vJ0vJ0u', 'manager@hotel.com', 'Jane', 'Manager', '+1-555-0002', 2),
('reception', '$2a$10$8f7jkZqN5pXj8N9K0vXyZOZqF5vJ8vJ0vJ0vJ0vJ0vJ0vJ0vJ0u', 'reception@hotel.com', 'Bob', 'Receptionist', '+1-555-0003', 3);

-- Insert Sample Reservations
INSERT INTO reservations (customer_id, room_id, user_id, check_in_date, check_out_date, number_of_guests, total_amount, status, notes) VALUES
(1, 5, 3, '2024-01-15', '2024-01-18', 2, 300.00, 'CHECKED_IN', 'VIP guest'),
(2, 7, 3, '2024-01-16', '2024-01-20', 2, 720.00, 'CHECKED_IN', 'Honeymoon couple'),
(3, 10, 3, '2024-01-10', '2024-01-12', 1, 180.00, 'CHECKED_OUT', 'Business traveler'),
(4, 15, 3, '2024-01-18', '2024-01-25', 4, 1540.00, 'CONFIRMED', 'Family vacation'),
(5, 3, 3, '2024-01-20', '2024-01-22', 1, 160.00, 'PENDING', 'Solo traveler');

-- Insert Sample Payments
INSERT INTO payments (reservation_id, payment_method, amount, payment_status, transaction_id, notes) VALUES
(1, 'CREDIT_CARD', 300.00, 'COMPLETED', 'TXN001', 'Paid at check-in'),
(2, 'BANK_TRANSFER', 720.00, 'COMPLETED', 'TXN002', 'Advance payment'),
(3, 'CASH', 180.00, 'COMPLETED', 'TXN003', 'Paid at check-out'),
(4, 'CREDIT_CARD', 500.00, 'PENDING', 'TXN004', 'Advance deposit');

-- ============================================
-- VIEWS
-- ============================================

-- Room availability view
CREATE OR REPLACE VIEW v_room_availability AS
SELECT 
    r.room_id,
    r.room_number,
    r.floor,
    rt.type_name,
    rt.base_price,
    rt.max_occupancy,
    r.status,
    CASE 
        WHEN r.status = 'AVAILABLE' THEN 'Available'
        WHEN r.status = 'OCCUPIED' THEN 'Occupied'
        WHEN r.status = 'MAINTENANCE' THEN 'Maintenance'
        WHEN r.status = 'CLEANING' THEN 'Cleaning'
    END AS status_text
FROM rooms r
JOIN room_types rt ON r.room_type_id = rt.room_type_id;

-- Reservation details view
CREATE OR REPLACE VIEW v_reservation_details AS
SELECT 
    res.reservation_id,
    CONCAT(c.first_name, ' ', c.last_name) AS customer_name,
    c.email AS customer_email,
    c.phone AS customer_phone,
    r.room_number,
    rt.type_name AS room_type,
    res.check_in_date,
    res.check_out_date,
    res.number_of_guests,
    res.total_amount,
    res.status,
    CONCAT(u.first_name, ' ', u.last_name) AS booked_by,
    res.created_at
FROM reservations res
JOIN customers c ON res.customer_id = c.customer_id
JOIN rooms r ON res.room_id = r.room_id
JOIN room_types rt ON r.room_type_id = rt.room_type_id
JOIN users u ON res.user_id = u.user_id;

-- Payment summary view
CREATE OR REPLACE VIEW v_payment_summary AS
SELECT 
    p.payment_id,
    p.reservation_id,
    CONCAT(c.first_name, ' ', c.last_name) AS customer_name,
    r.room_number,
    p.amount,
    p.payment_method,
    p.payment_status,
    p.payment_date
FROM payments p
JOIN reservations res ON p.reservation_id = res.reservation_id
JOIN customers c ON res.customer_id = c.customer_id
JOIN rooms r ON res.room_id = r.room_id;

-- ============================================
-- STORED PROCEDURES
-- ============================================

DELIMITER //

-- Get available rooms for date range
CREATE PROCEDURE sp_get_available_rooms(
    IN p_check_in DATE,
    IN p_check_out DATE
)
BEGIN
    SELECT 
        r.room_id,
        r.room_number,
        r.floor,
        rt.type_name,
        rt.base_price,
        rt.max_occupancy
    FROM rooms r
    JOIN room_types rt ON r.room_type_id = rt.room_type_id
    WHERE r.status = 'AVAILABLE'
    AND r.room_id NOT IN (
        SELECT room_id 
        FROM reservations 
        WHERE status IN ('PENDING', 'CONFIRMED', 'CHECKED_IN')
        AND NOT (p_check_out <= check_in_date OR p_check_in >= check_out_date)
    )
    ORDER BY r.floor, r.room_number;
END //

-- Calculate room revenue
CREATE PROCEDURE sp_get_room_revenue(
    IN p_start_date DATE,
    IN p_end_date DATE
)
BEGIN
    SELECT 
        r.room_number,
        rt.type_name,
        COUNT(res.reservation_id) AS total_bookings,
        SUM(res.total_amount) AS total_revenue
    FROM rooms r
    JOIN room_types rt ON r.room_type_id = rt.room_type_id
    LEFT JOIN reservations res ON r.room_id = res.room_id
        AND res.status IN ('CHECKED_IN', 'CHECKED_OUT')
        AND res.check_in_date >= p_start_date
        AND res.check_out_date <= p_end_date
    GROUP BY r.room_id, r.room_number, rt.type_name
    ORDER BY total_revenue DESC;
END //

-- Get occupancy report
CREATE PROCEDURE sp_get_occupancy_report(
    IN p_start_date DATE,
    IN p_end_date DATE
)
BEGIN
    SELECT 
        rt.type_name,
        COUNT(r.room_id) AS total_rooms,
        SUM(CASE 
            WHEN res.reservation_id IS NOT NULL 
            AND res.status IN ('CHECKED_IN', 'CHECKED_OUT')
            THEN 1 
            ELSE 0 
        END) AS occupied_count,
        ROUND(SUM(CASE 
            WHEN res.reservation_id IS NOT NULL 
            AND res.status IN ('CHECKED_IN', 'CHECKED_OUT')
            THEN 1 
            ELSE 0 
        END) / COUNT(r.room_id) * 100, 2) AS occupancy_percentage
    FROM room_types rt
    LEFT JOIN rooms r ON rt.room_type_id = r.room_type_id
    LEFT JOIN reservations res ON r.room_id = res.room_id
        AND res.check_in_date <= p_end_date
        AND res.check_out_date >= p_start_date
    GROUP BY rt.room_type_id, rt.type_name;
END //

DELIMITER ;

-- ============================================
-- END OF SCHEMA
-- ============================================

