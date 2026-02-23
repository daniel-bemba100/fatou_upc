package com.hotelmanager;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.formdev.flatlaf.FlatLightLaf;
import com.hotelmanager.dao.AuthUserDAO;
import com.hotelmanager.model.User;
import com.hotelmanager.ui.CustomersPanel;
import com.hotelmanager.ui.DashboardPanel;
import com.hotelmanager.ui.LoginPanel;
import com.hotelmanager.ui.PaymentsPanel;
import com.hotelmanager.ui.RegistrationPanel;
import com.hotelmanager.ui.ReportsPanel;
import com.hotelmanager.ui.ReservationsPanel;
import com.hotelmanager.ui.RoomsPanel;
import com.hotelmanager.ui.SettingsPanel;
import com.hotelmanager.ui.UIFactory;
import com.hotelmanager.util.AuthDBConnection;
import com.hotelmanager.util.DBConnection;
import com.hotelmanager.util.Logger;

/**
 * Main Swing Application for Hotel Manager Pro
 */
public class HotelManagerApp extends JFrame {
    
    private static HotelManagerApp instance;
    private static boolean instanceRunning = false;
    private static FileLock appLock = null;
    private static RandomAccessFile lockFile = null;
    private static FileChannel lockChannel = null;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JPanel sidebarPanel;
    
    // Panels
    private LoginPanel loginPanel;
    private RegistrationPanel registrationPanel;
    private DashboardPanel dashboardPanel;
    private RoomsPanel roomsPanel;
    private ReservationsPanel reservationsPanel;
    private CustomersPanel customersPanel;
    private PaymentsPanel paymentsPanel;
    private ReportsPanel reportsPanel;
    private SettingsPanel settingsPanel;
    
    private User currentUser;
    private static final String LOGIN_CARD = "LOGIN";
    private static final String REGISTER_CARD = "REGISTER";
    private static final String MAIN_CARD = "MAIN";
    
    public HotelManagerApp() {
        instance = this;
        instanceRunning = true;
        initializeDatabase();
        initializeUI();
    }
    
