package com.hotelmanager.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.hotelmanager.HotelManagerApp;
import com.hotelmanager.dao.CustomerDAO;
import com.hotelmanager.dao.PaymentDAO;
import com.hotelmanager.dao.ReservationDAO;
import com.hotelmanager.dao.RoomDAO;
import com.hotelmanager.util.Logger;

/**
 * Dashboard Panel - displays statistics and overview with modern styling
 */
public class DashboardPanel extends JPanel {
    
    private final HotelManagerApp mainApp;
    private JLabel totalRoomsLabel;
    private JLabel availableRoomsLabel;
    private JLabel totalReservationsLabel;
    private JLabel totalCustomersLabel;
    private JLabel totalRevenueLabel;
    
    // DAO instances
    private final RoomDAO roomDAO;
    private final ReservationDAO reservationDAO;
    private final CustomerDAO customerDAO;
    private final PaymentDAO paymentDAO;
    
    // Color palette for stat cards
    private static final Color ROOMS_COLOR = new Color(52, 152, 219);      // Blue
    private static final Color AVAILABLE_COLOR = new Color(39, 174, 96);   // Green
    private static final Color RESERVATIONS_COLOR = new Color(243, 156, 18); // Orange
    private static final Color CUSTOMERS_COLOR = new Color(155, 89, 182);   // Purple
    private static final Color REVENUE_COLOR = new Color(44, 62, 80);      // Dark
    
    public DashboardPanel(HotelManagerApp mainApp) {
        this.mainApp = mainApp;
        
        // Initialize DAOs
        this.roomDAO = new RoomDAO();
        this.reservationDAO = new ReservationDAO();
        this.customerDAO = new CustomerDAO();
        this.paymentDAO = new PaymentDAO();
        
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(UIFactory.BG_COLOR);
        
        // Header with logo
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIFactory.CARD_BG);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        // Left side - Title and subtitle
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(UIFactory.CARD_BG);
        titlePanel.add(UIFactory.createTitleLabel("Dashboard"));
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(UIFactory.createSubtitleLabel("Welcome to Hotel Manager Pro"));
        
        // Right side - Logo
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoPanel.setBackground(UIFactory.CARD_BG);
        
        // Load and scale the logo
        try {
            ImageIcon originalIcon = new ImageIcon(getClass().getResource("/icons/UPC_BRAND.png"));
            if (originalIcon.getImage() != null) {
                Image scaledImage = originalIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                ImageIcon scaledIcon = new ImageIcon(scaledImage);
                JLabel logoLabel = new JLabel(scaledIcon);
                logoPanel.add(logoLabel);
            }
        } catch (Exception e) {
            Logger.warn("Could not load logo: " + e.getMessage());
        }
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(logoPanel, BorderLayout.EAST);
        
        // Stats cards panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 5, 20, 20));
        statsPanel.setBackground(UIFactory.BG_COLOR);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Create stat cards with color coding
        totalRoomsLabel = createStatCard("🛏️", "Total Rooms", "0", ROOMS_COLOR, "All hotel rooms in system");
        availableRoomsLabel = createStatCard("✅", "Available Rooms", "0", AVAILABLE_COLOR, "Rooms ready for booking");
        totalReservationsLabel = createStatCard("📅", "Total Reservations", "0", RESERVATIONS_COLOR, "All booking records");
        totalCustomersLabel = createStatCard("👥", "Total Customers", "0", CUSTOMERS_COLOR, "Registered guests");
        totalRevenueLabel = createStatCard("💰", "Total Revenue", "$0", REVENUE_COLOR, "Income from payments");
        
        statsPanel.add(totalRoomsLabel);
        statsPanel.add(availableRoomsLabel);
        statsPanel.add(totalReservationsLabel);
        statsPanel.add(totalCustomersLabel);
        statsPanel.add(totalRevenueLabel);
        
