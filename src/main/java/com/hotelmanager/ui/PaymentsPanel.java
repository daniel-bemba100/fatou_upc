package com.hotelmanager.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
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
import com.hotelmanager.dao.PaymentDAO;
import com.hotelmanager.dao.ReservationDAO;
import com.hotelmanager.model.Payment;
import com.hotelmanager.model.Reservation;
import com.hotelmanager.util.Logger;

/**
 * Payments Panel - displays payment management interface with modern styling
 */
public class PaymentsPanel extends JPanel {
    
    private final HotelManagerApp mainApp;
    private final PaymentDAO paymentDAO;
    private final ReservationDAO reservationDAO;
    private JTable paymentsTable;
    private DefaultTableModel tableModel;
    
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    
    // Stat card labels
    private JLabel totalPaymentsLabel;
    private JLabel completedPaymentsLabel;
    private JLabel pendingPaymentsLabel;
    private JLabel totalRevenueLabel;
    
    // Colors for payment status
    private static final Color REVENUE_COLOR = new Color(44, 62, 80);       // Dark
    private static final Color COMPLETED_COLOR = new Color(39, 174, 96);   // Green
    private static final Color PENDING_COLOR = new Color(243, 156, 18);    // Orange
    private static final Color FAILED_COLOR = new Color(231, 76, 60);      // Red
    