    private void initializeDatabase() {
        try {
            AuthDBConnection.initialize();
            AuthDBConnection.getInstance().initializeDatabase();
            DBConnection.initialize();
            DBConnection.getInstance().initializeDatabase();
            Logger.info("Database connections initialized - Auth (SQLite) and Business (MariaDB)");
        } catch (Exception e) {
            Logger.error("Failed to initialize database", e);
            JOptionPane.showMessageDialog(null, 
                "Failed to initialize database: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void initializeUI() {
        setTitle("Hotel Manager Pro");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1200, 800);
        setMinimumSize(new Dimension(800, 700));
        setLocationRelativeTo(null);
        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        loadApplicationIcon();
        
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        loginPanel = new LoginPanel(this);
        registrationPanel = new RegistrationPanel(this);
        
        mainPanel.add(loginPanel, LOGIN_CARD);
        mainPanel.add(registrationPanel, REGISTER_CARD);
        
        checkAdminAndShowLogin();
        
        add(mainPanel);
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                shutdown();
            }
        });
    }
    
    private void checkAdminAndShowLogin() {
        try {
            AuthUserDAO authUserDAO = new AuthUserDAO();
            if (!authUserDAO.adminExists()) {
                cardLayout.show(mainPanel, REGISTER_CARD);
            } else {
                cardLayout.show(mainPanel, LOGIN_CARD);
            }
        } catch (Exception e) {
            Logger.error("Error checking admin existence", e);
            cardLayout.show(mainPanel, LOGIN_CARD);
        }
    }
    
    public void showLogin() {
        cardLayout.show(mainPanel, LOGIN_CARD);
    }
    
    public void showRegistration() {
        cardLayout.show(mainPanel, REGISTER_CARD);
    }
    
    public void showMainApp() {
        mainPanel.removeAll();
        
        JPanel mainContainer = new JPanel(new BorderLayout());
        
        sidebarPanel = createSidebar();
        mainContainer.add(sidebarPanel, BorderLayout.WEST);
        
        JPanel contentPanel = new JPanel(new CardLayout());
        
        dashboardPanel = new DashboardPanel(this);
        roomsPanel = new RoomsPanel(this);
        reservationsPanel = new ReservationsPanel(this);
        customersPanel = new CustomersPanel(this);
        paymentsPanel = new PaymentsPanel(this);
        reportsPanel = new ReportsPanel(this);
        settingsPanel = new SettingsPanel(this);
        
        contentPanel.add(dashboardPanel, "DASHBOARD");
        contentPanel.add(roomsPanel, "ROOMS");
        contentPanel.add(reservationsPanel, "RESERVATIONS");
        contentPanel.add(customersPanel, "CUSTOMERS");
        contentPanel.add(paymentsPanel, "PAYMENTS");
        contentPanel.add(reportsPanel, "REPORTS");
        contentPanel.add(settingsPanel, "SETTINGS");
        
        CardLayout contentCardLayout = (CardLayout) contentPanel.getLayout();
        contentCardLayout.show(contentPanel, "DASHBOARD");
        
        mainContainer.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(mainContainer, MAIN_CARD);
        
        this.mainPanel.setName("MAIN_CONTENT");
        
        cardLayout.show(mainPanel, MAIN_CARD);
        
        dashboardPanel.refreshData();
    }
    
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new GridBagLayout());
        sidebar.setBackground(UIFactory.SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(250, 0));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(5, 0, 5, 0);
        
        // Logo Panel - compact
        JPanel logoPanel = new JPanel();
        logoPanel.setBackground(UIFactory.SIDEBAR_BG);
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        logoPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        JLabel logoLabel = UIFactory.createTitleLabel("Hotel Manager");
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = UIFactory.createSubtitleLabel("Pro");
        subtitleLabel.setForeground(new Color(52, 152, 219));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        logoPanel.add(logoLabel);
        logoPanel.add(subtitleLabel);
        
        sidebar.add(logoPanel, gbc);
        
        gbc.gridy++;
        gbc.insets = new Insets(5, 10, 5, 10);
        
        // User Panel - compact
        JPanel userPanel = new JPanel();
        userPanel.setBackground(UIFactory.SIDEBAR_BG);
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        userPanel.setBorder(new EmptyBorder(0, 20, 10, 20));
        
        JLabel userLabel = UIFactory.createLabel("Logged in as:", 11, false);
        userLabel.setForeground(UIFactory.TEXT_SECONDARY);
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel usernameLabel = UIFactory.createLabel(getCurrentUser() != null ? 
            getCurrentUser().getUsername() : "Admin", 13, true);
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        userPanel.add(userLabel);
        userPanel.add(usernameLabel);
        
        sidebar.add(userPanel, gbc);
        
        // Menu Items - compact buttons
        String[][] menuItems = {
            {"Dashboard", "🏠"},
            {"Rooms", "🛏️"},
            {"Reservations", "📅"},
            {"Customers", "👥"},
            {"Payments", "💰"},
            {"Reports", "📊"},
            {"Settings", "⚙️"},
            {"Help", "❓"}
        };
        
        gbc.gridy++;
        gbc.insets = new Insets(2, 0, 2, 0);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        
        JPanel navPanel = new JPanel(new GridBagLayout());
        navPanel.setBackground(UIFactory.SIDEBAR_BG);
        
        GridBagConstraints navGbc = new GridBagConstraints();
        navGbc.gridx = 0;
        navGbc.gridy = 0;
        navGbc.fill = GridBagConstraints.HORIZONTAL;
        navGbc.insets = new Insets(1, 10, 1, 10);
        navGbc.weightx = 1.0;
        
        for (String[] item : menuItems) {
            JButton navButton = createCompactNavButton(item[0], item[1]);
            navPanel.add(navButton, navGbc);
            navGbc.gridy++;
        }
        
        sidebar.add(navPanel, gbc);
        
        // Bottom buttons
        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        gbc.insets = new Insets(10, 10, 5, 10);
        
        JButton exitButton = UIFactory.createSidebarExitButton("Exit");
        exitButton.setMaximumSize(new Dimension(200, 40));
        exitButton.setPreferredSize(new Dimension(200, 40));
        exitButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to exit?",
                "Confirm Exit",
                JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                shutdown();
            }
        });
        
        sidebar.add(exitButton, gbc);
        
        gbc.gridy++;
        gbc.insets = new Insets(5, 10, 15, 10);
        
        JButton logoutButton = UIFactory.createSidebarLogoutButton("Logout");
        logoutButton.setMaximumSize(new Dimension(200, 40));
        logoutButton.setPreferredSize(new Dimension(200, 40));
        logoutButton.addActionListener(e -> logout());
        
        sidebar.add(logoutButton, gbc);
        
        return sidebar;
    }
    
    private JButton createCompactNavButton(String text, String icon) {
        JButton button = new JButton(icon + "  " + text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0, 0, 0, 0));
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setOpaque(false);
        button.setMaximumSize(new Dimension(250, 40));
        button.setPreferredSize(new Dimension(230, 40));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(UIFactory.SIDEBAR_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(0, 0, 0, 0));
            }
        });
        
        button.addActionListener(e -> navigateTo(text));
        
        return button;
    }
    
    private void navigateTo(String section) {
        Component[] components = mainPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                JPanel mainContainer = (JPanel) comp;
                if (mainContainer.getLayout() instanceof BorderLayout) {
                    Component centerComp = ((BorderLayout) mainContainer.getLayout())
                        .getLayoutComponent(BorderLayout.CENTER);
                    if (centerComp != null && centerComp instanceof JPanel) {
                        JPanel contentPanel = (JPanel) centerComp;
                        CardLayout cl = (CardLayout) contentPanel.getLayout();
                        
                        switch (section) {
                            case "Dashboard":
                                cl.show(contentPanel, "DASHBOARD");
                                dashboardPanel.refreshData();
                                break;
                            case "Rooms":
                                cl.show(contentPanel, "ROOMS");
                                roomsPanel.refreshData();
                                break;
                            case "Reservations":
                                cl.show(contentPanel, "RESERVATIONS");
                                reservationsPanel.refreshData();
                                break;
                            case "Customers":
                                cl.show(contentPanel, "CUSTOMERS");
                                customersPanel.refreshData();
                                break;
                            case "Payments":
                                cl.show(contentPanel, "PAYMENTS");
                                paymentsPanel.refreshData();
                                break;
                            case "Reports":
                                cl.show(contentPanel, "REPORTS");
                                reportsPanel.refreshData();
                                break;
                            case "Settings":
                                cl.show(contentPanel, "SETTINGS");
                                settingsPanel.refreshData();
                                break;
                            case "Help":
                                showHelpDialog();
                                break;
                        }
                    }
                }
            }
        }
    }
    
    private void shutdown() {
        User current = getCurrentUser();
        if (current != null) {
            Logger.info("User logged out: " + current.getUsername());
        }
        
        if (loginPanel != null) {
            loginPanel.stopAnimation();
        }
        
        if (registrationPanel != null) {
            registrationPanel.stopAnimation();
        }
        
        DBConnection.getInstance().closeConnection();
        AuthDBConnection.getInstance().closeConnection();
        Logger.info("Application closing - resources cleaned up");
        
        instanceRunning = false;
        releaseLock();
        
        dispose();
        System.exit(0);
    }
    
    public void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            User current = getCurrentUser();
            if (current != null) {
                Logger.info("User logged out: " + current.getUsername());
            }
            currentUser = null;
            
            mainPanel.removeAll();
            mainPanel.add(loginPanel, LOGIN_CARD);
            mainPanel.add(registrationPanel, REGISTER_CARD);
            cardLayout.show(mainPanel, LOGIN_CARD);
            
            loginPanel.clearFields();
        }
    }
    
    public void loginSuccess(User user) {
        this.currentUser = user;
        Logger.info("User logged in: " + user.getUsername());
        showMainApp();
    }
    
    public void registrationSuccess() {
        Logger.info("Admin registered successfully");
        JOptionPane.showMessageDialog(this,
            "Registration successful! You can now login.",
            "Success",
            JOptionPane.INFORMATION_MESSAGE);
        showLogin();
    }
    
    public static User getCurrentUser() {
        return instance != null ? instance.currentUser : null;
    }
    
    private void loadApplicationIcon() {
        try {
            InputStream iconStream = getClass().getResourceAsStream("/icons/UPC_BRAND.png");
            if (iconStream != null) {
                BufferedImage image = ImageIO.read(iconStream);
                setIconImage(image);
                Logger.info("Application icon loaded successfully");
            } else {
                Logger.warn("Application icon not found in resources");
            }
        } catch (Exception e) {
            Logger.error("Failed to load application icon", e);
        }
    }
    
    private void showHelpDialog() {
        String helpText = "<html><body style='width: 400px; font-family: Segoe UI; font-size: 12px;'>"
            + "<h2 style='color: #3498db;'>Hotel Manager Pro - Help</h2>"
            + "<p><b>Welcome to Hotel Manager Pro!</b></p>"
            + "<p>This application helps you manage your hotel operations efficiently.</p>"
            + "<hr>"
            + "<p><b>Dashboard:</b> Overview of hotel statistics and key metrics.</p>"
            + "<p><b>Rooms:</b> Manage room availability, types, and status.</p>"
            + "<p><b>Reservations:</b> Handle guest bookings and check-ins.</p>"
            + "<p><b>Customers:</b> Maintain guest profiles and contact information.</p>"
            + "<p><b>Payments:</b> Process and track payments.</p>"
            + "<p><b>Reports:</b> Generate analytical reports on hotel performance.</p>"
            + "<p><b>Settings:</b> Configure application preferences.</p>"
            + "<hr>"
            + "<p><b>Tips:</b></p>"
            + "<ul>"
            + "<li>Use the sidebar to navigate between sections</li>"
            + "<li>The window can be resized but has a minimum size of 800x700</li>"
            + "<li>Click refresh buttons to update data</li>"
            + "</ul>"
            + "<p style='color: #7f8c8d; font-size: 11px;'>Version 1.0.0 | Hotel Manager Pro</p>"
            + "</body></html>";
        
        JOptionPane.showMessageDialog(this, helpText, "Help - Hotel Manager Pro", JOptionPane.INFORMATION_MESSAGE);
    }
    
