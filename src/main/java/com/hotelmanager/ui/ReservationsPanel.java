package com.hotelmanager.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
import com.hotelmanager.dao.CustomerDAO;
import com.hotelmanager.dao.ReservationDAO;
import com.hotelmanager.dao.RoomDAO;
import com.hotelmanager.model.Customer;
import com.hotelmanager.model.Reservation;
import com.hotelmanager.model.Room;
import com.hotelmanager.util.Logger;

/**
 * Reservations Panel - displays reservation management interface with modern styling
 */
public class ReservationsPanel extends JPanel {
    
    private final HotelManagerApp mainApp;
    private final ReservationDAO reservationDAO;
    private final CustomerDAO customerDAO;
    private final RoomDAO roomDAO;
    private JTable reservationsTable;
    private DefaultTableModel tableModel;
    
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    
    // Status labels for stat cards
    private JLabel pendingLabel;
    private JLabel confirmedLabel;
    private JLabel checkedInLabel;
    private JLabel checkedOutLabel;
    private JLabel cancelledLabel;
    
    // Color palette for reservation statuses
    private static final Color PENDING_COLOR = new Color(243, 156, 18);      // Orange
    private static final Color CONFIRMED_COLOR = new Color(52, 152, 219);    // Blue
    private static final Color CHECKED_IN_COLOR = new Color(39, 174, 96);   // Green
    private static final Color CHECKED_OUT_COLOR = new Color(44, 62, 80);   // Dark
    private static final Color CANCELLED_COLOR = new Color(231, 76, 60);    // Red
    
