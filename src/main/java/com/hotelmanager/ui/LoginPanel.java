package com.hotelmanager.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import com.hotelmanager.HotelManagerApp;
import com.hotelmanager.dao.AuthUserDAO;
import com.hotelmanager.model.User;
import com.hotelmanager.util.Logger;

/**
 * Modern Login Panel with FlatLaf styling
 */
public class LoginPanel extends JPanel {
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField visiblePasswordField;
    private JButton togglePasswordBtn;
    private JLabel errorLabel;
    private boolean passwordVisible = false;
    private BufferedImage backgroundImage;
    
    // Neon effect variables
    private float neonGlow = 0f;
    private Timer neonTimer;
    
    // Password recovery dialog reference - prevents multiple windows
    private PasswordRecoveryDialog passwordRecoveryDialog;
    
    private final HotelManagerApp mainApp;
    private final AuthUserDAO authUserDAO;
    
    public LoginPanel(HotelManagerApp mainApp) {
        this.mainApp = mainApp;
        this.authUserDAO = new AuthUserDAO();
        loadBackgroundImage();
        initializeNeonEffect();
        initializeUI();
    }
    
    private void initializeNeonEffect() {
        neonTimer = new Timer(50, e -> {
            neonGlow += 0.1f;
            if (neonGlow > 2 * Math.PI) {
                neonGlow = 0;
            }
            repaint();
        });
        neonTimer.start();
    }
    