/**
     * Check if another instance is already running using file lock
     */
    private static boolean isAnotherInstanceRunning() {
        try {
            String lockFilePath = System.getProperty("user.home") + "/.hotel_manager.lock";
            java.io.File lockFileObj = new java.io.File(lockFilePath);
            
            // Check if lock file exists and is older than 5 minutes (stale lock)
            if (lockFileObj.exists()) {
                long age = System.currentTimeMillis() - lockFileObj.lastModified();
                if (age > 5 * 60 * 1000) { // 5 minutes
                    // Lock file is stale, try to delete it
                    lockFileObj.delete();
                } else {
                    // Lock file is recent, try to acquire lock
                    try {
                        lockFile = new RandomAccessFile(lockFilePath, "rw");
                        lockChannel = lockFile.getChannel();
                        appLock = lockChannel.tryLock();
                        if (appLock == null) {
                            return true; // Another instance is running
                        }
                        // Successfully acquired lock, delete the stale file
                        lockFileObj.delete();
                    } catch (Exception e) {
                        // Can't acquire lock, another instance is running
                        return true;
                    }
                }
            }
            
            // Create fresh lock file
            lockFile = new RandomAccessFile(lockFilePath, "rw");
            lockChannel = lockFile.getChannel();
            appLock = lockChannel.tryLock();
            
            if (appLock == null) {
                return true;
            }
            
            return false;
        } catch (Exception e) {
            System.err.println("Could not create lock file: " + e.getMessage());
            return false; // Allow app to run if lock can't be created
        }
    }
    
    /**
     * Release the file lock when application closes
     */
    private static void releaseLock() {
        try {
            if (appLock != null) {
                appLock.release();
                appLock = null;
            }
            if (lockChannel != null) {
                lockChannel.close();
                lockChannel = null;
            }
            if (lockFile != null) {
                lockFile.close();
                lockFile = null;
            }
            // Delete the lock file
            String lockFilePath = System.getProperty("user.home") + "/.hotel_manager.lock";
            java.io.File lockFileObj = new java.io.File(lockFilePath);
            if (lockFileObj.exists()) {
                lockFileObj.delete();
            }
        } catch (Exception e) {
            System.err.println("Error releasing lock: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        // Check if an instance is already running using file lock
        if (isAnotherInstanceRunning()) {
            System.err.println("Application is already running. Exiting.");
            JOptionPane.showMessageDialog(null,
                "Hotel Manager Pro is already running.\nOnly one instance of the application is allowed.",
                "Application Running",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            FlatLightLaf.install();
        } catch (Exception e) {
            System.err.println("Failed to install FlatLaf: " + e.getMessage());
        }
        
        SwingUtilities.invokeLater(() -> {
            HotelManagerApp app = new HotelManagerApp();
            app.setVisible(true);
            
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                releaseLock();
            }));
        });
    }
}

