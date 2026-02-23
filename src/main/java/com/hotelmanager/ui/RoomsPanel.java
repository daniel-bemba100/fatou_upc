package com.hotelmanager.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import com.hotelmanager.HotelManagerApp;
import com.hotelmanager.dao.RoomDAO;
import com.hotelmanager.dao.RoomTypeDAO;
import com.hotelmanager.model.Room;
import com.hotelmanager.model.RoomType;
import com.hotelmanager.util.Logger;

/**
 * Rooms Panel - displays room management interface with modern styling
 */
public class RoomsPanel extends JPanel {
    
    private final HotelManagerApp mainApp;
    private final RoomDAO roomDAO;
    private final RoomTypeDAO roomTypeDAO;
    private JTable roomsTable;
    private DefaultTableModel tableModel;
    
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    
    // Status labels for stat cards
    private JLabel availableLabel;
    private JLabel occupiedLabel;
    private JLabel maintenanceLabel;
    private JLabel reservedLabel;
    
    // Color palette for room statuses
    private static final Color AVAILABLE_COLOR = new Color(39, 174, 96);    // Green
    private static final Color OCCUPIED_COLOR = new Color(52, 152, 219);    // Blue
    private static final Color MAINTENANCE_COLOR = new Color(243, 156, 18);  // Orange
    private static final Color RESERVED_COLOR = new Color(155, 89, 182);    // Purple
    
    public RoomsPanel(HotelManagerApp mainApp) {
        this.mainApp = mainApp;
        this.roomDAO = new RoomDAO();
        this.roomTypeDAO = new RoomTypeDAO();
        initializeUI();
        refreshData();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(UIFactory.BG_COLOR);
        
        // Header with logo
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIFactory.CARD_BG);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        // Left side - Title
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(UIFactory.CARD_BG);
        titlePanel.add(UIFactory.createTitleLabel("Rooms Management"));
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(UIFactory.createSubtitleLabel("Manage your hotel rooms and availability"));
        
        // Right side - Logo
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoPanel.setBackground(UIFactory.CARD_BG);
        