    private void loadBackgroundImage() {
        try {
            backgroundImage = ImageIO.read(getClass().getResource("/backgrounds/upc_hotel.png"));
        } catch (IOException e) {
            Logger.warn("Could not load background image: " + e.getMessage());
            backgroundImage = null;
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (backgroundImage != null) {
            // Draw the background image scaled to fit the panel
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            
            // Add a semi-transparent dark overlay
            g2d.setColor(new Color(10, 10, 20, 180));
            g2d.fillRect(0, 0, getWidth(), getHeight());
        } else {
            // Modern dark gradient background - deep blue/purple
            java.awt.GradientPaint gp = new java.awt.GradientPaint(
                0, 0, new Color(15, 15, 35), 
                0, getHeight(), new Color(25, 25, 50)
            );
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
        
        // Add subtle geometric pattern overlay
        g2d.setColor(new Color(255, 255, 255, 3));
        for (int i = 0; i < getWidth(); i += 40) {
            for (int j = 0; j < getHeight(); j += 40) {
                g2d.fillRect(i, j, 1, 1);
            }
        }
    }
    
    private void initializeUI() {
        setLayout(new GridBagLayout());
        setBackground(new Color(30, 30, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Main container card - modern glassmorphism style
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw glassmorphism background with subtle gradient
                java.awt.GradientPaint bgGradient = new java.awt.GradientPaint(
                    0, 0, new Color(30, 30, 45, 250),
                    0, getHeight(), new Color(25, 25, 40, 250)
                );
                g2.setPaint(bgGradient);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 25, 25);
                
                // Draw subtle inner shadow
                g2.setColor(new Color(0, 0, 0, 30));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 25, 25);
                
                // Draw neon border effect with animation
                int glowIntensity = (int) (120 + 80 * Math.sin(neonGlow));
                Color neonColor = new Color(52, 180, 219, glowIntensity);
                
                // Outer glow
                g2.setStroke(new java.awt.BasicStroke(5));
                g2.setColor(new Color(52, 180, 219, glowIntensity / 5));
                g2.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 24, 24);
                
                // Middle glow
                g2.setStroke(new java.awt.BasicStroke(3));
                g2.setColor(new Color(52, 180, 219, glowIntensity / 3));
                g2.drawRoundRect(3, 3, getWidth() - 7, getHeight() - 7, 23, 23);
                
                // Inner bright border
                g2.setStroke(new java.awt.BasicStroke(1));
                g2.setColor(new Color(52, 200, 255, glowIntensity));
                g2.drawRoundRect(5, 5, getWidth() - 11, getHeight() - 11, 21, 21);
                
                // Add subtle top highlight
                g2.setStroke(new java.awt.BasicStroke(1));
                g2.setColor(new Color(255, 255, 255, 15));
                g2.drawLine(25, 2, getWidth() - 25, 2);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(40, 45, 40, 45));
        card.setPreferredSize(new Dimension(420, 520));
        
        // Logo area
        JPanel logoPanel = createLogoPanel();
        
        // Welcome text - modern gradient text effect
        JLabel welcomeLabel = new JLabel("Welcome back!") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                java.awt.GradientPaint gp = new java.awt.GradientPaint(0, 0, new Color(52, 200, 255), 0, getHeight(), new Color(41, 128, 185));
                g2.setPaint(gp);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), 0, fm.getAscent());
            }
        };
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Sign in to continue to Hotel Manager");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(new Color(160, 160, 180));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Form fields
        JPanel formPanel = createFormPanel();
        
        // Login button
        JButton loginBtn = createLoginButton();
        
        // Forgot password link
        JPanel forgotPasswordPanel = createForgotPasswordLink();
        
        // Register link
        JPanel registerPanel = createRegisterLink();
        
        // Footer - lighter for dark theme
        JLabel footerLabel = new JLabel("© 2026 Hotel Manager Pro");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footerLabel.setForeground(new Color(150, 150, 150));
        footerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Build the card
        card.add(logoPanel);
        card.add(Box.createVerticalStrut(20));
        card.add(welcomeLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(subtitleLabel);
        card.add(Box.createVerticalStrut(30));
        card.add(formPanel);
        card.add(Box.createVerticalStrut(20));
        card.add(loginBtn);
        card.add(Box.createVerticalStrut(10));
        card.add(forgotPasswordPanel);
        card.add(Box.createVerticalStrut(15));
        card.add(registerPanel);
        card.add(Box.createVerticalStrut(20));
        card.add(footerLabel);
        
        add(card, gbc);
        
        // Add Enter key listener
        passwordField.addActionListener(e -> handleLogin());
    }
    
    private JPanel createLogoPanel() {
        JPanel logoPanel = new JPanel();
        logoPanel.setOpaque(false);
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        logoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Load and display the UPC logo image
        try {
            BufferedImage logoImage = ImageIO.read(getClass().getResourceAsStream("/icons/UPC_BRAND.png"));
            if (logoImage != null) {
                // Scale the image to fit the logo area
                int maxWidth = 120;
                int maxHeight = 80;
                double scale = Math.min((double) maxWidth / logoImage.getWidth(), 
                                       (double) maxHeight / logoImage.getHeight());
                int newWidth = (int) (logoImage.getWidth() * scale);
                int newHeight = (int) (logoImage.getHeight() * scale);
                
                java.awt.Image scaledImage = logoImage.getScaledInstance(newWidth, newHeight, 
                    java.awt.Image.SCALE_SMOOTH);
                JLabel logoLabel = new JLabel(new javax.swing.ImageIcon(scaledImage));
                logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                logoPanel.add(logoLabel);
            } else {
                // Fallback to icon if image not found
                logoPanel.add(createLogoIcon());
            }
        } catch (IOException e) {
            Logger.warn("Could not load UPC logo: " + e.getMessage());
            // Fallback to icon if loading fails
            logoPanel.add(createLogoIcon());
        }
        
        return logoPanel;
    }
    
    /**
     * Creates the fallback logo icon when image cannot be loaded
     */
    private JPanel createLogoIcon() {
        // Icon with gradient background
        JPanel iconCircle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                // Gradient background
                java.awt.GradientPaint gp = new java.awt.GradientPaint(0, 0, new Color(52, 152, 219), getWidth(), getHeight(), new Color(41, 128, 185));
                g2.setPaint(gp);
                g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
            }
        };
        iconCircle.setOpaque(false);
        iconCircle.setPreferredSize(new Dimension(70, 70));
        iconCircle.setLayout(new GridBagLayout());
        
        JLabel iconLabel = new JLabel("🏨", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        iconCircle.add(iconLabel);
        
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        iconCircle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        return iconCircle;
    }
    
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.setMaximumSize(new Dimension(320, 150));
        
        // Username field
        JLabel userLabel = createFieldLabel("Username");
        usernameField = createTextField();
        usernameField.setMaximumSize(new Dimension(320, 45));
        
        // Password field with toggle
        JPanel passwordPanel = createPasswordField();
        
        // Error message
        errorLabel = new JLabel("");
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        errorLabel.setForeground(new Color(231, 76, 60));
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        errorLabel.setVisible(false);
        
        formPanel.add(userLabel);
        formPanel.add(usernameField);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(passwordPanel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(errorLabel);
        
        return formPanel;
    }
    
    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(new Color(200, 200, 200));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
    
    /**
     * Creates a modern bottom border only
     */
    private javax.swing.border.Border createBottomBorder(Color color, int thickness) {
        return new javax.swing.border.EmptyBorder(0, 0, 0, 0) {
            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(color);
                g2.setStroke(new java.awt.BasicStroke(thickness));
                g2.drawLine(0, height - thickness, width, height - thickness);
            }
            
            @Override
            public boolean isBorderOpaque() {
                return false;
            }
        };
    }
    
    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(new Color(240, 240, 240));
        field.setBackground(new Color(50, 50, 50));
        field.setCaretColor(new Color(240, 240, 240));
        // Modern bottom border only - darker for dark theme
        field.setBorder(BorderFactory.createCompoundBorder(
            createBottomBorder(new Color(80, 80, 80), 2),
            new EmptyBorder(12, 15, 12, 15)
        ));
        field.setFocusCycleRoot(true);
        
        // Focus listener for bottom border color
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    createBottomBorder(new Color(52, 152, 219), 3),
                    new EmptyBorder(12, 15, 12, 15)
                ));
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    createBottomBorder(new Color(80, 80, 80), 2),
                    new EmptyBorder(12, 15, 12, 15)
                ));
            }
        });
        
        return field;
    }
    
    private JPanel createPasswordField() {
        JPanel passwordPanel = new JPanel();
        passwordPanel.setOpaque(false);
        passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.Y_AXIS));
        passwordPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordPanel.setMaximumSize(new Dimension(320, 50));
        
        JLabel passLabel = createFieldLabel("Password");
        passwordPanel.add(passLabel);
        
        JPanel fieldPanel = new JPanel();
        fieldPanel.setOpaque(false);
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.X_AXIS));
        fieldPanel.setMaximumSize(new Dimension(320, 45));
        
        // Password field - full width minus toggle button - dark theme
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setForeground(new Color(240, 240, 240));
        passwordField.setBackground(new Color(50, 50, 50));
        passwordField.setCaretColor(new Color(240, 240, 240));
        passwordField.setEchoChar('•');
        // Modern bottom border only - darker for dark theme
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            createBottomBorder(new Color(80, 80, 80), 2),
            new EmptyBorder(12, 15, 12, 15)
        ));
        passwordField.setMaximumSize(new Dimension(280, 43));
        passwordField.setPreferredSize(new Dimension(280, 43));
        
        visiblePasswordField = new JTextField();
        visiblePasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        visiblePasswordField.setForeground(new Color(240, 240, 240));
        visiblePasswordField.setBackground(new Color(50, 50, 50));
        visiblePasswordField.setCaretColor(new Color(240, 240, 240));
        // Modern bottom border only - darker for dark theme
        visiblePasswordField.setBorder(BorderFactory.createCompoundBorder(
            createBottomBorder(new Color(80, 80, 80), 2),
            new EmptyBorder(12, 15, 12, 15)
        ));
        visiblePasswordField.setVisible(false);
        visiblePasswordField.setMaximumSize(new Dimension(280, 43));
        visiblePasswordField.setPreferredSize(new Dimension(280, 43));
        
        togglePasswordBtn = new JButton();
        togglePasswordBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        togglePasswordBtn.setText("🔓");
        togglePasswordBtn.setPreferredSize(new Dimension(50, 41));
        togglePasswordBtn.setBackground(new Color(60, 60, 60));
        togglePasswordBtn.setForeground(new Color(240, 240, 240));
        togglePasswordBtn.setBorder(BorderFactory.createCompoundBorder(
            new javax.swing.border.LineBorder(new Color(80, 80, 80), 1),
            BorderFactory.createEmptyBorder(5, 2, 5, 2)
        ));
        togglePasswordBtn.setFocusPainted(false);
        togglePasswordBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        togglePasswordBtn.setToolTipText("Show password");
        togglePasswordBtn.addActionListener(e -> togglePasswordVisibility());
        togglePasswordBtn.setRolloverEnabled(true);
        
        // Focus listener for password field - dark theme borders
        passwordField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                passwordField.setBorder(BorderFactory.createCompoundBorder(
                    createBottomBorder(new Color(52, 152, 219), 3),
                    new EmptyBorder(12, 15, 12, 15)
                ));
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                passwordField.setBorder(BorderFactory.createCompoundBorder(
                    createBottomBorder(new Color(80, 80, 80), 2),
                    new EmptyBorder(12, 15, 12, 15)
                ));
            }
        });
        
        // Focus listener for visible password field - dark theme borders
        visiblePasswordField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                visiblePasswordField.setBorder(BorderFactory.createCompoundBorder(
                    createBottomBorder(new Color(52, 152, 219), 3),
                    new EmptyBorder(12, 15, 12, 15)
                ));
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                visiblePasswordField.setBorder(BorderFactory.createCompoundBorder(
                    createBottomBorder(new Color(80, 80, 80), 2),
                    new EmptyBorder(12, 15, 12, 15)
                ));
            }
        });
        
        fieldPanel.add(passwordField);
        fieldPanel.add(visiblePasswordField);
        fieldPanel.add(togglePasswordBtn);
        
        passwordPanel.add(fieldPanel);
        
        return passwordPanel;
    }
    
    private JButton createLoginButton() {
        JButton button = new JButton("Sign In") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient background
                java.awt.GradientPaint gp = new java.awt.GradientPaint(
                    0, 0, new Color(52, 180, 219),
                    0, getHeight(), new Color(41, 140, 180)
                );
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Draw text
                g2.setFont(getFont());
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(getText());
                int textHeight = fm.getAscent();
                g2.drawString(getText(), (getWidth() - textWidth) / 2, (getHeight() + textHeight) / 2 - 2);
            }
        };
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(320, 48));
        button.setMaximumSize(new Dimension(320, 48));
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setContentAreaFilled(false);
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setToolTipText("Click to sign in");
            }
        });
        
        button.addActionListener(e -> handleLogin());
        
        return button;
    }
    
    private JPanel createRegisterLink() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel label = new JLabel("Don't have an account? ");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(new Color(180, 180, 180));
        
        JButton registerBtn = new JButton("Create Admin Account");
        registerBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        registerBtn.setForeground(new Color(52, 152, 219));
        registerBtn.setBackground(new Color(60, 60, 60));
        registerBtn.setOpaque(true);
        registerBtn.setBorder(null);
        registerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerBtn.setFocusPainted(false);
        registerBtn.addActionListener(e -> mainApp.showRegistration());
        
        panel.add(label);
        panel.add(registerBtn);
        
        return panel;
    }
    
    /**
     * Creates the forgot password link panel
     */
    private JPanel createForgotPasswordLink() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JButton forgotPasswordBtn = new JButton("Forgot Password?");
        forgotPasswordBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        forgotPasswordBtn.setForeground(new Color(52, 152, 219));
        forgotPasswordBtn.setBackground(new Color(60, 60, 60));
        forgotPasswordBtn.setOpaque(true);
        forgotPasswordBtn.setBorder(null);
        forgotPasswordBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotPasswordBtn.setFocusPainted(false);
        
        // Add underline effect on hover
        forgotPasswordBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                forgotPasswordBtn.setText("<html><u>Forgot Password?</u></html>");
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                forgotPasswordBtn.setText("Forgot Password?");
            }
        });
        
        forgotPasswordBtn.addActionListener(e -> showPasswordRecoveryDialog());
        
        panel.add(forgotPasswordBtn);
        
        return panel;
    }
    
    /**
     * Show the password recovery dialog
     * Prevents multiple windows by reusing existing dialog
     */
    private void showPasswordRecoveryDialog() {
        // Check if dialog already exists and is visible
        if (passwordRecoveryDialog != null && passwordRecoveryDialog.isVisible()) {
            // Bring existing dialog to front
            passwordRecoveryDialog.toFront();
            passwordRecoveryDialog.requestFocus();
            return;
        }
        
        // Create new dialog
        Window parentWindow = javax.swing.SwingUtilities.getWindowAncestor(this);
        passwordRecoveryDialog = new PasswordRecoveryDialog(parentWindow, mainApp);
        
        // Add window listener to reset reference when dialog is closed
        passwordRecoveryDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                passwordRecoveryDialog = null;
            }
        });
        
        passwordRecoveryDialog.setVisible(true);
    }
    
    private void togglePasswordVisibility() {
        if (passwordVisible) {
            passwordField.setText(visiblePasswordField.getText());
            passwordField.setVisible(true);
            visiblePasswordField.setVisible(false);
            togglePasswordBtn.setText("🔓");
            togglePasswordBtn.setToolTipText("Show password");
            passwordVisible = false;
        } else {
            visiblePasswordField.setText(new String(passwordField.getPassword()));
            visiblePasswordField.setVisible(true);
            passwordField.setVisible(false);
            togglePasswordBtn.setText("🔒");
            togglePasswordBtn.setToolTipText("Hide password");
            passwordVisible = true;
        }
    }
    
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordVisible ? 
            visiblePasswordField.getText() : 
            new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter username and password");
            return;
        }
        
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        
        try {
            User user = authUserDAO.authenticate(username, password);
            
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            
            if (user != null) {
                hideError();
                mainApp.loginSuccess(user);
            } else {
                showError("Invalid username or password");
                Logger.warn("Failed login attempt for username: " + username);
            }
        } catch (Exception e) {
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            showError("Login failed: " + e.getMessage());
            Logger.error("Login error", e);
        }
    }
    
    private void showError(String message) {
        errorLabel.setText("⚠️ " + message);
        errorLabel.setVisible(true);
    }
    
    private void hideError() {
        errorLabel.setVisible(false);
    }
    
    public void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        visiblePasswordField.setText("");
        hideError();
    }
    
    /**
     * Stop the neon animation timer to allow clean shutdown
     */
    public void stopAnimation() {
        if (neonTimer != null) {
            neonTimer.stop();
            neonTimer = null;
        }
    }
}

