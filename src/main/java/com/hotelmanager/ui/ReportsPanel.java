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
import com.hotelmanager.dao.PaymentDAO;
import com.hotelmanager.dao.ReservationDAO;
import com.hotelmanager.dao.RoomDAO;
import com.hotelmanager.util.Logger;

/**
 * Reports Panel - displays reports and analytics interface with modern styling
 */
public class ReportsPanel extends JPanel {
    
    private final HotelManagerApp mainApp;
    private final RoomDAO roomDAO;
    private final ReservationDAO reservationDAO;
    private final CustomerDAO customerDAO;
    private final PaymentDAO paymentDAO;
    private JTable reportsTable;
    private DefaultTableModel tableModel;
    
    private JButton generateButton;
    private JButton refreshButton;
    private JLabel summaryLabel;
    
    // Stat card labels
    private JLabel totalRoomsLabel;
    private JLabel totalReservationsLabel;
    private JLabel totalCustomersLabel;
    private JLabel totalRevenueLabel;
    
    // Colors
    private static final Color ROOMS_COLOR = new Color(52, 152, 219);       // Blue
    private static final Color RESERVATIONS_COLOR = new Color(243, 156, 18); // Orange
    private static final Color CUSTOMERS_COLOR = new Color(155, 89, 182);    // Purple
    private static final Color REVENUE_COLOR = new Color(39, 174, 96);      // Green
    
