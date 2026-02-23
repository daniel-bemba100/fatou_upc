package com.hotelmanager.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.hotelmanager.HotelManagerApp;
import com.hotelmanager.dao.AuthUserDAO;
import com.hotelmanager.dao.UserDAO;
import com.hotelmanager.model.User;
import com.hotelmanager.util.Logger;
import com.hotelmanager.util.PasswordUtil;

/**
 * Password Recovery Dialog - allows admin users to recover their account
 * when password is forgotten
 */
public class PasswordRecoveryDialog extends JDialog {
    
    private final AuthUserDAO authUserDAO;
    private final HotelManagerApp mainApp;
    
    // Form fields for Option 1: Reset with Security Question
    private JTextField usernameField;
    private JComboBox<String> securityQuestionCombo;
    private JPasswordField securityAnswerField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    
    // Form fields for Option 2: Account Recovery (create new admin)
    private JTextField newAdminUsernameField;
    private JPasswordField newAdminPasswordField;
    private JPasswordField newAdminConfirmField;
    private JTextField newAdminEmailField;
    private JTextField newAdminFirstNameField;
    private JTextField newAdminLastNameField;
    
    // Panels
    private JPanel option1Panel;
    private JPanel option2Panel;
    
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
    
    public PasswordRecoveryDialog(Window parent, HotelManagerApp mainApp) {
        super(parent, "Password Recovery", ModalityType.APPLICATION_MODAL);
        this.mainApp = mainApp;
        this.authUserDAO = new AuthUserDAO();
        
        initializeUI();
        pack();
        setLocationRelativeTo(parent);
        setResizable(false);
    }
    