        // Content area
        JPanel contentPanel = new JPanel(new GridLayout(2, 1, 20, 20));
        contentPanel.setBackground(UIFactory.BG_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 30, 30));
        
        // Quick actions panel
        JPanel quickActions = createModernCard();
        quickActions.setLayout(new BoxLayout(quickActions, BoxLayout.Y_AXIS));
        
        JLabel quickActionsTitle = UIFactory.createLabel("⚡ Quick Actions", 18, true);
        quickActionsTitle.setIconTextGap(10);
        quickActions.add(quickActionsTitle);
        quickActions.add(Box.createVerticalStrut(15));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton newReservationBtn = UIFactory.createPrimaryButton("+ New Reservation");
        buttonPanel.add(newReservationBtn);
        
        JButton addCustomerBtn = UIFactory.createSecondaryButton("+ Add Customer");
        buttonPanel.add(addCustomerBtn);
        
        JButton checkInBtn = UIFactory.createSecondaryButton("✓ Check In");
        buttonPanel.add(checkInBtn);
        
        quickActions.add(buttonPanel);
        
        // Recent activity panel
        JPanel recentActivity = createModernCard();
        recentActivity.setLayout(new BoxLayout(recentActivity, BoxLayout.Y_AXIS));
        
        JLabel activityTitle = UIFactory.createLabel("📊 Recent Activity", 18, true);
        activityTitle.setIconTextGap(10);
        recentActivity.add(activityTitle);
        recentActivity.add(Box.createVerticalStrut(15));
        
        JTextArea activityArea = new JTextArea("• Room 101 checked in - John Doe\n" +
                                               "• Reservation #45 completed - Sarah Smith\n" +
                                               "• New customer registered - Mike Johnson\n" +
                                               "• Payment received - $250.00");
        activityArea.setEditable(false);
        activityArea.setBackground(Color.WHITE);
        activityArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        activityArea.setForeground(UIFactory.TEXT_COLOR);
        activityArea.setLineWrap(true);
        activityArea.setWrapStyleWord(true);
        recentActivity.add(activityArea);
        
        contentPanel.add(quickActions);
        contentPanel.add(recentActivity);
        
        add(headerPanel, BorderLayout.NORTH);
        add(statsPanel, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Create a modern stat card with color coding, icon, and hover effect
     */
    private JLabel createStatCard(String icon, String label, String value, Color accentColor, String description) {
        // Main card panel
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Create colored top border
        JPanel colorStrip = new JPanel();
        colorStrip.setBackground(accentColor);
        colorStrip.setMaximumSize(new java.awt.Dimension(200, 4));
        
        // Icon with colored background circle
        JPanel iconContainer = new JPanel();
        iconContainer.setLayout(new FlowLayout(FlowLayout.CENTER));
        iconContainer.setBackground(Color.WHITE);
        
        JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        
        // Value label - large number
        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLabel.setForeground(accentColor);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Main label
        JLabel labelText = new JLabel(label, SwingConstants.CENTER);
        labelText.setFont(new Font("Segoe UI", Font.BOLD, 14));
        labelText.setForeground(UIFactory.TEXT_COLOR);
        labelText.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Description label
        JLabel descLabel = new JLabel(description, SwingConstants.CENTER);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        descLabel.setForeground(UIFactory.TEXT_SECONDARY);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Add components
        card.add(colorStrip);
        card.add(Box.createVerticalStrut(15));
        card.add(iconLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(labelText);
        card.add(Box.createVerticalStrut(5));
        card.add(descLabel);
        card.add(Box.createVerticalStrut(15));
        
        // Add hover effect
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(accentColor, 2),
                    new EmptyBorder(19, 19, 19, 19)
                ));
                card.setBackground(new Color(250, 250, 250));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(new Color(189, 195, 199), 1),
                    new EmptyBorder(20, 20, 20, 20)
                ));
                card.setBackground(Color.WHITE);
            }
        });
        
        // Set border
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(189, 195, 199), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        // Return the value label for updating
        return valueLabel;
    }
    
    /**
     * Create a modern card with subtle shadow effect
     */
    private JPanel createModernCard() {
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(189, 195, 199), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        return card;
    }
    
    /**
     * Refresh dashboard data from database
     */
    public void refreshData() {
        try {
            // Get total rooms count
            int totalRooms = roomDAO.getTotalCount();
            totalRoomsLabel.setText(String.valueOf(totalRooms));
            
            // Get available rooms count
            int availableRooms = roomDAO.getAvailableCount();
            availableRoomsLabel.setText(String.valueOf(availableRooms));
            
            // Get total reservations count
            int totalReservations = reservationDAO.getTotalCount();
            totalReservationsLabel.setText(String.valueOf(totalReservations));
            
            // Get total customers count
            int totalCustomers = customerDAO.getTotalCount();
            totalCustomersLabel.setText(String.valueOf(totalCustomers));
            
            // Get total revenue
            double totalRevenue = paymentDAO.getTotalRevenue();
            totalRevenueLabel.setText(String.format("$%,.2f", totalRevenue));
            
            Logger.info("Dashboard data refreshed successfully");
        } catch (Exception e) {
            Logger.error("Error refreshing dashboard data", e);
            // Keep existing values on error
        }
    }
}

