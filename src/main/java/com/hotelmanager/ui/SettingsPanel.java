package com.hotelmanager.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.FileOutputStream;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.hotelmanager.HotelManagerApp;
import com.hotelmanager.util.DBConnection;
import com.hotelmanager.util.Logger;

/**
 * Settings Panel - displays application settings interface with modern styling
 */
public class SettingsPanel extends JPanel {
    
    private final HotelManagerApp mainApp;
    private JLabel dbTypeLabel;
    private JLabel dbPathLabel;
    
    // Database type combo
    private JComboBox<String> dbTypeCombo;
    
    // MariaDB connection fields
    private JTextField hostField;
    private JTextField portField;
    private JTextField databaseField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    
    // Stat card labels
    private JLabel dbStatusLabel;
    private JLabel appVersionLabel;
    private JLabel connectionTypeLabel;
    
    // Colors
    private static final Color SETTINGS_COLOR = new Color(44, 62, 80);       // Dark
    private static final Color DB_COLOR = new Color(52, 152, 219);          // Blue
    private static final Color APP_COLOR = new Color(155, 89, 182);          // Purple
    
    public SettingsPanel(HotelManagerApp mainApp) {
        this.mainApp = mainApp;
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
        titlePanel.add(UIFactory.createTitleLabel("Settings"));
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(UIFactory.createSubtitleLabel("Configure application and database settings"));
        
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
        
        // Main content panel with scroll - all content including stats will scroll together
        JPanel mainContentPanel = new JPanel();
        mainContentPanel.setLayout(new BoxLayout(mainContentPanel, BoxLayout.Y_AXIS));
        mainContentPanel.setBackground(UIFactory.BG_COLOR);
        
        // Stats cards panel - 3 cards
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 15, 15));
        statsPanel.setBackground(UIFactory.BG_COLOR);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));
        
        dbStatusLabel = createStatCard("🗄️", "Database", "SQLite", DB_COLOR, "Current database type");
        appVersionLabel = createStatCard("📱", "Version", "1.0.0", APP_COLOR, "Application version");
        connectionTypeLabel = createStatCard("🔗", "Connection", "Local", SETTINGS_COLOR, "Connection status");
        
        statsPanel.add(dbStatusLabel);
        statsPanel.add(appVersionLabel);
        statsPanel.add(connectionTypeLabel);
        
        mainContentPanel.add(statsPanel);
        
