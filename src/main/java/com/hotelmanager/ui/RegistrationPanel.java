package com.hotelmanager.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.hotelmanager.HotelManagerApp;
import com.hotelmanager.dao.AuthUserDAO;
import com.hotelmanager.model.User;
import com.hotelmanager.model.UserRole;
import com.hotelmanager.util.Logger;
import com.hotelmanager.util.PasswordUtil;

/**
 * Modern Registration Panel with FlatLaf styling - matches LoginPanel design
 */
public class RegistrationPanel extends JPanel {
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField visiblePasswordField;
    private JTextField visibleConfirmPasswordField;
    private JTextField emailField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField phoneField;
    
    // Security question fields
    private JComboBox<String> securityQuestionCombo;
    private JPasswordField securityAnswerField;
    
    // Available security questions
    private static final String[] SECURITY_QUESTIONS = {
        "What is your mother's maiden name?",
        "What is the name of your first pet?",
        "What is your birthplace?",
        "What is the name of your first school?",
        "What is your favorite color?",
        "What is your favorite movie?",
        "What is the name of your best friend?",
        "What was your first car?",
        "What is your favorite food?",
        "What is your favorite sports team?"
    };
    
    private JLabel usernameErrorLabel;
    private JLabel passwordErrorLabel;
    private JLabel confirmPasswordErrorLabel;
    private JLabel emailErrorLabel;
    private JLabel strengthLabel;
    
    private JButton togglePasswordBtn;
    private JButton toggleConfirmPasswordBtn;
    private boolean passwordVisible = false;
    private boolean confirmPasswordVisible = false;
    
    // Background image and neon effect
    private BufferedImage backgroundImage;
    private float neonGlow = 0f;
    private Timer neonTimer;
    
    private final HotelManagerApp mainApp;
    private final AuthUserDAO authUserDAO;
    