        try {
            ImageIcon originalIcon = new ImageIcon(getClass().getResource("/icons/UPC_BRAND.png"));
            if (originalIcon.getImage() != null) {
                Image scaledImage = originalIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                ImageIcon scaledIcon = new ImageIcon(scaledImage);
                JLabel logoLabel = new JLabel(scaledIcon);
                logoPanel.add(logoLabel);
            }
        } catch (Exception e) {
            Logger.warn("Could not load logo: " + e.getMessage());
        }
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(logoPanel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Stats cards panel - Room status overview
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 15));
        statsPanel.setBackground(UIFactory.BG_COLOR);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));
        
        availableLabel = createStatCard("✅", "Available", "0", AVAILABLE_COLOR, "Ready for booking");
        occupiedLabel = createStatCard("🏠", "Occupied", "0", OCCUPIED_COLOR, "Currently in use");
        maintenanceLabel = createStatCard("🔧", "Maintenance", "0", MAINTENANCE_COLOR, "Under maintenance");
        reservedLabel = createStatCard("📅", "Reserved", "0", RESERVED_COLOR, "Booked for later");
        
        statsPanel.add(availableLabel);
        statsPanel.add(occupiedLabel);
        statsPanel.add(maintenanceLabel);
        statsPanel.add(reservedLabel);
        
        add(statsPanel, BorderLayout.CENTER);
        
        // Content - Table with buttons
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(UIFactory.BG_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 20, 30));
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(189, 195, 199), 1),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        addButton = UIFactory.createPrimaryButton("+ Add Room");
        editButton = UIFactory.createSecondaryButton("✏️ Edit Room");
        deleteButton = UIFactory.createDangerButton("🗑️ Delete Room");
        refreshButton = UIFactory.createSecondaryButton("🔄 Refresh");
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        
        contentPanel.add(buttonPanel, BorderLayout.NORTH);
        
        // Table model
        String[] columnNames = {"ID", "Room Number", "Floor", "Room Type", "Status", "Price", "Description"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        roomsTable = new JTable(tableModel);
        roomsTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        roomsTable.setRowHeight(40);
        
        // Style the table header
        roomsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        roomsTable.getTableHeader().setBackground(UIFactory.PRIMARY_COLOR);
        roomsTable.getTableHeader().setForeground(Color.WHITE);
        roomsTable.getTableHeader().setOpaque(true);
        
        // Alternate row colors
        roomsTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
                }
                // Color the status column
                if (column == 4 && value != null) {
                    String status = value.toString();
                    switch (status) {
                        case "AVAILABLE":
                            c.setForeground(AVAILABLE_COLOR);
                            break;
                        case "OCCUPIED":
                            c.setForeground(OCCUPIED_COLOR);
                            break;
                        case "MAINTENANCE":
                            c.setForeground(MAINTENANCE_COLOR);
                            break;
                        case "RESERVED":
                            c.setForeground(RESERVED_COLOR);
                            break;
                    }
                }
                return c;
            }
        });
        
        roomsTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        
        // Hide ID column
        roomsTable.getColumnModel().getColumn(0).setMinWidth(0);
        roomsTable.getColumnModel().getColumn(0).setMaxWidth(0);
        roomsTable.getColumnModel().getColumn(0).setWidth(0);
        
        JScrollPane scrollPane = new JScrollPane(roomsTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(189, 195, 199), 1),
            new EmptyBorder(0, 0, 0, 0)
        ));
        scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(189, 195, 199);
            }
        });
        
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Status bar
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(UIFactory.CARD_BG);
        statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel statusLabel = new JLabel("💡 Double-click a row to edit | Room prices shown per night");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        statusLabel.setForeground(UIFactory.TEXT_SECONDARY);
        statusPanel.add(statusLabel);
        
        contentPanel.add(statusPanel, BorderLayout.SOUTH);
        
        add(contentPanel, BorderLayout.SOUTH);
        
        // Add button listeners
        addButton.addActionListener(e -> showAddRoomDialog());
        editButton.addActionListener(e -> showEditRoomDialog());
        deleteButton.addActionListener(e -> deleteSelectedRoom());
        refreshButton.addActionListener(e -> refreshData());
        
        // Double-click to edit
        roomsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    showEditRoomDialog();
                }
            }
        });
    }
    
    /**
     * Create a modern stat card with color coding
     */
    private JLabel createStatCard(String icon, String label, String value, Color accentColor, String description) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Colored top strip
        JPanel colorStrip = new JPanel();
        colorStrip.setBackground(accentColor);
        colorStrip.setMaximumSize(new Dimension(150, 4));
        
        JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(accentColor);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel labelText = new JLabel(label, SwingConstants.CENTER);
        labelText.setFont(new Font("Segoe UI", Font.BOLD, 13));
        labelText.setForeground(UIFactory.TEXT_COLOR);
        labelText.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel descLabel = new JLabel(description, SwingConstants.CENTER);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        descLabel.setForeground(UIFactory.TEXT_SECONDARY);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(colorStrip);
        card.add(Box.createVerticalStrut(10));
        card.add(iconLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(labelText);
        card.add(Box.createVerticalStrut(3));
        card.add(descLabel);
        card.add(Box.createVerticalStrut(10));
        
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(189, 195, 199), 1),
            new EmptyBorder(10, 15, 10, 15)
        ));
        
        return valueLabel;
    }
    
    /**
     * Refresh room data from database
     */
    public void refreshData() {
        try {
            List<Room> rooms = roomDAO.findAll();
            tableModel.setRowCount(0);
            
            // Count rooms by status
            int available = 0;
            int occupied = 0;
            int maintenance = 0;
            int reserved = 0;
            
            for (Room room : rooms) {
                String roomTypeName = getRoomTypeName(room.getRoomTypeId());
                Object[] rowData = {
                    room.getId(),
                    room.getRoomNumber(),
                    room.getFloor(),
                    roomTypeName,
                    room.getStatusCode(),
                    String.format("$%.2f/night", room.getPrice()),
                    room.getDescription()
                };
                tableModel.addRow(rowData);
                
                // Count by status
                String status = room.getStatusCode();
                switch (status) {
                    case "AVAILABLE":
                        available++;
                        break;
                    case "OCCUPIED":
                        occupied++;
                        break;
                    case "MAINTENANCE":
                        maintenance++;
                        break;
                    case "RESERVED":
                        reserved++;
                        break;
                }
            }
            
            // Update stat cards
            availableLabel.setText(String.valueOf(available));
            occupiedLabel.setText(String.valueOf(occupied));
            maintenanceLabel.setText(String.valueOf(maintenance));
            reservedLabel.setText(String.valueOf(reserved));
            
            Logger.info("Rooms data refreshed: " + rooms.size() + " rooms loaded");
        } catch (Exception e) {
            Logger.error("Error loading rooms", e);
            JOptionPane.showMessageDialog(this, 
                "Error loading rooms: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String getRoomTypeName(int roomTypeId) {
        try {
            RoomType roomType = roomTypeDAO.findById(roomTypeId);
            return roomType != null ? roomType.getTypeName() : "Unknown";
        } catch (Exception e) {
            return "Unknown";
        }
    }
    
    private void showAddRoomDialog() {
        JPanel panel = new JPanel(new java.awt.GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        javax.swing.JTextField roomNumberField = new javax.swing.JTextField();
        javax.swing.JSpinner floorSpinner = new javax.swing.JSpinner(new javax.swing.SpinnerNumberModel(1, 1, 20, 1));
        javax.swing.JComboBox<String> typeCombo = new javax.swing.JComboBox<>();
        javax.swing.JComboBox<String> statusCombo = new javax.swing.JComboBox<>(new String[]{"AVAILABLE", "OCCUPIED", "MAINTENANCE", "RESERVED"});
        javax.swing.JTextField priceField = new javax.swing.JTextField();
        javax.swing.JTextField descriptionField = new javax.swing.JTextField();
        
        try {
            List<RoomType> roomTypes = roomTypeDAO.findAll();
            for (RoomType rt : roomTypes) {
                typeCombo.addItem(rt.getTypeName());
            }
        } catch (Exception e) {
            typeCombo.addItem("Standard");
        }
        
        panel.add(new JLabel("Room Number:"));
        panel.add(roomNumberField);
        panel.add(new JLabel("Floor:"));
        panel.add(floorSpinner);
        panel.add(new JLabel("Room Type:"));
        panel.add(typeCombo);
        panel.add(new JLabel("Status:"));
        panel.add(statusCombo);
        panel.add(new JLabel("Price ($):"));
        panel.add(priceField);
        panel.add(new JLabel("Description:"));
        panel.add(descriptionField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Room", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                Room room = new Room();
                room.setRoomNumber(roomNumberField.getText());
                room.setFloor((Integer) floorSpinner.getValue());
                room.setStatusCode((String) statusCombo.getSelectedItem());
                room.setPrice(Double.parseDouble(priceField.getText()));
                room.setDescription(descriptionField.getText());
                
                // Get room type ID
                String selectedType = (String) typeCombo.getSelectedItem();
                List<RoomType> roomTypes = roomTypeDAO.findAll();
                for (RoomType rt : roomTypes) {
                    if (rt.getTypeName().equals(selectedType)) {
                        room.setRoomTypeId(rt.getId());
                        break;
                    }
                }
                
                roomDAO.save(room);
                refreshData();
                JOptionPane.showMessageDialog(this, "Room added successfully!");
                Logger.info("Room added: " + room.getRoomNumber());
            } catch (Exception e) {
                Logger.error("Error adding room", e);
                JOptionPane.showMessageDialog(this, "Error adding room: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void showEditRoomDialog() {
        int selectedRow = roomsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a room to edit",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int roomId = (Integer) tableModel.getValueAt(selectedRow, 0);
        
        try {
            Room room = roomDAO.findById(roomId);
            if (room == null) {
                JOptionPane.showMessageDialog(this, "Room not found",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            JPanel panel = new JPanel(new java.awt.GridLayout(6, 2, 10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            javax.swing.JTextField roomNumberField = new javax.swing.JTextField(room.getRoomNumber());
            javax.swing.JSpinner floorSpinner = new javax.swing.JSpinner(new javax.swing.SpinnerNumberModel(room.getFloor(), 1, 20, 1));
            javax.swing.JComboBox<String> typeCombo = new javax.swing.JComboBox<>();
            javax.swing.JComboBox<String> statusCombo = new javax.swing.JComboBox<>(new String[]{"AVAILABLE", "OCCUPIED", "MAINTENANCE", "RESERVED"});
            javax.swing.JTextField priceField = new javax.swing.JTextField(String.valueOf(room.getPrice()));
            javax.swing.JTextField descriptionField = new javax.swing.JTextField(room.getDescription());
            
            try {
                List<RoomType> roomTypes = roomTypeDAO.findAll();
                int index = 0;
                for (int i = 0; i < roomTypes.size(); i++) {
                    RoomType rt = roomTypes.get(i);
                    typeCombo.addItem(rt.getTypeName());
                    if (rt.getId() == room.getRoomTypeId()) {
                        index = i;
                    }
                }
                typeCombo.setSelectedIndex(index);
            } catch (Exception e) {
                typeCombo.addItem("Standard");
            }
            
            statusCombo.setSelectedItem(room.getStatusCode());
            
            panel.add(new JLabel("Room Number:"));
            panel.add(roomNumberField);
            panel.add(new JLabel("Floor:"));
            panel.add(floorSpinner);
            panel.add(new JLabel("Room Type:"));
            panel.add(typeCombo);
            panel.add(new JLabel("Status:"));
            panel.add(statusCombo);
            panel.add(new JLabel("Price ($):"));
            panel.add(priceField);
            panel.add(new JLabel("Description:"));
            panel.add(descriptionField);
            
            int result = JOptionPane.showConfirmDialog(this, panel, "Edit Room", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
            if (result == JOptionPane.OK_OPTION) {
                room.setRoomNumber(roomNumberField.getText());
                room.setFloor((Integer) floorSpinner.getValue());
                room.setStatusCode((String) statusCombo.getSelectedItem());
                room.setPrice(Double.parseDouble(priceField.getText()));
                room.setDescription(descriptionField.getText());
                
                // Get room type ID
                String selectedType = (String) typeCombo.getSelectedItem();
                List<RoomType> roomTypes = roomTypeDAO.findAll();
                for (RoomType rt : roomTypes) {
                    if (rt.getTypeName().equals(selectedType)) {
                        room.setRoomTypeId(rt.getId());
                        break;
                    }
                }
                
                roomDAO.update(room);
                refreshData();
                JOptionPane.showMessageDialog(this, "Room updated successfully!");
                Logger.info("Room updated: " + room.getRoomNumber());
            }
        } catch (Exception e) {
            Logger.error("Error editing room", e);
            JOptionPane.showMessageDialog(this, "Error editing room: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteSelectedRoom() {
        int selectedRow = roomsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a room to delete",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this room?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int roomId = (Integer) tableModel.getValueAt(selectedRow, 0);
                roomDAO.delete(roomId);
                refreshData();
                JOptionPane.showMessageDialog(this, "Room deleted successfully!");
                Logger.info("Room deleted: ID " + roomId);
            } catch (Exception e) {
                Logger.error("Error deleting room", e);
                JOptionPane.showMessageDialog(this, "Error deleting room: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