// Content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(UIFactory.BG_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 20, 30));

        // Settings container - directly add to content panel without nested scroll
        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        settingsPanel.setBackground(UIFactory.BG_COLOR);
        
        // Database Settings Section
        JPanel dbSection = createSection("⚙️ Database Settings");
        
        // Database Type
        JPanel typePanel = new JPanel(new GridBagLayout());
        typePanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel typeLabel = new JLabel("Database Type:");
        typeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        typeLabel.setForeground(UIFactory.TEXT_COLOR);
        typePanel.add(typeLabel, gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        String[] dbTypes = {"sqlite", "mariadb"};
        dbTypeCombo = new JComboBox<>(dbTypes);
        dbTypeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dbTypeCombo.setPreferredSize(new Dimension(180, 35));
        dbTypeCombo.addActionListener(e -> toggleMariaDBFields());
        typePanel.add(dbTypeCombo, gbc);
        
        dbSection.add(typePanel);
        
        // SQLite Info
        JPanel sqliteInfo = new JPanel();
        sqliteInfo.setBackground(Color.WHITE);
        sqliteInfo.setLayout(new BoxLayout(sqliteInfo, BoxLayout.Y_AXIS));
        JLabel sqliteLabel = new JLabel("💡 SQLite: Used for user login/registration (local storage)");
        sqliteLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        sqliteLabel.setForeground(UIFactory.TEXT_SECONDARY);
        sqliteInfo.add(sqliteLabel);
        dbSection.add(sqliteInfo);
        
        dbSection.add(Box.createVerticalStrut(15));
        
        // MariaDB Connection Panel
        JPanel mariaPanel = new JPanel(new GridBagLayout());
        mariaPanel.setBackground(Color.WHITE);
        mariaPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(189, 195, 199), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        // Section title
        JLabel mariaTitle = new JLabel("🔌 MariaDB Connection");
        mariaTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        mariaTitle.setForeground(DB_COLOR);
        GridBagConstraints titleGbc = new GridBagConstraints();
        titleGbc.gridx = 0;
        titleGbc.gridy = 0;
        titleGbc.gridwidth = 2;
        titleGbc.anchor = GridBagConstraints.WEST;
        titleGbc.insets = new Insets(0, 0, 15, 0);
        mariaPanel.add(mariaTitle, titleGbc);
        
        GridBagConstraints mgbc = new GridBagConstraints();
        mgbc.insets = new Insets(8, 10, 8, 10);
        mgbc.anchor = GridBagConstraints.WEST;
        
        // Host
        mgbc.gridx = 0;
        mgbc.gridy = 1;
        mgbc.weightx = 0;
        JLabel hostLabel = new JLabel("Host:");
        hostLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        hostLabel.setForeground(UIFactory.TEXT_COLOR);
        mariaPanel.add(hostLabel, mgbc);
        
        mgbc.gridx = 1;
        mgbc.fill = GridBagConstraints.HORIZONTAL;
        mgbc.weightx = 1.0;
        hostField = new JTextField("localhost", 15);
        hostField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        hostField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(189, 195, 199), 1),
            new EmptyBorder(8, 10, 8, 10)
        ));
        mariaPanel.add(hostField, mgbc);
        
        // Port
        mgbc.gridx = 0;
        mgbc.gridy = 2;
        mgbc.weightx = 0;
        mgbc.fill = GridBagConstraints.NONE;
        JLabel portLabel = new JLabel("Port:");
        portLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        portLabel.setForeground(UIFactory.TEXT_COLOR);
        mariaPanel.add(portLabel, mgbc);
        
        mgbc.gridx = 1;
        mgbc.fill = GridBagConstraints.HORIZONTAL;
        mgbc.weightx = 1.0;
        portField = new JTextField("3306", 15);
        portField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        portField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(189, 195, 199), 1),
            new EmptyBorder(8, 10, 8, 10)
        ));
        mariaPanel.add(portField, mgbc);
        
        // Database
        mgbc.gridx = 0;
        mgbc.gridy = 3;
        mgbc.weightx = 0;
        mgbc.fill = GridBagConstraints.NONE;
        JLabel dbLabel = new JLabel("Database:");
        dbLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dbLabel.setForeground(UIFactory.TEXT_COLOR);
        mariaPanel.add(dbLabel, mgbc);
        
        mgbc.gridx = 1;
        mgbc.fill = GridBagConstraints.HORIZONTAL;
        mgbc.weightx = 1.0;
        databaseField = new JTextField("hotel_manager_db", 15);
        databaseField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        databaseField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(189, 195, 199), 1),
            new EmptyBorder(8, 10, 8, 10)
        ));
        mariaPanel.add(databaseField, mgbc);
        
        // Username
        mgbc.gridx = 0;
        mgbc.gridy = 4;
        mgbc.weightx = 0;
        mgbc.fill = GridBagConstraints.NONE;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        userLabel.setForeground(UIFactory.TEXT_COLOR);
        mariaPanel.add(userLabel, mgbc);
        
        mgbc.gridx = 1;
        mgbc.fill = GridBagConstraints.HORIZONTAL;
        mgbc.weightx = 1.0;
        usernameField = new JTextField("root", 15);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(189, 195, 199), 1),
            new EmptyBorder(8, 10, 8, 10)
        ));
        mariaPanel.add(usernameField, mgbc);
        
        // Password
        mgbc.gridx = 0;
        mgbc.gridy = 5;
        mgbc.weightx = 0;
        mgbc.fill = GridBagConstraints.NONE;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        passLabel.setForeground(UIFactory.TEXT_COLOR);
        mariaPanel.add(passLabel, mgbc);
        
        mgbc.gridx = 1;
        mgbc.fill = GridBagConstraints.HORIZONTAL;
        mgbc.weightx = 1.0;
        passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(189, 195, 199), 1),
            new EmptyBorder(8, 10, 8, 10)
        ));
        mariaPanel.add(passwordField, mgbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton testBtn = UIFactory.createSecondaryButton("🔗 Test Connection");
        testBtn.setPreferredSize(new Dimension(160, 40));
        testBtn.addActionListener(this::testMariaDBConnection);
        
        JButton saveBtn = UIFactory.createPrimaryButton("💾 Save & Apply");
        saveBtn.setPreferredSize(new Dimension(160, 40));
        saveBtn.addActionListener(this::saveMariaDBSettings);
        
        buttonPanel.add(testBtn);
        buttonPanel.add(saveBtn);
        
        mgbc.gridx = 0;
        mgbc.gridy = 6;
        mgbc.gridwidth = 2;
        mgbc.anchor = GridBagConstraints.CENTER;
        mariaPanel.add(buttonPanel, mgbc);
        
        dbSection.add(mariaPanel);
        
        // Current Status
        dbTypeLabel = UIFactory.createLabel("Database Type: ", 13, false);
        dbPathLabel = UIFactory.createLabel("Connection: ", 13, false);
        
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        statusPanel.setBackground(Color.WHITE);
        
        JLabel statusTitle = new JLabel("📊 Current Status");
        statusTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        statusTitle.setForeground(UIFactory.TEXT_COLOR);
        
        statusPanel.add(statusTitle);
        statusPanel.add(Box.createVerticalStrut(8));
        statusPanel.add(dbTypeLabel);
        statusPanel.add(Box.createVerticalStrut(5));
        statusPanel.add(dbPathLabel);
        
        dbSection.add(Box.createVerticalStrut(15));
        dbSection.add(statusPanel);
        
        settingsPanel.add(dbSection);
        
        // Application Settings
        JPanel appSection = createSection("ℹ️ Application Info");
        
        JPanel appInfo = new JPanel();
        appInfo.setLayout(new BoxLayout(appInfo, BoxLayout.Y_AXIS));
        appInfo.setBackground(Color.WHITE);
        
        JLabel hotelName = new JLabel("🏨 Hotel Name: Hotel Manager Pro");
        hotelName.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        hotelName.setForeground(UIFactory.TEXT_COLOR);
        
        JLabel version = new JLabel("📋 Version: 1.0.0");
        version.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        version.setForeground(UIFactory.TEXT_COLOR);
        
        JLabel developer = new JLabel("👨‍💻 Developed by: Daniel Bemba");
        developer.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        developer.setForeground(UIFactory.TEXT_COLOR);
        
        appInfo.add(hotelName);
        appInfo.add(Box.createVerticalStrut(8));
        appInfo.add(version);
        appInfo.add(Box.createVerticalStrut(8));
        appInfo.add(developer);
        
        appSection.add(appInfo);
        
        settingsPanel.add(Box.createVerticalStrut(20));
        settingsPanel.add(appSection);
        
        JScrollPane scrollPane = new JScrollPane(settingsPanel);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        // Style the scrollbar
        scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(189, 195, 199);
                this.trackColor = new Color(245, 245, 245);
            }
            
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }
            
            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }
            
            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                return button;
            }
        });
scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        
        // Add settings panel to content panel
contentPanel.add(scrollPane);
        
        // Add everything to main content panel
        mainContentPanel.add(contentPanel);
        
        // Create outer scroll pane for everything
        JScrollPane mainScrollPane = new JScrollPane(mainContentPanel);
        mainScrollPane.setBorder(null);
        mainScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        // Style the scrollbar
        mainScrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(189, 195, 199);
                this.trackColor = new Color(245, 245, 245);
            }
            
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }
            
            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }
            
            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                return button;
            }
        });
        mainScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        
        add(headerPanel, BorderLayout.NORTH);
        add(mainScrollPane, BorderLayout.CENTER);
        
        // Load current settings
        loadCurrentSettings();
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
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
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
    
    private JPanel createSection(String title) {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(Color.WHITE);
        
        // Section title with icon
        JLabel sectionTitle = new JLabel(title);
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        sectionTitle.setForeground(UIFactory.TEXT_COLOR);
        sectionTitle.setBorder(new EmptyBorder(15, 15, 10, 15));
        
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.add(sectionTitle);
        
        section.add(titlePanel);
        section.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(189, 195, 199), 1),
            new EmptyBorder(0, 0, 15, 0)
        ));
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, 500));
        return section;
    }
    
    private void toggleMariaDBFields() {
        boolean isMariaDB = "mariadb".equalsIgnoreCase((String) dbTypeCombo.getSelectedItem());
        hostField.setEnabled(isMariaDB);
        portField.setEnabled(isMariaDB);
        databaseField.setEnabled(isMariaDB);
        usernameField.setEnabled(isMariaDB);
        passwordField.setEnabled(isMariaDB);
    }
    
    private void loadCurrentSettings() {
        try {
            DBConnection dbConnection = DBConnection.getInstance();
            String dbType = dbConnection.getDbType();
            dbTypeCombo.setSelectedItem(dbType);
            
            // Update stat card
            dbStatusLabel.setText(dbType.toUpperCase());
            
            // Load MariaDB settings from config
            Properties props = new Properties();
            try {
                java.io.FileInputStream fis = new java.io.FileInputStream("config.properties");
                props.load(fis);
                fis.close();
                
                String url = props.getProperty("db.url", "");
                if (url.contains("://")) {
                    String[] parts = url.replace("jdbc:mariadb://", "").split("/");
                    String hostPort = parts[0];
                    String[] hp = hostPort.split(":");
                    hostField.setText(hp[0]);
                    if (hp.length > 1) {
                        portField.setText(hp[1]);
                    }
                    if (parts.length > 1) {
                        String databaseName = parts[1].split("\\?")[0];
                        databaseField.setText(databaseName);
                    }
                }
                usernameField.setText(props.getProperty("db.username", "root"));
                passwordField.setText(props.getProperty("db.password", ""));
            } catch (Exception e) {
                Logger.warn("Could not load config.properties: " + e.getMessage());
            }
            
            toggleMariaDBFields();
        } catch (Exception e) {
            Logger.error("Error loading settings", e);
        }
    }
    
    private void testMariaDBConnection(ActionEvent e) {
        String host = hostField.getText().trim();
        String port = portField.getText().trim();
        String database = databaseField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (host.isEmpty() || database.isEmpty() || username.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please fill in all required fields (Host, Database, Username)",
                "Missing Information",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JButton btn = (JButton) e.getSource();
        btn.setEnabled(false);
        btn.setText("Testing...");
        
        new Thread(() -> {
            boolean success = DBConnection.getInstance().testConnection(
                host, port, database, username, password, "mariadb"
            );
            
            javax.swing.SwingUtilities.invokeLater(() -> {
                btn.setEnabled(true);
                btn.setText("🔗 Test Connection");
                
                if (success) {
                    JOptionPane.showMessageDialog(SettingsPanel.this,
                        "✅ Connection successful!\n\nConnected to MariaDB at " + host + ":" + port + "/" + database,
                        "Connection Test",
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(SettingsPanel.this,
                        "❌ Connection failed!\n\nPlease check your settings and ensure MariaDB is running.",
                        "Connection Test",
                        JOptionPane.ERROR_MESSAGE);
                }
            });
        }).start();
    }
    
    private void saveMariaDBSettings(ActionEvent e) {
        String selectedType = (String) dbTypeCombo.getSelectedItem();
        
        try {
            Properties props = new Properties();
            java.io.FileInputStream fis = new java.io.FileInputStream("config.properties");
            props.load(fis);
            fis.close();
            
            props.setProperty("db.type", selectedType);
            
            if ("mariadb".equalsIgnoreCase(selectedType)) {
                String host = hostField.getText().trim();
                String port = portField.getText().trim();
                String database = databaseField.getText().trim();
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword());
                
                String dbUrl = "jdbc:mariadb://" + host + ":" + port + "/" + database + "?serverTimezone=UTC";
                props.setProperty("db.url", dbUrl);
                props.setProperty("db.username", username);
                props.setProperty("db.password", password);
            }
            
            FileOutputStream fos = new FileOutputStream("config.properties");
            props.store(fos, "Hotel Manager Database Configuration");
            fos.close();
            
            // Update stat cards
            dbStatusLabel.setText(selectedType.toUpperCase());
            connectionTypeLabel.setText("MariaDB".equalsIgnoreCase(selectedType) ? "Remote" : "Local");
            
            JOptionPane.showMessageDialog(this,
                "✅ Settings saved successfully!\n\nPlease restart the application for changes to take effect.",
                "Settings Saved",
                JOptionPane.INFORMATION_MESSAGE);
            
            Logger.info("Database settings updated to: " + selectedType);
            
        } catch (Exception ex) {
            Logger.error("Error saving settings", ex);
            JOptionPane.showMessageDialog(this,
                "❌ Error saving settings: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Refresh settings data
     */
    public void refreshData() {
        try {
            DBConnection db = DBConnection.getInstance();
            String dbType = db.getDbType();
            String dbPath = "";
            
            if (db.isMariaDB()) {
                dbPath = "jdbc:mariadb://localhost:3306/hotel_manager_db (MariaDB - Full data)";
            } else {
                dbPath = "MySQL (Legacy support)";
            }
            
            dbTypeLabel.setText("Database Type: " + dbType.toUpperCase());
            dbPathLabel.setText("Connection: " + dbPath);
            
            // Update stat cards
            dbStatusLabel.setText(dbType.toUpperCase());
            connectionTypeLabel.setText(db.isMariaDB() ? "Remote" : "Local");
            
            Logger.info("Settings data refreshed");
        } catch (Exception e) {
            Logger.error("Error loading settings", e);
        }
    }
}