    private void initializeUI() {
        // Dark theme colors
        Color darkBg = new Color(30, 30, 46);
        Color cardBg = new Color(40, 40, 68);
        Color lightText = new Color(224, 224, 224);
        Color mutedText = new Color(150, 150, 170);
        Color neonBlue = new Color(52, 152, 219);
        Color darkInput = new Color(45, 45, 68);
        
        setLayout(new GridBagLayout());
        setPreferredSize(new Dimension(500, 620));
        getContentPane().setBackground(darkBg);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(25, 25, 10, 25);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Title with neon glow effect
        JLabel titleLabel = new JLabel("🔐 Password Recovery");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(neonBlue);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(titleLabel, gbc);
        
        gbc.gridy++;
        gbc.insets = new Insets(5, 25, 20, 25);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Recover your admin account or create a new one");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(mutedText);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(subtitleLabel, gbc);
        
        // Main container panel with dark theme
        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS));
        containerPanel.setOpaque(false);
        containerPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(neonBlue, 1),
            new EmptyBorder(20, 25, 20, 25)
        ));
        containerPanel.setBackground(cardBg);
        
        // Option 1: Reset with Security Question
        JLabel option1Title = createSectionTitle("Option 1: Reset Password with Security Question");
        containerPanel.add(option1Title);
        containerPanel.add(Box.createVerticalStrut(10));
        
        option1Panel = createOption1Panel();
        containerPanel.add(option1Panel);
        
        containerPanel.add(Box.createVerticalStrut(20));
        
        // Divider
        JPanel divider = createDivider();
        containerPanel.add(divider);
        
        containerPanel.add(Box.createVerticalStrut(20));
        
        // Option 2: Create New Admin
        JLabel option2Title = createSectionTitle("Option 2: Create New Admin Account");
        containerPanel.add(option2Title);
        
        JLabel option2Desc = new JLabel("<html><body style='width: 400px; color: #9696aa;'>Use this if you don't have a security question set up or cannot recover your account. This will create a new admin account.</body></html>");
        option2Desc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        option2Desc.setAlignmentX(Component.LEFT_ALIGNMENT);
        containerPanel.add(option2Desc);
        
        containerPanel.add(Box.createVerticalStrut(10));
        
        option2Panel = createOption2Panel();
        containerPanel.add(option2Panel);
        
        gbc.gridy++;
        gbc.insets = new Insets(10, 20, 20, 20);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        
        add(containerPanel, gbc);
        
        // Close button at bottom
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        
        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        closeButton.setPreferredSize(new Dimension(100, 35));
        closeButton.setBackground(new Color(80, 80, 100));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> dispose());
        
        buttonPanel.add(closeButton);
        
        gbc.gridy++;
        gbc.insets = new Insets(10, 20, 20, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        
        add(buttonPanel, gbc);
    }
    
    private JLabel createSectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(new Color(52, 152, 219));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
    
    private JPanel createDivider() {
        JPanel divider = new JPanel();
        divider.setLayout(new BoxLayout(divider, BoxLayout.X_AXIS));
        divider.setAlignmentX(Component.CENTER_ALIGNMENT);
        divider.setMaximumSize(new Dimension(450, 20));
        
        JPanel leftLine = new JPanel();
        leftLine.setBackground(new Color(80, 80, 100));
        leftLine.setMaximumSize(new Dimension(180, 1));
        
        JPanel rightLine = new JPanel();
        rightLine.setBackground(new Color(80, 80, 100));
        rightLine.setMaximumSize(new Dimension(180, 1));
        
        JLabel orLabel = new JLabel("OR");
        orLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        orLabel.setForeground(new Color(120, 120, 140));
        
        divider.add(leftLine);
        divider.add(Box.createHorizontalStrut(10));
        divider.add(orLabel);
        divider.add(Box.createHorizontalStrut(10));
        divider.add(rightLine);
        
        return divider;
    }
    
    private JPanel createOption1Panel() {
        // Dark theme colors
        Color darkBg = new Color(30, 30, 46);
        Color cardBg = new Color(40, 40, 68);
        Color lightText = new Color(224, 224, 224);
        Color mutedText = new Color(150, 150, 170);
        Color neonBlue = new Color(52, 152, 219);
        Color darkInput = new Color(45, 45, 68);
        
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(450, 250));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 0, 5, 10);
        
        // Username
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userLabel.setForeground(lightText);
        panel.add(userLabel, gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        usernameField = new JTextField();
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        usernameField.setPreferredSize(new Dimension(200, 30));
        usernameField.setBackground(darkInput);
        usernameField.setForeground(lightText);
        usernameField.setCaretColor(lightText);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(80, 80, 100), 1),
            new EmptyBorder(5, 8, 5, 8)
        ));
        panel.add(usernameField, gbc);
        
        gbc.gridx = 2;
        gbc.weightx = 0;
        
        JButton checkUserBtn = new JButton("Check");
        checkUserBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        checkUserBtn.setPreferredSize(new Dimension(70, 28));
        checkUserBtn.setBackground(neonBlue);
        checkUserBtn.setForeground(Color.WHITE);
        checkUserBtn.setFocusPainted(false);
        checkUserBtn.addActionListener(e -> checkUserSecurityQuestion());
        panel.add(checkUserBtn, gbc);
        
        // Security Question
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        
        JLabel questionLabel = new JLabel("Security Question:");
        questionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        questionLabel.setForeground(lightText);
        panel.add(questionLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        securityQuestionCombo = new JComboBox<>(SECURITY_QUESTIONS);
        securityQuestionCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        securityQuestionCombo.setPreferredSize(new Dimension(280, 30));
        securityQuestionCombo.setBackground(darkInput);
        securityQuestionCombo.setForeground(lightText);
        securityQuestionCombo.setEnabled(false);
        panel.add(securityQuestionCombo, gbc);
        
        // Security Answer
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        
        JLabel answerLabel = new JLabel("Answer:");
        answerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        answerLabel.setForeground(lightText);
        panel.add(answerLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        securityAnswerField = new JPasswordField();
        securityAnswerField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        securityAnswerField.setPreferredSize(new Dimension(280, 30));
        securityAnswerField.setBackground(darkInput);
        securityAnswerField.setForeground(lightText);
        securityAnswerField.setCaretColor(lightText);
        securityAnswerField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(80, 80, 100), 1),
            new EmptyBorder(5, 8, 5, 8)
        ));
        securityAnswerField.setEnabled(false);
        panel.add(securityAnswerField, gbc);
        
        // New Password
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        
        JLabel newPassLabel = new JLabel("New Password:");
        newPassLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        newPassLabel.setForeground(lightText);
        panel.add(newPassLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        newPasswordField = new JPasswordField();
        newPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        newPasswordField.setPreferredSize(new Dimension(280, 30));
        newPasswordField.setBackground(darkInput);
        newPasswordField.setForeground(lightText);
        newPasswordField.setCaretColor(lightText);
        newPasswordField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(80, 80, 100), 1),
            new EmptyBorder(5, 8, 5, 8)
        ));
        newPasswordField.setEnabled(false);
        panel.add(newPasswordField, gbc);
        
        // Confirm Password
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        
        JLabel confirmLabel = new JLabel("Confirm Password:");
        confirmLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        confirmLabel.setForeground(lightText);
        panel.add(confirmLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        confirmPasswordField.setPreferredSize(new Dimension(280, 30));
        confirmPasswordField.setBackground(darkInput);
        confirmPasswordField.setForeground(lightText);
        confirmPasswordField.setCaretColor(lightText);
        confirmPasswordField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(80, 80, 100), 1),
            new EmptyBorder(5, 8, 5, 8)
        ));
        confirmPasswordField.setEnabled(false);
        panel.add(confirmPasswordField, gbc);
        
        // Reset Button
        gbc.gridx = 1;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(15, 0, 5, 0);
        
        JButton resetBtn = new JButton("Reset Password");
        resetBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        resetBtn.setForeground(Color.WHITE);
        resetBtn.setBackground(neonBlue);
        resetBtn.setPreferredSize(new Dimension(130, 35));
        resetBtn.setEnabled(false);
        resetBtn.setFocusPainted(false);
        resetBtn.addActionListener(e -> handlePasswordReset());
        panel.add(resetBtn, gbc);
        
        return panel;
    }
    
    private JPanel createOption2Panel() {
        // Dark theme colors
        Color darkBg = new Color(30, 30, 46);
        Color cardBg = new Color(40, 40, 68);
        Color lightText = new Color(224, 224, 224);
        Color mutedText = new Color(150, 150, 170);
        Color neonBlue = new Color(52, 152, 219);
        Color darkInput = new Color(45, 45, 68);
        Color greenBtn = new Color(39, 174, 96);
        
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(450, 280));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 0, 5, 10);
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        
        // Username
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userLabel.setForeground(lightText);
        panel.add(userLabel, gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        newAdminUsernameField = new JTextField();
        newAdminUsernameField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        newAdminUsernameField.setPreferredSize(new Dimension(200, 30));
        newAdminUsernameField.setBackground(darkInput);
        newAdminUsernameField.setForeground(lightText);
        newAdminUsernameField.setCaretColor(lightText);
        newAdminUsernameField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(80, 80, 100), 1),
            new EmptyBorder(5, 8, 5, 8)
        ));
        panel.add(newAdminUsernameField, gbc);
        
        // Password
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        passLabel.setForeground(lightText);
        panel.add(passLabel, gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        newAdminPasswordField = new JPasswordField();
        newAdminPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        newAdminPasswordField.setPreferredSize(new Dimension(200, 30));
        newAdminPasswordField.setBackground(darkInput);
        newAdminPasswordField.setForeground(lightText);
        newAdminPasswordField.setCaretColor(lightText);
        newAdminPasswordField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(80, 80, 100), 1),
            new EmptyBorder(5, 8, 5, 8)
        ));
        panel.add(newAdminPasswordField, gbc);
        
        // Confirm Password
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        
        JLabel confirmLabel = new JLabel("Confirm:");
        confirmLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        confirmLabel.setForeground(lightText);
        panel.add(confirmLabel, gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        newAdminConfirmField = new JPasswordField();
        newAdminConfirmField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        newAdminConfirmField.setPreferredSize(new Dimension(200, 30));
        newAdminConfirmField.setBackground(darkInput);
        newAdminConfirmField.setForeground(lightText);
        newAdminConfirmField.setCaretColor(lightText);
        newAdminConfirmField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(80, 80, 100), 1),
            new EmptyBorder(5, 8, 5, 8)
        ));
        panel.add(newAdminConfirmField, gbc);
        
        // Email
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        emailLabel.setForeground(lightText);
        panel.add(emailLabel, gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        newAdminEmailField = new JTextField();
        newAdminEmailField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        newAdminEmailField.setPreferredSize(new Dimension(200, 30));
        newAdminEmailField.setBackground(darkInput);
        newAdminEmailField.setForeground(lightText);
        newAdminEmailField.setCaretColor(lightText);
        newAdminEmailField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(80, 80, 100), 1),
            new EmptyBorder(5, 8, 5, 8)
        ));
        panel.add(newAdminEmailField, gbc);
        
        // First Name
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        
        JLabel firstNameLabel = new JLabel("First Name:");
        firstNameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        firstNameLabel.setForeground(lightText);
        panel.add(firstNameLabel, gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        newAdminFirstNameField = new JTextField();
        newAdminFirstNameField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        newAdminFirstNameField.setPreferredSize(new Dimension(200, 30));
        newAdminFirstNameField.setBackground(darkInput);
        newAdminFirstNameField.setForeground(lightText);
        newAdminFirstNameField.setCaretColor(lightText);
        newAdminFirstNameField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(80, 80, 100), 1),
            new EmptyBorder(5, 8, 5, 8)
        ));
        panel.add(newAdminFirstNameField, gbc);
        
        // Last Name
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        
        JLabel lastNameLabel = new JLabel("Last Name:");
        lastNameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lastNameLabel.setForeground(lightText);
        panel.add(lastNameLabel, gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        newAdminLastNameField = new JTextField();
        newAdminLastNameField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        newAdminLastNameField.setPreferredSize(new Dimension(200, 30));
        newAdminLastNameField.setBackground(darkInput);
        newAdminLastNameField.setForeground(lightText);
        newAdminLastNameField.setCaretColor(lightText);
        newAdminLastNameField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(80, 80, 100), 1),
            new EmptyBorder(5, 8, 5, 8)
        ));
        panel.add(newAdminLastNameField, gbc);
        
        // Create Admin Button
        gbc.gridx = 1;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(15, 0, 5, 0);
        
        JButton createBtn = new JButton("Create Admin");
        createBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        createBtn.setForeground(Color.WHITE);
        createBtn.setBackground(greenBtn);
        createBtn.setPreferredSize(new Dimension(130, 35));
        createBtn.setFocusPainted(false);
        createBtn.addActionListener(e -> handleCreateAdmin());
        panel.add(createBtn, gbc);
        
        return panel;
    }
    
    private void checkUserSecurityQuestion() {
        String username = usernameField.getText().trim();
        
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter a username.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            User user = authUserDAO.findByUsername(username);
            
            if (user == null) {
                JOptionPane.showMessageDialog(this,
                    "User not found. Please check the username.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Check if user has a security question set
            if (user.getSecurityQuestion() == null || user.getSecurityQuestion().isEmpty()) {
                int response = JOptionPane.showConfirmDialog(this,
                    "This user does not have a security question set up.\n" +
                    "Would you like to set up a security question now?",
                    "No Security Question",
                    JOptionPane.YES_NO_OPTION);
                
                if (response == JOptionPane.YES_OPTION) {
                    // Show setup dialog
                    showSecurityQuestionSetupDialog(user);
                }
                return;
            }
            
            // Enable fields and set the security question
            securityQuestionCombo.setSelectedItem(user.getSecurityQuestion());
            securityQuestionCombo.setEnabled(true);
            securityAnswerField.setEnabled(true);
            newPasswordField.setEnabled(true);
            confirmPasswordField.setEnabled(true);
            
            // Enable reset button
            for (Component comp : option1Panel.getComponents()) {
                if (comp instanceof JButton && ((JButton) comp).getText().equals("Reset Password")) {
                    ((JButton) comp).setEnabled(true);
                }
            }
            
            JOptionPane.showMessageDialog(this,
                "Security question found! Please enter your answer and new password.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception e) {
            Logger.error("Error checking user security question", e);
            JOptionPane.showMessageDialog(this,
                "Error: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showSecurityQuestionSetupDialog(User user) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JLabel questionLabel = new JLabel("Select Security Question:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(questionLabel, gbc);
        
        JComboBox<String> questionCombo = new JComboBox<>(SECURITY_QUESTIONS);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(questionCombo, gbc);
        
        JLabel answerLabel = new JLabel("Your Answer:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(answerLabel, gbc);
        
        JTextField answerField = new JTextField(20);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(answerField, gbc);
        
        int result = JOptionPane.showConfirmDialog(this, panel,
            "Set Up Security Question", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            String question = (String) questionCombo.getSelectedItem();
            String answer = answerField.getText().trim();
            
            if (answer.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please provide an answer.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                boolean updated = authUserDAO.updateSecurityQuestion(user.getId(), question, answer);
                if (updated) {
                    JOptionPane.showMessageDialog(this,
                        "Security question set up successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Enable the form fields
                    securityQuestionCombo.setSelectedItem(question);
                    securityQuestionCombo.setEnabled(true);
                    securityAnswerField.setEnabled(true);
                    newPasswordField.setEnabled(true);
                    confirmPasswordField.setEnabled(true);
                    
                    // Enable reset button
                    for (Component comp : option1Panel.getComponents()) {
                        if (comp instanceof JButton && ((JButton) comp).getText().equals("Reset Password")) {
                            ((JButton) comp).setEnabled(true);
                        }
                    }
                }
            } catch (Exception e) {
                Logger.error("Error setting up security question", e);
                JOptionPane.showMessageDialog(this,
                    "Error setting up security question: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void handlePasswordReset() {
        String username = usernameField.getText().trim();
        String answer = new String(securityAnswerField.getPassword()).trim();
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        
        // Validation
        if (answer.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter your security answer.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (newPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter a new password.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (newPassword.length() < 6) {
            JOptionPane.showMessageDialog(this,
                "Password must be at least 6 characters.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                "Passwords do not match.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            User user = authUserDAO.findByUsername(username);
            if (user == null) {
                JOptionPane.showMessageDialog(this,
                    "User not found.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Verify security answer
            boolean isValidAnswer = authUserDAO.verifySecurityAnswer(user.getId(), answer);
            if (!isValidAnswer) {
                JOptionPane.showMessageDialog(this,
                    "Incorrect security answer. Please try again.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Change password
            boolean changed = authUserDAO.changePassword(user.getId(), newPassword);
            if (changed) {
                JOptionPane.showMessageDialog(this,
                    "✅ Password reset successfully!\n\nYou can now log in with your new password.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Clear fields
                usernameField.setText("");
                securityAnswerField.setText("");
                newPasswordField.setText("");
                confirmPasswordField.setText("");
                
                // Disable fields
                securityQuestionCombo.setEnabled(false);
                securityAnswerField.setEnabled(false);
                newPasswordField.setEnabled(false);
                confirmPasswordField.setEnabled(false);
                
                for (Component comp : option1Panel.getComponents()) {
                    if (comp instanceof JButton && ((JButton) comp).getText().equals("Reset Password")) {
                        ((JButton) comp).setEnabled(false);
                    }
                }
                
                // Close dialog and return to login
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to reset password. Please try again.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            Logger.error("Error resetting password", e);
            JOptionPane.showMessageDialog(this,
                "Error: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleCreateAdmin() {
        String username = newAdminUsernameField.getText().trim();
        String password = new String(newAdminPasswordField.getPassword());
        String confirmPassword = new String(newAdminConfirmField.getPassword());
        String email = newAdminEmailField.getText().trim();
        String firstName = newAdminFirstNameField.getText().trim();
        String lastName = newAdminLastNameField.getText().trim();
        
        // Validation
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter a username.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter a password.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this,
                "Password must be at least 6 characters.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                "Passwords do not match.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (firstName.isEmpty()) {
            firstName = "Admin";
        }
        
        if (lastName.isEmpty()) {
            lastName = "User";
        }
        
        try {
            // Check if username already exists
            User existingUser = authUserDAO.findByUsername(username);
            if (existingUser != null) {
                JOptionPane.showMessageDialog(this,
                    "Username already exists. Please choose a different username.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Check if email already exists (if provided)
            if (!email.isEmpty()) {
                User existingEmail = authUserDAO.findByEmail(email);
                if (existingEmail != null) {
                    JOptionPane.showMessageDialog(this,
                        "Email already exists. Please use a different email.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            // Create new admin user
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPasswordHash(PasswordUtil.hashPassword(password));
            newUser.setEmail(email.isEmpty() ? null : email);
            newUser.setFirstName(firstName);
            newUser.setLastName(lastName);
            newUser.setPhone(null);
            newUser.setRole(com.hotelmanager.model.UserRole.ADMIN);
            newUser.setActive(true);
            newUser.setSecurityQuestion(null);
            newUser.setSecurityAnswerHash(null);
            
            int userId = authUserDAO.createUser(newUser);
            
            if (userId > 0) {
                Logger.info("New admin account created via password recovery: " + username);
                
                JOptionPane.showMessageDialog(this,
                    "🎉 New admin account created successfully!\n\n" +
                    "Username: " + username + "\n\n" +
                    "You can now log in with these credentials.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Clear fields
                newAdminUsernameField.setText("");
                newAdminPasswordField.setText("");
                newAdminConfirmField.setText("");
                newAdminEmailField.setText("");
                newAdminFirstNameField.setText("");
                newAdminLastNameField.setText("");
                
                // Close dialog
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to create admin account. Please try again.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            Logger.error("Error creating admin account", e);
            JOptionPane.showMessageDialog(this,
                "Error: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}