    public PaymentsPanel(HotelManagerApp mainApp) {
        this.mainApp = mainApp;
        this.paymentDAO = new PaymentDAO();
        this.reservationDAO = new ReservationDAO();
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
        titlePanel.add(UIFactory.createTitleLabel("Payments Management"));
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(UIFactory.createSubtitleLabel("Track and manage all payment transactions"));
        
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
        
        totalPaymentsLabel = createStatCard("📊", "Total Payments", "0", REVENUE_COLOR, "All payment records");
        completedPaymentsLabel = createStatCard("✅", "Completed", "0", COMPLETED_COLOR, "Successful payments");
        pendingPaymentsLabel = createStatCard("⏳", "Pending", "0", PENDING_COLOR, "Awaiting payment");
        totalRevenueLabel = createStatCard("💰", "Total Revenue", "$0", COMPLETED_COLOR, "Income from payments");
        
        statsPanel.add(totalPaymentsLabel);
        statsPanel.add(completedPaymentsLabel);
        statsPanel.add(pendingPaymentsLabel);
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
        
        addButton = UIFactory.createPrimaryButton("+ Record Payment");
        editButton = UIFactory.createSecondaryButton("✏️ Edit");
        deleteButton = UIFactory.createDangerButton("🗑️ Delete");
        refreshButton = UIFactory.createSecondaryButton("🔄 Refresh");
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        
        contentPanel.add(buttonPanel, BorderLayout.NORTH);
        
        // Table model
        String[] columnNames = {"ID", "Reservation", "Amount", "Method", "Status", "Date", "Transaction ID"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        paymentsTable = new JTable(tableModel);
        paymentsTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        paymentsTable.setRowHeight(40);
        
        // Style the table header
        paymentsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        paymentsTable.getTableHeader().setBackground(UIFactory.PRIMARY_COLOR);
        paymentsTable.getTableHeader().setForeground(Color.WHITE);
        paymentsTable.getTableHeader().setOpaque(true);
        
        // Color-coded status column
        paymentsTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
                }
                // Color the Status column
                if (column == 4 && value != null) {
                    String status = value.toString();
                    if ("COMPLETED".equals(status)) {
                        c.setForeground(COMPLETED_COLOR);
                    } else if ("PENDING".equals(status)) {
                        c.setForeground(PENDING_COLOR);
                    } else if ("FAILED".equals(status)) {
                        c.setForeground(FAILED_COLOR);
                    } else if ("REFUNDED".equals(status)) {
                        c.setForeground(new Color(155, 89, 182));
                    }
                }
                // Highlight amount column
                if (column == 2 && value != null) {
                    c.setForeground(REVENUE_COLOR);
                }
                return c;
            }
        });
        
        paymentsTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        
        // Hide ID column
        paymentsTable.getColumnModel().getColumn(0).setMinWidth(0);
        paymentsTable.getColumnModel().getColumn(0).setMaxWidth(0);
        paymentsTable.getColumnModel().getColumn(0).setWidth(0);
        
        JScrollPane scrollPane = new JScrollPane(paymentsTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(189, 195, 199), 1),
            new EmptyBorder(0, 0, 0, 0)
        ));
        
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Status bar
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(UIFactory.CARD_BG);
        statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel statusLabel = new JLabel("💡 Click status to see details | All payment transactions are recorded here");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        statusLabel.setForeground(UIFactory.TEXT_SECONDARY);
        statusPanel.add(statusLabel);
        
        contentPanel.add(statusPanel, BorderLayout.SOUTH);
        
        add(contentPanel, BorderLayout.SOUTH);
        
        // Add button listeners
        addButton.addActionListener(e -> showAddPaymentDialog());
        editButton.addActionListener(e -> showEditPaymentDialog());
        deleteButton.addActionListener(e -> deleteSelectedPayment());
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
     * Refresh payment data from database
     */
    public void refreshData() {
        try {
            List<Payment> payments = paymentDAO.findAll();
            tableModel.setRowCount(0);
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
            
            int completedCount = 0;
            int pendingCount = 0;
            double totalRevenue = 0;
            
            for (Payment payment : payments) {
                String reservationInfo = getReservationInfo(payment.getReservationId());
                
                Object[] rowData = {
                    payment.getId(),
                    reservationInfo,
                    String.format("$%,.2f", payment.getAmount()),
                    payment.getPaymentMethod(),
                    payment.getPaymentStatusCode(),
                    payment.getPaymentDate() != null ? payment.getPaymentDate().format(dateFormatter) : "",
                    payment.getTransactionId()
                };
                tableModel.addRow(rowData);
                
                // Count stats
                if ("COMPLETED".equals(payment.getPaymentStatusCode())) {
                    completedCount++;
                    totalRevenue += payment.getAmount();
                } else if ("PENDING".equals(payment.getPaymentStatusCode())) {
                    pendingCount++;
                }
            }
            
            // Update stat cards
            totalPaymentsLabel.setText(String.valueOf(payments.size()));
            completedPaymentsLabel.setText(String.valueOf(completedCount));
            pendingPaymentsLabel.setText(String.valueOf(pendingCount));
            totalRevenueLabel.setText(String.format("$%,.2f", totalRevenue));
            
            Logger.info("Payments data refreshed: " + payments.size() + " payments loaded");
        } catch (Exception e) {
            Logger.error("Error loading payments", e);
            JOptionPane.showMessageDialog(this, 
                "Error loading payments: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String getReservationInfo(int reservationId) {
        try {
            Reservation res = reservationDAO.findById(reservationId);
            return res != null ? "Res #" + res.getId() : "Unknown";
        } catch (Exception e) {
            return "Unknown";
        }
    }
    
    private void showAddPaymentDialog() {
        JPanel panel = new JPanel(new java.awt.GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        javax.swing.JComboBox<Reservation> reservationCombo = new javax.swing.JComboBox<>();
        javax.swing.JTextField amountField = new javax.swing.JTextField();
        javax.swing.JComboBox<String> methodCombo = new javax.swing.JComboBox<>(new String[]{"CASH", "CREDIT_CARD", "DEBIT_CARD", "BANK_TRANSFER", "ONLINE"});
        javax.swing.JComboBox<String> statusCombo = new javax.swing.JComboBox<>(new String[]{"PENDING", "COMPLETED", "FAILED", "REFUNDED"});
        javax.swing.JTextField transactionField = new javax.swing.JTextField();
        
        try {
            List<Reservation> reservations = reservationDAO.findAll();
            for (Reservation r : reservations) {
                reservationCombo.addItem(r);
            }
        } catch (Exception e) {
            Logger.error("Error loading reservations", e);
        }
        
        panel.add(new JLabel("Reservation:"));
        panel.add(reservationCombo);
        panel.add(new JLabel("Amount:"));
        panel.add(amountField);
        panel.add(new JLabel("Payment Method:"));
        panel.add(methodCombo);
        panel.add(new JLabel("Status:"));
        panel.add(statusCombo);
        panel.add(new JLabel("Transaction ID:"));
        panel.add(transactionField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Record Payment", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                Reservation selectedReservation = (Reservation) reservationCombo.getSelectedItem();
                
                if (selectedReservation == null) {
                    JOptionPane.showMessageDialog(this, "Please select a reservation",
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                Payment payment = new Payment();
                payment.setReservationId(selectedReservation.getId());
                payment.setAmount(Double.parseDouble(amountField.getText()));
                payment.setPaymentMethod((String) methodCombo.getSelectedItem());
                payment.setPaymentStatusCode((String) statusCombo.getSelectedItem());
                payment.setTransactionId(transactionField.getText());
                
                paymentDAO.insert(payment);
                refreshData();
                JOptionPane.showMessageDialog(this, "Payment recorded successfully!");
                Logger.info("Payment recorded: $" + amountField.getText());
            } catch (Exception e) {
                Logger.error("Error recording payment", e);
                JOptionPane.showMessageDialog(this, "Error recording payment: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void showEditPaymentDialog() {
        int selectedRow = paymentsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a payment to edit",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int paymentId = (Integer) tableModel.getValueAt(selectedRow, 0);
        
        try {
            Payment payment = paymentDAO.findById(paymentId);
            if (payment == null) {
                JOptionPane.showMessageDialog(this, "Payment not found",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            JPanel panel = new JPanel(new java.awt.GridLayout(4, 2, 10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            javax.swing.JTextField amountField = new javax.swing.JTextField(String.valueOf(payment.getAmount()));
            javax.swing.JComboBox<String> methodCombo = new javax.swing.JComboBox<>(new String[]{"CASH", "CREDIT_CARD", "DEBIT_CARD", "BANK_TRANSFER", "ONLINE"});
            javax.swing.JComboBox<String> statusCombo = new javax.swing.JComboBox<>(new String[]{"PENDING", "COMPLETED", "FAILED", "REFUNDED"});
            javax.swing.JTextField transactionField = new javax.swing.JTextField(payment.getTransactionId());
            
            methodCombo.setSelectedItem(payment.getPaymentMethod());
            statusCombo.setSelectedItem(payment.getPaymentStatusCode());
            
            panel.add(new JLabel("Amount:"));
            panel.add(amountField);
            panel.add(new JLabel("Payment Method:"));
            panel.add(methodCombo);
            panel.add(new JLabel("Status:"));
            panel.add(statusCombo);
            panel.add(new JLabel("Transaction ID:"));
            panel.add(transactionField);
            
            int result = JOptionPane.showConfirmDialog(this, panel, "Edit Payment", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
            if (result == JOptionPane.OK_OPTION) {
                payment.setAmount(Double.parseDouble(amountField.getText()));
                payment.setPaymentMethod((String) methodCombo.getSelectedItem());
                payment.setPaymentStatusCode((String) statusCombo.getSelectedItem());
                payment.setTransactionId(transactionField.getText());
                
                paymentDAO.update(payment);
                refreshData();
                JOptionPane.showMessageDialog(this, "Payment updated successfully!");
                Logger.info("Payment updated: ID " + paymentId);
            }
        } catch (Exception e) {
            Logger.error("Error editing payment", e);
            JOptionPane.showMessageDialog(this, "Error editing payment: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteSelectedPayment() {
        int selectedRow = paymentsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a payment to delete",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this payment?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int paymentId = (Integer) tableModel.getValueAt(selectedRow, 0);
                paymentDAO.delete(paymentId);
                refreshData();
                JOptionPane.showMessageDialog(this, "Payment deleted successfully!");
                Logger.info("Payment deleted: ID " + paymentId);
            } catch (Exception e) {
                Logger.error("Error deleting payment", e);
                JOptionPane.showMessageDialog(this, "Error deleting payment: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

