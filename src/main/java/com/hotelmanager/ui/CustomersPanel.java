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
import com.hotelmanager.dao.CustomerDAO;
import com.hotelmanager.model.Customer;
import com.hotelmanager.util.Logger;

/**
 * Customers Panel - displays customer management interface with modern styling
 */
public class CustomersPanel extends JPanel {
    
    private final HotelManagerApp mainApp;
    private final CustomerDAO customerDAO;
    private JTable customersTable;
    private DefaultTableModel tableModel;
    
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    
    // Stat card label
    private JLabel totalCustomersLabel;
    
    // Color palette
    private static final Color CUSTOMERS_COLOR = new Color(155, 89, 182);  // Purple
    
    public CustomersPanel(HotelManagerApp mainApp) {
        this.mainApp = mainApp;
        this.customerDAO = new CustomerDAO();
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
        titlePanel.add(UIFactory.createTitleLabel("Customers Management"));
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(UIFactory.createSubtitleLabel("Manage your hotel guests and their information"));
        
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
        
        // Stats card panel - Customer overview
        JPanel statsPanel = new JPanel(new GridLayout(1, 1, 15, 15));
        statsPanel.setBackground(UIFactory.BG_COLOR);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));
        
        totalCustomersLabel = createStatCard("👥", "Total Customers", "0", CUSTOMERS_COLOR, "Registered guests in system");
        
        statsPanel.add(totalCustomersLabel);
        
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
        
        addButton = UIFactory.createPrimaryButton("+ Add Customer");
        editButton = UIFactory.createSecondaryButton("✏️ Edit");
        deleteButton = UIFactory.createDangerButton("🗑️ Delete");
        refreshButton = UIFactory.createSecondaryButton("🔄 Refresh");
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        
        contentPanel.add(buttonPanel, BorderLayout.NORTH);
        
        // Table model
        String[] columnNames = {"ID", "First Name", "Last Name", "Email", "Phone", "ID Type", "ID Number"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        customersTable = new JTable(tableModel);
        customersTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        customersTable.setRowHeight(40);
        
        // Style the table header
        customersTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        customersTable.getTableHeader().setBackground(UIFactory.PRIMARY_COLOR);
        customersTable.getTableHeader().setForeground(Color.WHITE);
        customersTable.getTableHeader().setOpaque(true);
        
        // Alternate row colors
        customersTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
                }
                // Highlight email column
                if (column == 3 && value != null) {
                    c.setForeground(new Color(52, 152, 219));
                }
                return c;
            }
        });
        
        customersTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        
        // Hide ID column
        customersTable.getColumnModel().getColumn(0).setMinWidth(0);
        customersTable.getColumnModel().getColumn(0).setMaxWidth(0);
        customersTable.getColumnModel().getColumn(0).setWidth(0);
        
        JScrollPane scrollPane = new JScrollPane(customersTable);
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
        
        JLabel statusLabel = new JLabel("💡 Double-click a row to edit | Manage all registered guests here");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        statusLabel.setForeground(UIFactory.TEXT_SECONDARY);
        statusPanel.add(statusLabel);
        
        contentPanel.add(statusPanel, BorderLayout.SOUTH);
        
        add(contentPanel, BorderLayout.SOUTH);
        
        // Add button listeners
        addButton.addActionListener(e -> showAddCustomerDialog());
        editButton.addActionListener(e -> showEditCustomerDialog());
        deleteButton.addActionListener(e -> deleteSelectedCustomer());
        refreshButton.addActionListener(e -> refreshData());
        
        // Double-click to edit
        customersTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    showEditCustomerDialog();
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
        colorStrip.setMaximumSize(new Dimension(200, 4));
        
        JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(accentColor);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel labelText = new JLabel(label, SwingConstants.CENTER);
        labelText.setFont(new Font("Segoe UI", Font.BOLD, 14));
        labelText.setForeground(UIFactory.TEXT_COLOR);
        labelText.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel descLabel = new JLabel(description, SwingConstants.CENTER);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
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
            new EmptyBorder(10, 20, 10, 20)
        ));
        
        return valueLabel;
    }
    
    /**
     * Refresh customer data from database
     */
    public void refreshData() {
        try {
            List<Customer> customers = customerDAO.findAll();
            tableModel.setRowCount(0);
            
            for (Customer customer : customers) {
                Object[] rowData = {
                    customer.getId(),
                    customer.getFirstName(),
                    customer.getLastName(),
                    customer.getEmail(),
                    customer.getPhone(),
                    customer.getIdType(),
                    customer.getIdNumber()
                };
                tableModel.addRow(rowData);
            }
            
            // Update stat card
            totalCustomersLabel.setText(String.valueOf(customers.size()));
            
            Logger.info("Customers data refreshed: " + customers.size() + " customers loaded");
        } catch (Exception e) {
            Logger.error("Error loading customers", e);
            JOptionPane.showMessageDialog(this, 
                "Error loading customers: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showAddCustomerDialog() {
        JPanel panel = new JPanel(new java.awt.GridLayout(7, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        javax.swing.JTextField firstNameField = new javax.swing.JTextField();
        javax.swing.JTextField lastNameField = new javax.swing.JTextField();
        javax.swing.JTextField emailField = new javax.swing.JTextField();
        javax.swing.JTextField phoneField = new javax.swing.JTextField();
        javax.swing.JTextField idTypeField = new javax.swing.JTextField();
        javax.swing.JTextField idNumberField = new javax.swing.JTextField();
        javax.swing.JTextField addressField = new javax.swing.JTextField();
        
        panel.add(new JLabel("First Name:"));
        panel.add(firstNameField);
        panel.add(new JLabel("Last Name:"));
        panel.add(lastNameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);
        panel.add(new JLabel("ID Type:"));
        panel.add(idTypeField);
        panel.add(new JLabel("ID Number:"));
        panel.add(idNumberField);
        panel.add(new JLabel("Address:"));
        panel.add(addressField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Customer", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                Customer customer = new Customer();
                customer.setFirstName(firstNameField.getText());
                customer.setLastName(lastNameField.getText());
                customer.setEmail(emailField.getText());
                customer.setPhone(phoneField.getText());
                customer.setIdType(idTypeField.getText());
                customer.setIdNumber(idNumberField.getText());
                customer.setAddress(addressField.getText());
                
                customerDAO.insert(customer);
                refreshData();
                JOptionPane.showMessageDialog(this, "Customer added successfully!");
                Logger.info("Customer added: " + customer.getFirstName() + " " + customer.getLastName());
            } catch (Exception e) {
                Logger.error("Error adding customer", e);
                JOptionPane.showMessageDialog(this, "Error adding customer: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void showEditCustomerDialog() {
        int selectedRow = customersTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a customer to edit",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int customerId = (Integer) tableModel.getValueAt(selectedRow, 0);
        
        try {
            Customer customer = customerDAO.findById(customerId);
            if (customer == null) {
                JOptionPane.showMessageDialog(this, "Customer not found",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            JPanel panel = new JPanel(new java.awt.GridLayout(7, 2, 10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            javax.swing.JTextField firstNameField = new javax.swing.JTextField(customer.getFirstName());
            javax.swing.JTextField lastNameField = new javax.swing.JTextField(customer.getLastName());
            javax.swing.JTextField emailField = new javax.swing.JTextField(customer.getEmail());
            javax.swing.JTextField phoneField = new javax.swing.JTextField(customer.getPhone());
            javax.swing.JTextField idTypeField = new javax.swing.JTextField(customer.getIdType());
            javax.swing.JTextField idNumberField = new javax.swing.JTextField(customer.getIdNumber());
            javax.swing.JTextField addressField = new javax.swing.JTextField(customer.getAddress());
            
            panel.add(new JLabel("First Name:"));
            panel.add(firstNameField);
            panel.add(new JLabel("Last Name:"));
            panel.add(lastNameField);
            panel.add(new JLabel("Email:"));
            panel.add(emailField);
            panel.add(new JLabel("Phone:"));
            panel.add(phoneField);
            panel.add(new JLabel("ID Type:"));
            panel.add(idTypeField);
            panel.add(new JLabel("ID Number:"));
            panel.add(idNumberField);
            panel.add(new JLabel("Address:"));
            panel.add(addressField);
            
            int result = JOptionPane.showConfirmDialog(this, panel, "Edit Customer", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
            if (result == JOptionPane.OK_OPTION) {
                customer.setFirstName(firstNameField.getText());
                customer.setLastName(lastNameField.getText());
                customer.setEmail(emailField.getText());
                customer.setPhone(phoneField.getText());
                customer.setIdType(idTypeField.getText());
                customer.setIdNumber(idNumberField.getText());
                customer.setAddress(addressField.getText());
                
                customerDAO.update(customer);
                refreshData();
                JOptionPane.showMessageDialog(this, "Customer updated successfully!");
                Logger.info("Customer updated: " + customer.getFirstName() + " " + customer.getLastName());
            }
        } catch (Exception e) {
            Logger.error("Error editing customer", e);
            JOptionPane.showMessageDialog(this, "Error editing customer: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteSelectedCustomer() {
        int selectedRow = customersTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a customer to delete",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this customer?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int customerId = (Integer) tableModel.getValueAt(selectedRow, 0);
                customerDAO.delete(customerId);
                refreshData();
                JOptionPane.showMessageDialog(this, "Customer deleted successfully!");
                Logger.info("Customer deleted: ID " + customerId);
            } catch (Exception e) {
                Logger.error("Error deleting customer", e);
                JOptionPane.showMessageDialog(this, "Error deleting customer: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