    public RegistrationPanel(HotelManagerApp mainApp) {
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
            backgroundImage = ImageIO.read(getClass().getResource("/backgrounds/registration/registration.png"));
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
            
// Add a semi-transparent overlay for better readability (matching login form transparency)
            g2d.setColor(new Color(0, 0, 0, 60));
            g2d.fillRect(0, 0, getWidth(), getHeight());
        } else {
            // Modern dark gradient background - deep blue/purple (matching LoginPanel)
            java.awt.GradientPaint gp = new java.awt.GradientPaint(
                0, 0, new Color(15, 15, 35), 
                0, getHeight(), new Color(25, 25, 50)
            );
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            
            // Add subtle geometric pattern overlay (matching LoginPanel)
            g2d.setColor(new Color(255, 255, 255, 3));
            for (int i = 0; i < getWidth(); i += 40) {
                for (int j = 0; j < getHeight(); j += 40) {
                    g2d.fillRect(i, j, 1, 1);
                }
            }
        }
    }
    
    private void initializeUI() {
        setLayout(new GridBagLayout());
        setBackground(new Color(20, 20, 25));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        
        // Create scroll pane - this will wrap the card
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        // Make scroll pane fill the available space
        scrollPane.setMinimumSize(new Dimension(400, 600));
        
        // Customize scrollbar appearance
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setUnitIncrement(16);
        verticalScrollBar.setBlockIncrement(100);
        verticalScrollBar.setPreferredSize(new Dimension(10, Integer.MAX_VALUE));
        
        // Main container card - modern glassmorphism style (matching LoginPanel)
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw glassmorphism background with subtle gradient (matching LoginPanel)
                java.awt.GradientPaint bgGradient = new java.awt.GradientPaint(
                    0, 0, new Color(30, 30, 45, 250),
                    0, getHeight(), new Color(25, 25, 40, 250)
                );
                g2.setPaint(bgGradient);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 25, 25);
                
                // Draw subtle inner shadow
                g2.setColor(new Color(0, 0, 0, 30));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 25, 25);
                
                // Draw neon border effect with animation (matching LoginPanel)
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
        card.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Set preferred size to allow scrolling
        card.setPreferredSize(new Dimension(450, 850));
        
        // Back button
        JButton backBtn = createBackButton();
        
        // Logo area
        JPanel logoPanel = createLogoPanel();
        
        // Title text with gradient effect (matching LoginPanel)
        JLabel titleLabel = new JLabel("Create Admin Account") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                java.awt.GradientPaint gp = new java.awt.GradientPaint(0, 0, new Color(52, 200, 255), 0, getHeight(), new Color(41, 128, 185));
                g2.setPaint(gp);
                g2.setFont(getFont());
                java.awt.FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), 0, fm.getAscent());
            }
        };
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Set up your hotel management system");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(new Color(180, 180, 180));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Form panel with proper grid layout
        JPanel formPanel = createFormPanelGrid();
        
        // Register button
        JButton registerBtn = createRegisterButton();
        
        // Login link
        JPanel loginPanel = createLoginLink();
        
        // Footer
        JLabel footerLabel = new JLabel("© 2026 Hotel Manager Pro");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footerLabel.setForeground(new Color(150, 150, 150));
        footerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Build the card with proper spacing
        JPanel topSection = new JPanel();
        topSection.setOpaque(false);
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        topSection.setAlignmentX(Component.CENTER_ALIGNMENT);
        topSection.add(backBtn);
        topSection.add(Box.createVerticalStrut(10));
        topSection.add(logoPanel);
        topSection.add(Box.createVerticalStrut(15));
        topSection.add(titleLabel);
        topSection.add(Box.createVerticalStrut(5));
        topSection.add(subtitleLabel);
        
        card.add(topSection);
        card.add(Box.createVerticalStrut(25));
        card.add(formPanel);
        card.add(Box.createVerticalStrut(20));
        card.add(registerBtn);
        card.add(Box.createVerticalStrut(15));
        card.add(loginPanel);
        card.add(Box.createVerticalStrut(20));
        card.add(footerLabel);
        
        // Set the card as the viewport view
        scrollPane.setViewportView(card);
        
        // Add scroll pane directly to the panel
        add(scrollPane, gbc);
        
        // Add real-time validation
        addRealTimeValidation();
    }
    
    private JButton createBackButton() {
        JButton backBtn = new JButton("←  Back to Login");
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        backBtn.setForeground(new Color(150, 200, 255));
        backBtn.setBorder(null);
        backBtn.setContentAreaFilled(false);
        backBtn.setFocusPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        backBtn.addActionListener(e -> mainApp.showLogin());
        return backBtn;
    }
    
    private JPanel createLogoPanel() {
        JPanel logoPanel = new JPanel();
        logoPanel.setOpaque(false);
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        logoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Load and display UPC logo image
        try {
            BufferedImage logoImage = ImageIO.read(getClass().getResource("/icons/UPC_BRAND.png"));
            if (logoImage != null) {
                // Scale the image to fit the logo area
                int maxWidth = 120;
                int maxHeight = 80;
                double scale = Math.min((double) maxWidth / logoImage.getWidth(), 
                                       (double) maxHeight / logoImage.getHeight());
                int newWidth = (int) (logoImage.getWidth() * scale);
                int newHeight = (int) (logoImage.getHeight() * scale);
                
                BufferedImage scaledImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = scaledImage.createGraphics();
                g2d.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, 
                                    java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2d.drawImage(logoImage, 0, 0, newWidth, newHeight, null);
                g2d.dispose();
                
                JLabel logoLabel = new JLabel(new javax.swing.ImageIcon(scaledImage));
                logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                logoPanel.add(logoLabel);
            } else {
                // Fallback to icon if image not found
                logoPanel.add(createFallbackIcon());
            }
        } catch (IOException e) {
            Logger.warn("Could not load UPC logo: " + e.getMessage());
            logoPanel.add(createFallbackIcon());
        }
        
        return logoPanel;
    }
    
    /**
     * Creates a fallback icon when the logo image cannot be loaded
     */
    private JPanel createFallbackIcon() {
        JPanel iconCircle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
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
    
    /**
     * Creates a form panel using GridBagLayout for better alignment
     */
    private JPanel createFormPanelGrid() {
        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new GridBagLayout());
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.setMaximumSize(new Dimension(380, 750));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.weightx = 1.0;
        
        // Username
        JLabel userLabel = createFieldLabel("Username *");
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(userLabel, gbc);
        
        gbc.gridy++;
        usernameField = createTextField();
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        formPanel.add(usernameField, gbc);
        
        gbc.gridy++;
        usernameErrorLabel = createErrorLabel();
        formPanel.add(usernameErrorLabel, gbc);
        
        gbc.gridy++;
        gbc.insets = new Insets(8, 0, 8, 0);
        formPanel.add(Box.createVerticalStrut(1), gbc);
        gbc.insets = new Insets(0, 0, 0, 0);
        
        // Password
        JLabel passLabel = createFieldLabel("Password *");
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(passLabel, gbc);
        
        gbc.gridy++;
        JPanel passwordSection = createPasswordSection();
        formPanel.add(passwordSection, gbc);
        
        gbc.gridy++;
        gbc.insets = new Insets(8, 0, 8, 0);
        formPanel.add(Box.createVerticalStrut(1), gbc);
        gbc.insets = new Insets(0, 0, 0, 0);
        
        // Confirm Password
        JLabel confirmLabel = createFieldLabel("Confirm Password *");
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(confirmLabel, gbc);
        
        gbc.gridy++;
        JPanel confirmSection = createConfirmPasswordSection();
        formPanel.add(confirmSection, gbc);
        
        gbc.gridy++;
        gbc.insets = new Insets(15, 0, 10, 0);
        formPanel.add(createDivider(), gbc);
        gbc.insets = new Insets(0, 0, 0, 0);
        
        // Email
        JLabel emailLabel = createFieldLabel("Email (Optional)");
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(emailLabel, gbc);
        
        gbc.gridy++;
        emailField = createTextField();
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        formPanel.add(emailField, gbc);
        
        gbc.gridy++;
        emailErrorLabel = createErrorLabel();
        formPanel.add(emailErrorLabel, gbc);
        
        gbc.gridy++;
        formPanel.add(Box.createVerticalStrut(8), gbc);
        
        // First Name and Last Name in a row
        gbc.gridwidth = 1;
        gbc.insets = new Insets(0, 0, 5, 5);
        
        JLabel firstNameLabel = createFieldLabel("First Name");
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(firstNameLabel, gbc);
        
        gbc.gridx = 1;
        gbc.insets = new Insets(0, 5, 5, 0);
        JLabel lastNameLabel = createFieldLabel("Last Name");
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(lastNameLabel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 5, 5);
        firstNameField = createTextField();
        formPanel.add(firstNameField, gbc);
        
        gbc.gridx = 1;
        gbc.insets = new Insets(0, 5, 5, 0);
        lastNameField = createTextField();
        formPanel.add(lastNameField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(8, 0, 0, 0);
        
        // Phone
        JLabel phoneLabel = createFieldLabel("Phone (Optional)");
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(phoneLabel, gbc);
        
        gbc.gridy++;
        phoneField = createTextField();
        phoneField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        formPanel.add(phoneField, gbc);
        
        gbc.gridy++;
        formPanel.add(Box.createVerticalStrut(15), gbc);
        
        // Security Question Section
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 0, 5, 0);
        formPanel.add(createSecurityQuestionDivider(), gbc);
        gbc.insets = new Insets(0, 0, 0, 0);
        
        // Security Question
        JLabel securityQuestionLabel = createFieldLabel("Security Question (for password recovery)");
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(securityQuestionLabel, gbc);
        
        gbc.gridy++;
        securityQuestionCombo = new JComboBox<>(SECURITY_QUESTIONS);
        securityQuestionCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        securityQuestionCombo.setForeground(new Color(240, 240, 240));
        securityQuestionCombo.setBackground(new Color(50, 50, 55));
        securityQuestionCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        formPanel.add(securityQuestionCombo, gbc);
        
        // Security Answer
        gbc.gridy++;
        JLabel securityAnswerLabel = createFieldLabel("Security Answer");
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(securityAnswerLabel, gbc);
        
        gbc.gridy++;
        securityAnswerField = new JPasswordField();
        securityAnswerField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        securityAnswerField.setForeground(new Color(240, 240, 240));
        securityAnswerField.setBackground(new Color(50, 50, 55));
        securityAnswerField.setCaretColor(new Color(240, 240, 240));
        securityAnswerField.setBorder(BorderFactory.createCompoundBorder(
            createBottomBorder(new Color(80, 80, 85), 2),
            new EmptyBorder(12, 15, 12, 15)
        ));
        securityAnswerField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        formPanel.add(securityAnswerField, gbc);
        
        gbc.gridy++;
        JLabel securityHintLabel = new JLabel("This will be used to recover your password if forgotten");
        securityHintLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        securityHintLabel.setForeground(new Color(150, 150, 155));
        formPanel.add(securityHintLabel, gbc);
        
        return formPanel;
    }
    
    private JPanel createSecurityQuestionDivider() {
        JPanel divider = new JPanel();
        divider.setOpaque(false);
        divider.setLayout(new BoxLayout(divider, BoxLayout.X_AXIS));
        divider.setMaximumSize(new Dimension(320, 20));
        
        JPanel leftLine = new JPanel();
        leftLine.setBackground(new Color(80, 80, 85));
        leftLine.setMaximumSize(new Dimension(100, 1));
        
        JPanel rightLine = new JPanel();
        rightLine.setBackground(new Color(80, 80, 85));
        rightLine.setMaximumSize(new Dimension(100, 1));
        
        JLabel orLabel = new JLabel("Security Setup");
        orLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        orLabel.setForeground(new Color(150, 150, 155));
        
        divider.add(leftLine);
        divider.add(orLabel);
        divider.add(rightLine);
        
        return divider;
    }
    
    private JPanel createPasswordSection() {
        JPanel section = new JPanel();
        section.setOpaque(false);
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.setMaximumSize(new Dimension(320, 100));
        
        // Password field with toggle
        JPanel fieldPanel = new JPanel();
        fieldPanel.setOpaque(false);
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.X_AXIS));
        fieldPanel.setMaximumSize(new Dimension(320, 45));
        
        passwordField = new JPasswordField();
        visiblePasswordField = new JTextField();
        
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setForeground(new Color(240, 240, 240));
        passwordField.setBackground(new Color(50, 50, 55));
        passwordField.setCaretColor(new Color(240, 240, 240));
        passwordField.setEchoChar('•');
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            createBottomBorder(new Color(80, 80, 85), 2),
            new EmptyBorder(12, 15, 12, 15)
        ));
        passwordField.setMaximumSize(new Dimension(270, 43));
        
        visiblePasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        visiblePasswordField.setForeground(new Color(240, 240, 240));
        visiblePasswordField.setBackground(new Color(50, 50, 55));
        visiblePasswordField.setCaretColor(new Color(240, 240, 240));
        visiblePasswordField.setBorder(BorderFactory.createCompoundBorder(
            createBottomBorder(new Color(80, 80, 85), 2),
            new EmptyBorder(12, 15, 12, 15)
        ));
        visiblePasswordField.setVisible(false);
        visiblePasswordField.setMaximumSize(new Dimension(270, 43));
        
        togglePasswordBtn = createToggleButton();
        togglePasswordBtn.addActionListener(e -> togglePasswordVisibility());
        
        fieldPanel.add(passwordField);
        fieldPanel.add(visiblePasswordField);
        fieldPanel.add(togglePasswordBtn);
        
        section.add(fieldPanel);
        
        passwordErrorLabel = createErrorLabel();
        section.add(passwordErrorLabel);
        
        strengthLabel = createStrengthIndicator();
        section.add(strengthLabel);
        
        return section;
    }
    
    private JPanel createConfirmPasswordSection() {
        JPanel section = new JPanel();
        section.setOpaque(false);
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.setMaximumSize(new Dimension(320, 80));
        
        // Confirm password field with toggle
        JPanel fieldPanel = new JPanel();
        fieldPanel.setOpaque(false);
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.X_AXIS));
        fieldPanel.setMaximumSize(new Dimension(320, 45));
        
        confirmPasswordField = new JPasswordField();
        visibleConfirmPasswordField = new JTextField();
        
        confirmPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        confirmPasswordField.setForeground(new Color(240, 240, 240));
        confirmPasswordField.setBackground(new Color(50, 50, 55));
        confirmPasswordField.setCaretColor(new Color(240, 240, 240));
        confirmPasswordField.setEchoChar('•');
        confirmPasswordField.setBorder(BorderFactory.createCompoundBorder(
            createBottomBorder(new Color(80, 80, 85), 2),
            new EmptyBorder(12, 15, 12, 15)
        ));
        confirmPasswordField.setMaximumSize(new Dimension(270, 43));
        
        visibleConfirmPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        visibleConfirmPasswordField.setForeground(new Color(240, 240, 240));
        visibleConfirmPasswordField.setBackground(new Color(50, 50, 55));
        visibleConfirmPasswordField.setCaretColor(new Color(240, 240, 240));
        visibleConfirmPasswordField.setBorder(BorderFactory.createCompoundBorder(
            createBottomBorder(new Color(80, 80, 85), 2),
            new EmptyBorder(12, 15, 12, 15)
        ));
        visibleConfirmPasswordField.setVisible(false);
        visibleConfirmPasswordField.setMaximumSize(new Dimension(270, 43));
        
        toggleConfirmPasswordBtn = createToggleButton();
        toggleConfirmPasswordBtn.addActionListener(e -> toggleConfirmPasswordVisibility());
        
        fieldPanel.add(confirmPasswordField);
        fieldPanel.add(visibleConfirmPasswordField);
        fieldPanel.add(toggleConfirmPasswordBtn);
        
        section.add(fieldPanel);
        
        confirmPasswordErrorLabel = createErrorLabel();
        section.add(confirmPasswordErrorLabel);
        
        return section;
    }
    
    private JPanel createNameRow() {
        JPanel row = new JPanel();
        row.setOpaque(false);
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(320, 95));
        
        JPanel firstNamePanel = new JPanel();
        firstNamePanel.setOpaque(false);
        firstNamePanel.setLayout(new BoxLayout(firstNamePanel, BoxLayout.Y_AXIS));
        firstNamePanel.setMaximumSize(new Dimension(155, 90));
        
        JLabel firstNameLabel = createFieldLabel("First Name");
        firstNameField = createTextField();
        firstNameField.setMaximumSize(new Dimension(155, 40));
        
        firstNamePanel.add(firstNameLabel);
        firstNamePanel.add(firstNameField);
        
        JPanel lastNamePanel = new JPanel();
        lastNamePanel.setOpaque(false);
        lastNamePanel.setLayout(new BoxLayout(lastNamePanel, BoxLayout.Y_AXIS));
        lastNamePanel.setMaximumSize(new Dimension(155, 90));
        
        JLabel lastNameLabel = createFieldLabel("Last Name");
        lastNameField = createTextField();
        lastNameField.setMaximumSize(new Dimension(155, 40));
        
        lastNamePanel.add(lastNameLabel);
        lastNamePanel.add(lastNameField);
        
        row.add(firstNamePanel);
        row.add(Box.createHorizontalStrut(10));
        row.add(lastNamePanel);
        
        return row;
    }
    
    private JPanel createDivider() {
        JPanel divider = new JPanel();
        divider.setOpaque(false);
        divider.setLayout(new BoxLayout(divider, BoxLayout.X_AXIS));
        divider.setMaximumSize(new Dimension(320, 20));
        
        JPanel leftLine = new JPanel();
        leftLine.setBackground(new Color(80, 80, 85));
        leftLine.setMaximumSize(new Dimension(130, 1));
        
        JPanel rightLine = new JPanel();
        rightLine.setBackground(new Color(80, 80, 85));
        rightLine.setMaximumSize(new Dimension(130, 1));
        
        JLabel orLabel = new JLabel("Optional Details");
        orLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        orLabel.setForeground(new Color(150, 150, 155));
        
        divider.add(leftLine);
        divider.add(orLabel);
        divider.add(rightLine);
        
        return divider;
    }
    
    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(new Color(200, 200, 200));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
    
    private JLabel createErrorLabel() {
        JLabel label = new JLabel("");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        label.setForeground(new Color(231, 76, 60));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setVisible(false);
        return label;
    }
    
    private JLabel createStrengthIndicator() {
        JLabel label = new JLabel("");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setMaximumSize(new Dimension(320, 20));
        return label;
    }
    
    /**
     * Creates a modern bottom border only (same as LoginPanel)
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
        field.setBackground(new Color(50, 50, 55));
        field.setCaretColor(new Color(240, 240, 240));
        field.setBorder(BorderFactory.createCompoundBorder(
            createBottomBorder(new Color(80, 80, 85), 2),
            new EmptyBorder(12, 15, 12, 15)
        ));
        
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
                    createBottomBorder(new Color(80, 80, 85), 2),
                    new EmptyBorder(12, 15, 12, 15)
                ));
            }
        });
        
        return field;
    }
    
    private JButton createToggleButton() {
        JButton button = new JButton();
        button.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 14));
        button.setText("👁️");
        button.setPreferredSize(new Dimension(50, 41));
        button.setBackground(new Color(50, 50, 55));
        button.setForeground(new Color(240, 240, 240));
        button.setBorder(BorderFactory.createCompoundBorder(
            new javax.swing.border.LineBorder(new Color(80, 80, 85), 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setToolTipText("Show password");
        button.setRolloverEnabled(true);
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(70, 70, 75));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(50, 50, 55));
            }
        });
        
        return button;
    }
    
    private JButton createRegisterButton() {
        JButton button = new JButton("Create Account") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient background (matching LoginPanel)
                java.awt.GradientPaint gp = new java.awt.GradientPaint(
                    0, 0, new Color(52, 180, 219),
                    0, getHeight(), new Color(41, 140, 180)
                );
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Draw text
                g2.setFont(getFont());
                g2.setColor(Color.WHITE);
                java.awt.FontMetrics fm = g2.getFontMetrics();
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
                button.setToolTipText("Click to create account");
            }
        });
        
        button.addActionListener(e -> handleRegister());
        
        return button;
    }
    
    private JPanel createLoginLink() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel label = new JLabel("Already have an account? ");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(new Color(200, 200, 200));
        
        JButton loginBtn = new JButton("Sign In");
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        loginBtn.setForeground(new Color(52, 152, 219));
        loginBtn.setBackground(new Color(50, 50, 55));
        loginBtn.setOpaque(true);
        loginBtn.setBorder(null);
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginBtn.setFocusPainted(false);
        loginBtn.addActionListener(e -> mainApp.showLogin());
        
        panel.add(label);
        panel.add(loginBtn);
        
        return panel;
    }
    
    private void addRealTimeValidation() {
        usernameField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { validateUsername(); }
            public void removeUpdate(DocumentEvent e) { validateUsername(); }
            public void insertUpdate(DocumentEvent e) { validateUsername(); }
        });
        
        passwordField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { updatePasswordStrength(); }
            public void removeUpdate(DocumentEvent e) { updatePasswordStrength(); }
            public void insertUpdate(DocumentEvent e) { updatePasswordStrength(); }
        });
        
        confirmPasswordField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { validateConfirmPassword(); }
            public void removeUpdate(DocumentEvent e) { validateConfirmPassword(); }
            public void insertUpdate(DocumentEvent e) { validateConfirmPassword(); }
        });
        
        emailField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { validateEmail(); }
            public void removeUpdate(DocumentEvent e) { validateEmail(); }
            public void insertUpdate(DocumentEvent e) { validateEmail(); }
        });
    }
    
    private void validateUsername() {
        String username = usernameField.getText().trim();
        if (username.isEmpty()) {
            showError(usernameErrorLabel, "");
            return;
        }
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            showError(usernameErrorLabel, "Only letters, numbers, and underscores allowed");
        } else if (username.length() < 3) {
            showError(usernameErrorLabel, "At least 3 characters required");
        } else {
            showError(usernameErrorLabel, "");
        }
    }
    
    private void updatePasswordStrength() {
        String password = new String(passwordField.getPassword());
        if (password.isEmpty()) {
            strengthLabel.setText("");
            showError(passwordErrorLabel, "");
            return;
        }
        
        int strength = calculatePasswordStrength(password);
        
        if (strength < 2) {
            strengthLabel.setText("🔴 Weak - Add more characters");
            strengthLabel.setForeground(new Color(231, 76, 60));
            showError(passwordErrorLabel, "Password is too weak");
        } else if (strength < 3) {
            strengthLabel.setText("🟡 Medium - Could be stronger");
            strengthLabel.setForeground(new Color(243, 156, 18));
            showError(passwordErrorLabel, "");
        } else {
            strengthLabel.setText("🟢 Strong password");
            strengthLabel.setForeground(new Color(39, 174, 96));
            showError(passwordErrorLabel, "");
        }
    }
    
    private int calculatePasswordStrength(String password) {
        int score = 0;
        if (password.length() >= 6) score++;
        if (password.length() >= 10) score++;
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*[a-z].*")) score++;
        if (password.matches(".*[0-9].*")) score++;
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) score++;
        return Math.min(score, 4);
    }
    
    private void validateConfirmPassword() {
        String password = new String(passwordField.getPassword());
        String confirm = new String(confirmPasswordField.getPassword());
        
        if (confirm.isEmpty()) {
            showError(confirmPasswordErrorLabel, "");
            return;
        }
        
        if (!password.equals(confirm)) {
            showError(confirmPasswordErrorLabel, "Passwords do not match");
        } else {
            showError(confirmPasswordErrorLabel, "");
        }
    }
    
    private void validateEmail() {
        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            showError(emailErrorLabel, "");
            return;
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showError(emailErrorLabel, "Invalid email format");
        } else {
            showError(emailErrorLabel, "");
        }
    }
    
    private void showError(JLabel label, String message) {
        if (label == null) return;
        if (message.isEmpty()) {
            label.setVisible(false);
            label.setText("");
        } else {
            label.setText(message);
            label.setVisible(true);
        }
    }
    
    private void togglePasswordVisibility() {
        if (passwordVisible) {
            passwordField.setText(visiblePasswordField.getText());
            passwordField.setVisible(true);
            visiblePasswordField.setVisible(false);
            togglePasswordBtn.setText("👁️");
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
    
    private void toggleConfirmPasswordVisibility() {
        if (confirmPasswordVisible) {
            confirmPasswordField.setText(visibleConfirmPasswordField.getText());
            confirmPasswordField.setVisible(true);
            visibleConfirmPasswordField.setVisible(false);
            toggleConfirmPasswordBtn.setText("👁️");
            toggleConfirmPasswordBtn.setToolTipText("Show password");
            confirmPasswordVisible = false;
        } else {
            visibleConfirmPasswordField.setText(new String(confirmPasswordField.getPassword()));
            visibleConfirmPasswordField.setVisible(true);
            confirmPasswordField.setVisible(false);
            toggleConfirmPasswordBtn.setText("🔒");
            toggleConfirmPasswordBtn.setToolTipText("Hide password");
            confirmPasswordVisible = true;
        }
    }
    
    private void handleRegister() {
        showError(usernameErrorLabel, "");
        showError(passwordErrorLabel, "");
        showError(confirmPasswordErrorLabel, "");
        showError(emailErrorLabel, "");
        
        String username = usernameField.getText().trim();
        String password = passwordVisible ? 
            visiblePasswordField.getText() : 
            new String(passwordField.getPassword());
        String confirmPassword = confirmPasswordVisible ? 
            visibleConfirmPasswordField.getText() : 
            new String(confirmPasswordField.getPassword());
        String email = emailField.getText().trim();
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String phone = phoneField.getText().trim();
        
        // Validation
        if (username.isEmpty()) {
            showError(usernameErrorLabel, "Username is required");
            usernameField.requestFocus();
            return;
        }
        
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            showError(usernameErrorLabel, "Only letters, numbers, and underscores allowed");
            usernameField.requestFocus();
            return;
        }
        
        if (password.isEmpty()) {
            showError(passwordErrorLabel, "Password is required");
            passwordField.requestFocus();
            return;
        }
        
        if (password.length() < 6) {
            showError(passwordErrorLabel, "Password must be at least 6 characters");
            passwordField.requestFocus();
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            showError(confirmPasswordErrorLabel, "Passwords do not match");
            confirmPasswordField.requestFocus();
            return;
        }
        
        if (!email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showError(emailErrorLabel, "Invalid email format");
            emailField.requestFocus();
            return;
        }
        
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        
        try {
            User existingUser = authUserDAO.findByUsername(username);
            if (existingUser != null) {
                showError(usernameErrorLabel, "Username already exists");
                usernameField.requestFocus();
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                return;
            }
            
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPasswordHash(PasswordUtil.hashPassword(password));
            newUser.setEmail(email.isEmpty() ? null : email);
            newUser.setFirstName(firstName.isEmpty() ? null : firstName);
            newUser.setLastName(lastName.isEmpty() ? null : lastName);
            newUser.setPhone(phone.isEmpty() ? null : phone);
            newUser.setRole(UserRole.ADMIN);
            newUser.setActive(true);
            
            // Set security question and answer
            String securityQuestion = (String) securityQuestionCombo.getSelectedItem();
            String securityAnswer = new String(securityAnswerField.getPassword()).trim();
            newUser.setSecurityQuestion(securityQuestion);
            newUser.setSecurityAnswerHash(PasswordUtil.hashPassword(securityAnswer.toLowerCase().trim()));
            
            int userId = authUserDAO.createUser(newUser);
            
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            
            if (userId > 0) {
                Logger.info("Admin user registered successfully: " + username);
                JOptionPane.showMessageDialog(this,
                    "🎉 Admin account created successfully!\n\nYou can now log in with your credentials.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                clearFields();
                mainApp.showLogin();
            } else {
                showError(usernameErrorLabel, "Failed to create account. Please try again.");
            }
            
        } catch (Exception e) {
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Logger.error("Error during registration", e);
            showError(usernameErrorLabel, "Registration failed: " + e.getMessage());
        }
    }
    
    private void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        visiblePasswordField.setText("");
        confirmPasswordField.setText("");
        visibleConfirmPasswordField.setText("");
        emailField.setText("");
        firstNameField.setText("");
        lastNameField.setText("");
        phoneField.setText("");
        
        // Clear security question fields
        if (securityQuestionCombo != null) {
            securityQuestionCombo.setSelectedIndex(0);
        }
        if (securityAnswerField != null) {
            securityAnswerField.setText("");
        }
        
        showError(usernameErrorLabel, "");
        showError(passwordErrorLabel, "");
        showError(confirmPasswordErrorLabel, "");
        showError(emailErrorLabel, "");
        strengthLabel.setText("");
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