    public ReportsPanel(HotelManagerApp mainApp) {
        this.mainApp = mainApp;
        this.roomDAO = new RoomDAO();
        this.reservationDAO = new ReservationDAO();
        this.customerDAO = new CustomerDAO();
        this.paymentDAO = new PaymentDAO();
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
        titlePanel.add(UIFactory.createTitleLabel("Reports & Analytics"));
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(UIFactory.createSubtitleLabel("Generate and view hotel performance reports"));
        
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
        
        // Stats cards panel - 4 cards
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 15));
        statsPanel.setBackground(UIFactory.BG_COLOR);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));
        
        totalRoomsLabel = createStatCard("🛏️", "Total Rooms", "0", ROOMS_COLOR, "All hotel rooms");
        totalReservationsLabel = createStatCard("📅", "Reservations", "0", RESERVATIONS_COLOR, "Booking records");
        totalCustomersLabel = createStatCard("👥", "Customers", "0", CUSTOMERS_COLOR, "Registered guests");
        totalRevenueLabel = createStatCard("💰", "Revenue", "$0", REVENUE_COLOR, "Total income");
        
        statsPanel.add(totalRoomsLabel);
        statsPanel.add(totalReservationsLabel);
        statsPanel.add(totalCustomersLabel);
        statsPanel.add(totalRevenueLabel);
        
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
        
        generateButton = UIFactory.createPrimaryButton("📊 Generate Report");
        refreshButton = UIFactory.createSecondaryButton("🔄 Refresh");
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(generateButton);
        
        contentPanel.add(buttonPanel, BorderLayout.NORTH);
        
        // Table model
        String[] columnNames = {"Metric", "Value"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        reportsTable = new JTable(tableModel);
        reportsTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        reportsTable.setRowHeight(40);
        
        // Style the table header
        reportsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        reportsTable.getTableHeader().setBackground(UIFactory.PRIMARY_COLOR);
        reportsTable.getTableHeader().setForeground(Color.WHITE);
        reportsTable.getTableHeader().setOpaque(true);
        
        // Alternate row colors
        reportsTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
                }
                // Highlight value column
                if (column == 1 && value != null) {
                    String strValue = value.toString();
                    if (strValue.startsWith("$")) {
                        c.setForeground(REVENUE_COLOR);
                    } else if (strValue.endsWith("%")) {
                        c.setForeground(RESERVATIONS_COLOR);
                    }
                }
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(reportsTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(189, 195, 199), 1),
            new EmptyBorder(0, 0, 0, 0)
        ));
        
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Summary panel at bottom
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        summaryPanel.setBackground(UIFactory.CARD_BG);
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        summaryLabel = new JLabel("📈 Overview of hotel performance and statistics");
        summaryLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        summaryLabel.setForeground(UIFactory.TEXT_SECONDARY);
        summaryPanel.add(summaryLabel);
        
        contentPanel.add(summaryPanel, BorderLayout.SOUTH);
        
        add(contentPanel, BorderLayout.SOUTH);
        
        // Add button listeners
        generateButton.addActionListener(e -> generateReport());
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
        colorStrip.setMaximumSize(new Dimension(200, 4));
        
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
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        descLabel.setForeground(UIFactory.TEXT_SECONDARY);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(colorStrip);
        card.add(Box.createVerticalStrut(8));
        card.add(iconLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(labelText);
        card.add(Box.createVerticalStrut(2));
        card.add(descLabel);
        card.add(Box.createVerticalStrut(8));
        
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(189, 195, 199), 1),
            new EmptyBorder(8, 15, 8, 15)
        ));
        
        return valueLabel;
    }
    
    /**
     * Refresh report data from database
     */
    public void refreshData() {
        try {
            // Load summary statistics
            int totalRooms = roomDAO.getTotalCount();
            int availableRooms = roomDAO.getAvailableCount();
            int totalCustomers = customerDAO.getTotalCount();
            int totalReservations = reservationDAO.getTotalCount();
            double totalRevenue = paymentDAO.getTotalRevenue();
            
            // Update stat cards
            totalRoomsLabel.setText(String.valueOf(totalRooms));
            totalReservationsLabel.setText(String.valueOf(totalReservations));
            totalCustomersLabel.setText(String.valueOf(totalCustomers));
            totalRevenueLabel.setText(String.format("$%,.2f", totalRevenue));
            
            // Update summary
            summaryLabel.setText(String.format(
                "📈 Total Rooms: %d | Available: %d | Customers: %d | Reservations: %d | Revenue: $%,.2f",
                totalRooms, availableRooms, totalCustomers, totalReservations, totalRevenue));
            
            Logger.info("Reports data refreshed");
        } catch (Exception e) {
            Logger.error("Error loading report data", e);
            JOptionPane.showMessageDialog(this, 
                "Error loading report data: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void generateReport() {
        try {
            // Show report options
            String[] reportTypes = {
                "Room Statistics", 
                "Reservation Statistics", 
                "Financial Summary", 
                "Customer Statistics"
            };
            String selectedReport = (String) JOptionPane.showInputDialog(this,
                "Select Report Type:",
                "Generate Report",
                JOptionPane.QUESTION_MESSAGE,
                null,
                reportTypes,
                reportTypes[0]);
            
            if (selectedReport == null) return;
            
            tableModel.setRowCount(0);
            
            switch (selectedReport) {
                case "Room Statistics":
                    generateRoomReport();
                    break;
                case "Reservation Statistics":
                    generateReservationReport();
                    break;
                case "Financial Summary":
                    generateFinancialReport();
                    break;
                case "Customer Statistics":
                    generateCustomerReport();
                    break;
            }
            
            Logger.info("Report generated: " + selectedReport);
        } catch (Exception e) {
            Logger.error("Error generating report", e);
            JOptionPane.showMessageDialog(this, 
                "Error generating report: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void generateRoomReport() throws Exception {
        int totalRooms = roomDAO.getTotalCount();
        int availableRooms = roomDAO.getAvailableCount();
        int occupiedRooms = totalRooms - availableRooms;
        
        tableModel.addRow(new Object[]{"Total Rooms", totalRooms});
        tableModel.addRow(new Object[]{"Available Rooms", availableRooms});
        tableModel.addRow(new Object[]{"Occupied Rooms", occupiedRooms});
        tableModel.addRow(new Object[]{"Occupancy Rate", String.format("%.1f%%", 
            totalRooms > 0 ? (occupiedRooms * 100.0 / totalRooms) : 0)});
    }
    
    private void generateReservationReport() throws Exception {
        List<com.hotelmanager.model.Reservation> reservations = reservationDAO.findAll();
        
        int pending = 0;
        int confirmed = 0;
        int checkedIn = 0;
        int checkedOut = 0;
        int cancelled = 0;
        
        for (com.hotelmanager.model.Reservation res : reservations) {
            String status = res.getStatusCode();
            switch (status) {
                case "PENDING": pending++; break;
                case "CONFIRMED": confirmed++; break;
                case "CHECKED_IN": checkedIn++; break;
                case "CHECKED_OUT": checkedOut++; break;
                case "CANCELLED": cancelled++; break;
            }
        }
        
        tableModel.addRow(new Object[]{"Total Reservations", reservations.size()});
        tableModel.addRow(new Object[]{"Pending", pending});
        tableModel.addRow(new Object[]{"Confirmed", confirmed});
        tableModel.addRow(new Object[]{"Checked In", checkedIn});
        tableModel.addRow(new Object[]{"Checked Out", checkedOut});
        tableModel.addRow(new Object[]{"Cancelled", cancelled});
    }
    
    private void generateFinancialReport() throws Exception {
        double totalRevenue = paymentDAO.getTotalRevenue();
        
        tableModel.addRow(new Object[]{"Total Revenue", String.format("$%,.2f", totalRevenue)});
        tableModel.addRow(new Object[]{"Report Date", LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"))});
    }
    
    private void generateCustomerReport() throws Exception {
        int totalCustomers = customerDAO.getTotalCount();
        
        tableModel.addRow(new Object[]{"Total Customers", totalCustomers});
    }
}