    public ReservationsPanel(HotelManagerApp mainApp) {
        this.mainApp = mainApp;
        this.reservationDAO = new ReservationDAO();
        this.customerDAO = new CustomerDAO();
        this.roomDAO = new RoomDAO();
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
        titlePanel.add(UIFactory.createTitleLabel("Reservations Management"));
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(UIFactory.createSubtitleLabel("Manage guest bookings and reservations"));
        
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
        
        // Stats cards panel - Reservation status overview
        JPanel statsPanel = new JPanel(new GridLayout(1, 5, 15, 15));
        statsPanel.setBackground(UIFactory.BG_COLOR);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));
        
        pendingLabel = createStatCard("⏳", "Pending", "0", PENDING_COLOR, "Awaiting confirmation");
        confirmedLabel = createStatCard("✅", "Confirmed", "0", CONFIRMED_COLOR, "Booked & confirmed");
        checkedInLabel = createStatCard("🏨", "Checked In", "0", CHECKED_IN_COLOR, "Currently staying");
        checkedOutLabel = createStatCard("📝", "Checked Out", "0", CHECKED_OUT_COLOR, "Completed stay");
        cancelledLabel = createStatCard("❌", "Cancelled", "0", CANCELLED_COLOR, "Canceled bookings");
        
        statsPanel.add(pendingLabel);
        statsPanel.add(confirmedLabel);
        statsPanel.add(checkedInLabel);
        statsPanel.add(checkedOutLabel);
        statsPanel.add(cancelledLabel);
        
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
        
        addButton = UIFactory.createPrimaryButton("+ New Reservation");
        editButton = UIFactory.createSecondaryButton("✏️ Edit");
        deleteButton = UIFactory.createDangerButton("❌ Cancel");
        refreshButton = UIFactory.createSecondaryButton("🔄 Refresh");
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        
        contentPanel.add(buttonPanel, BorderLayout.NORTH);
        
        // Table model
        String[] columnNames = {"ID", "Customer", "Room", "Check In", "Check Out", "Guests", "Total", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        reservationsTable = new JTable(tableModel);
        reservationsTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        reservationsTable.setRowHeight(40);
        
        // Style the table header
        reservationsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        reservationsTable.getTableHeader().setBackground(UIFactory.PRIMARY_COLOR);
        reservationsTable.getTableHeader().setForeground(Color.WHITE);
        reservationsTable.getTableHeader().setOpaque(true);
        
        // Alternate row colors and status color coding
        reservationsTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
                }
                // Color the status column
                if (column == 7 && value != null) {
                    String status = value.toString();
                    switch (status) {
                        case "PENDING":
                            c.setForeground(PENDING_COLOR);
                            break;
                        case "CONFIRMED":
                            c.setForeground(CONFIRMED_COLOR);
                            break;
                        case "CHECKED_IN":
                            c.setForeground(CHECKED_IN_COLOR);
                            break;
                        case "CHECKED_OUT":
                            c.setForeground(CHECKED_OUT_COLOR);
                            break;
                        case "CANCELLED":
                            c.setForeground(CANCELLED_COLOR);
                            break;
                    }
                }
                return c;
            }
        });
        
        reservationsTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        
        // Hide ID column
        reservationsTable.getColumnModel().getColumn(0).setMinWidth(0);
        reservationsTable.getColumnModel().getColumn(0).setMaxWidth(0);
        reservationsTable.getColumnModel().getColumn(0).setWidth(0);
        
        JScrollPane scrollPane = new JScrollPane(reservationsTable);
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
        
        JLabel statusLabel = new JLabel("💡 Double-click a row to edit | Track all guest bookings here");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        statusLabel.setForeground(UIFactory.TEXT_SECONDARY);
        statusPanel.add(statusLabel);
        
        contentPanel.add(statusPanel, BorderLayout.SOUTH);
        
        add(contentPanel, BorderLayout.SOUTH);
        
        // Add button listeners
        addButton.addActionListener(e -> showAddReservationDialog());
        editButton.addActionListener(e -> showEditReservationDialog());
        deleteButton.addActionListener(e -> cancelSelectedReservation());
        refreshButton.addActionListener(e -> refreshData());
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
        colorStrip.setMaximumSize(new Dimension(120, 4));
        
        JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(accentColor);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel labelText = new JLabel(label, SwingConstants.CENTER);
        labelText.setFont(new Font("Segoe UI", Font.BOLD, 12));
        labelText.setForeground(UIFactory.TEXT_COLOR);
        labelText.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel descLabel = new JLabel(description, SwingConstants.CENTER);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        descLabel.setForeground(UIFactory.TEXT_SECONDARY);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(colorStrip);
        card.add(Box.createVerticalStrut(8));
        card.add(iconLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(3));
        card.add(labelText);
        card.add(Box.createVerticalStrut(2));
        card.add(descLabel);
        card.add(Box.createVerticalStrut(8));
        
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(189, 195, 199), 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        
        return valueLabel;
    }
    
    /**
     * Refresh reservation data from database
     */
    public void refreshData() {
        try {
            List<Reservation> reservations = reservationDAO.findAll();
            tableModel.setRowCount(0);
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
            
            // Count reservations by status
            int pending = 0;
            int confirmed = 0;
            int checkedIn = 0;
            int checkedOut = 0;
            int cancelled = 0;
            
            for (Reservation res : reservations) {
                String customerName = getCustomerName(res.getCustomerId());
                String roomNumber = getRoomNumber(res.getRoomId());
                
                Object[] rowData = {
                    res.getId(),
                    customerName,
                    roomNumber,
                    res.getCheckInDate() != null ? res.getCheckInDate().format(dateFormatter) : "",
                    res.getCheckOutDate() != null ? res.getCheckOutDate().format(dateFormatter) : "",
                    res.getNumberOfGuests(),
                    String.format("$%.2f", res.getTotalAmount()),
                    res.getStatusCode()
                };
                tableModel.addRow(rowData);
                
                // Count by status
                String status = res.getStatusCode();
                switch (status) {
                    case "PENDING":
                        pending++;
                        break;
                    case "CONFIRMED":
                        confirmed++;
                        break;
                    case "CHECKED_IN":
                        checkedIn++;
                        break;
                    case "CHECKED_OUT":
                        checkedOut++;
                        break;
                    case "CANCELLED":
                        cancelled++;
                        break;
                }
            }
            
            // Update stat cards
            pendingLabel.setText(String.valueOf(pending));
            confirmedLabel.setText(String.valueOf(confirmed));
            checkedInLabel.setText(String.valueOf(checkedIn));
            checkedOutLabel.setText(String.valueOf(checkedOut));
            cancelledLabel.setText(String.valueOf(cancelled));
            
            Logger.info("Reservations data refreshed: " + reservations.size() + " reservations loaded");
        } catch (Exception e) {
            Logger.error("Error loading reservations", e);
            JOptionPane.showMessageDialog(this, 
                "Error loading reservations: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String getCustomerName(int customerId) {
        try {
            Customer customer = customerDAO.findById(customerId);
            return customer != null ? customer.getFirstName() + " " + customer.getLastName() : "Unknown";
        } catch (Exception e) {
            return "Unknown";
        }
    }
    
    private String getRoomNumber(int roomId) {
        try {
            Room room = roomDAO.findById(roomId);
            return room != null ? room.getRoomNumber() : "Unknown";
        } catch (Exception e) {
            return "Unknown";
        }
    }
    
    private void showAddReservationDialog() {
        JPanel panel = new JPanel(new java.awt.GridLayout(7, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        javax.swing.JComboBox<Customer> customerCombo = new javax.swing.JComboBox<>();
        javax.swing.JComboBox<Room> roomCombo = new javax.swing.JComboBox<>();
        javax.swing.JTextField checkInField = new javax.swing.JTextField("YYYY-MM-DD");
        javax.swing.JTextField checkOutField = new javax.swing.JTextField("YYYY-MM-DD");
        javax.swing.JSpinner guestsSpinner = new javax.swing.JSpinner(new javax.swing.SpinnerNumberModel(1, 1, 10, 1));
        javax.swing.JTextField totalField = new javax.swing.JTextField();
        javax.swing.JComboBox<String> statusCombo = new javax.swing.JComboBox<>(new String[]{"PENDING", "CONFIRMED", "CHECKED_IN", "CHECKED_OUT", "CANCELLED"});
        
        try {
            List<Customer> customers = customerDAO.findAll();
            for (Customer c : customers) {
                customerCombo.addItem(c);
            }
            
            List<Room> rooms = roomDAO.findAvailableRooms();
            for (Room r : rooms) {
                roomCombo.addItem(r);
            }
        } catch (Exception e) {
            Logger.error("Error loading data for dialog", e);
        }
        
        panel.add(new JLabel("Customer:"));
        panel.add(customerCombo);
        panel.add(new JLabel("Room:"));
        panel.add(roomCombo);
        panel.add(new JLabel("Check In (YYYY-MM-DD):"));
        panel.add(checkInField);
        panel.add(new JLabel("Check Out (YYYY-MM-DD):"));
        panel.add(checkOutField);
        panel.add(new JLabel("Guests:"));
        panel.add(guestsSpinner);
        panel.add(new JLabel("Total Amount ($):"));
        panel.add(totalField);
        panel.add(new JLabel("Status:"));
        panel.add(statusCombo);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "New Reservation", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                Customer selectedCustomer = (Customer) customerCombo.getSelectedItem();
                Room selectedRoom = (Room) roomCombo.getSelectedItem();
                
                if (selectedCustomer == null || selectedRoom == null) {
                    JOptionPane.showMessageDialog(this, "Please select customer and room",
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                Reservation res = new Reservation();
                res.setCustomerId(selectedCustomer.getId());
                res.setRoomId(selectedRoom.getId());
                res.setCheckInDate(LocalDate.parse(checkInField.getText()));
                res.setCheckOutDate(LocalDate.parse(checkOutField.getText()));
                res.setNumberOfGuests((Integer) guestsSpinner.getValue());
                res.setTotalAmount(Double.parseDouble(totalField.getText()));
                res.setStatusCode((String) statusCombo.getSelectedItem());
                
                reservationDAO.insert(res);
                refreshData();
                JOptionPane.showMessageDialog(this, "Reservation created successfully!");
                Logger.info("Reservation created for customer: " + selectedCustomer.getFirstName());
            } catch (Exception e) {
                Logger.error("Error creating reservation", e);
                JOptionPane.showMessageDialog(this, "Error creating reservation: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void showEditReservationDialog() {
        int selectedRow = reservationsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a reservation to edit",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int reservationId = (Integer) tableModel.getValueAt(selectedRow, 0);
        
        try {
            Reservation res = reservationDAO.findById(reservationId);
            if (res == null) {
                JOptionPane.showMessageDialog(this, "Reservation not found",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            JPanel panel = new JPanel(new java.awt.GridLayout(7, 2, 10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            
            javax.swing.JTextField checkInField = new javax.swing.JTextField(res.getCheckInDate() != null ? res.getCheckInDate().format(dateFormatter) : "");
            javax.swing.JTextField checkOutField = new javax.swing.JTextField(res.getCheckOutDate() != null ? res.getCheckOutDate().format(dateFormatter) : "");
            javax.swing.JSpinner guestsSpinner = new javax.swing.JSpinner(new javax.swing.SpinnerNumberModel(res.getNumberOfGuests(), 1, 10, 1));
            javax.swing.JTextField totalField = new javax.swing.JTextField(String.valueOf(res.getTotalAmount()));
            javax.swing.JComboBox<String> statusCombo = new javax.swing.JComboBox<>(new String[]{"PENDING", "CONFIRMED", "CHECKED_IN", "CHECKED_OUT", "CANCELLED"});
            statusCombo.setSelectedItem(res.getStatusCode());
            
            panel.add(new JLabel("Check In (YYYY-MM-DD):"));
            panel.add(checkInField);
            panel.add(new JLabel("Check Out (YYYY-MM-DD):"));
            panel.add(checkOutField);
            panel.add(new JLabel("Guests:"));
            panel.add(guestsSpinner);
            panel.add(new JLabel("Total Amount ($):"));
            panel.add(totalField);
            panel.add(new JLabel("Status:"));
            panel.add(statusCombo);
            
            int result = JOptionPane.showConfirmDialog(this, panel, "Edit Reservation", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
            if (result == JOptionPane.OK_OPTION) {
                res.setCheckInDate(LocalDate.parse(checkInField.getText()));
                res.setCheckOutDate(LocalDate.parse(checkOutField.getText()));
                res.setNumberOfGuests((Integer) guestsSpinner.getValue());
                res.setTotalAmount(Double.parseDouble(totalField.getText()));
                res.setStatusCode((String) statusCombo.getSelectedItem());
                
                reservationDAO.update(res);
                refreshData();
                JOptionPane.showMessageDialog(this, "Reservation updated successfully!");
                Logger.info("Reservation updated: ID " + reservationId);
            }
        } catch (Exception e) {
            Logger.error("Error editing reservation", e);
            JOptionPane.showMessageDialog(this, "Error editing reservation: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cancelSelectedReservation() {
        int selectedRow = reservationsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a reservation to cancel",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to cancel this reservation?",
            "Confirm Cancel", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int reservationId = (Integer) tableModel.getValueAt(selectedRow, 0);
                reservationDAO.updateStatus(reservationId, "CANCELLED");
                refreshData();
                JOptionPane.showMessageDialog(this, "Reservation cancelled successfully!");
                Logger.info("Reservation cancelled: ID " + reservationId);
            } catch (Exception e) {
                Logger.error("Error cancelling reservation", e);
                JOptionPane.showMessageDialog(this, "Error cancelling reservation: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

