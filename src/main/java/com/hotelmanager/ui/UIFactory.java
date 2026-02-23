package com.hotelmanager.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;

/**
 * UI Factory class for creating professional-looking Swing components
 * with consistent styling across the application
 */
public class UIFactory {
    
    // Color Palette
    public static final Color PRIMARY_COLOR = new Color(44, 62, 80);        // Dark blue-gray
    public static final Color SECONDARY_COLOR = new Color(52, 152, 219);    // Blue
    public static final Color ACCENT_COLOR = new Color(231, 76, 60);        // Red
    public static final Color SUCCESS_COLOR = new Color(39, 174, 96);      // Green
    public static final Color WARNING_COLOR = new Color(243, 156, 18);     // Orange
    public static final Color BG_COLOR = new Color(236, 240, 241);          // Light gray
    public static final Color CARD_BG = Color.WHITE;
    public static final Color TEXT_COLOR = new Color(44, 62, 80);
    public static final Color TEXT_SECONDARY = new Color(127, 140, 141);
    public static final Color BORDER_COLOR = new Color(189, 195, 199);
    public static final Color SIDEBAR_BG = new Color(44, 62, 80);
    public static final Color SIDEBAR_HOVER = new Color(52, 73, 94);
    
    // Private constructor to prevent instantiation
    private UIFactory() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    /**
     * Create a styled text field
     */
    public static JTextField createTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(TEXT_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1),
            new EmptyBorder(10, 12, 10, 12)
        ));
        field.setBackground(Color.WHITE);
        
        // Add focus listener for border color change
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(SECONDARY_COLOR, 2),
                    new EmptyBorder(9, 11, 9, 11)
                ));
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(BORDER_COLOR, 1),
                    new EmptyBorder(10, 12, 10, 12)
                ));
            }
        });
        
        return field;
    }
    
    /**
     * Create a styled password field
     */
    public static JPasswordField createPasswordField(int columns) {
        JPasswordField field = new JPasswordField(columns);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(TEXT_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1),
            new EmptyBorder(10, 12, 10, 12)
        ));
        field.setBackground(Color.WHITE);
        
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(SECONDARY_COLOR, 2),
                    new EmptyBorder(9, 11, 9, 11)
                ));
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(BORDER_COLOR, 1),
                    new EmptyBorder(10, 12, 10, 12)
                ));
            }
        });
        
        return field;
    }
    
    /**
     * Create a styled primary button
     */
    public static JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(SECONDARY_COLOR);
        button.setBorder(new EmptyBorder(12, 24, 12, 24));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(41, 128, 185));
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(SECONDARY_COLOR);
            }
        });
        
        return button;
    }
    
    /**
     * Create a styled secondary button
     */
    public static JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(TEXT_COLOR);
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1),
            new EmptyBorder(12, 24, 12, 24)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(BG_COLOR);
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.WHITE);
            }
        });
        
        return button;
    }
    
    /**
     * Create a styled danger button
     */
    public static JButton createDangerButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(ACCENT_COLOR);
        button.setBorder(new EmptyBorder(12, 24, 12, 24));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(192, 57, 43));
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(ACCENT_COLOR);
            }
        });
        
        return button;
    }
    
    /**
     * Create a modern sidebar button (Exit)
     */
    public static JButton createSidebarExitButton(String text) {
        JButton button = new JButton("🚪  " + text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(52, 73, 94));
        button.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(44, 62, 80), 1),
            new EmptyBorder(8, 15, 8, 15)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setIconTextGap(8);
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(41, 128, 185));
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(52, 73, 94));
            }
        });
        
        return button;
    }
    
    /**
     * Create a modern sidebar button (Logout)
     */
    public static JButton createSidebarLogoutButton(String text) {
        JButton button = new JButton("🔓  " + text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(ACCENT_COLOR);
        button.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(192, 57, 43), 1),
            new EmptyBorder(8, 15, 8, 15)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setIconTextGap(8);
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(192, 57, 43));
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(ACCENT_COLOR);
            }
        });
        
        return button;
    }
    
    /**
     * Create a styled label
     */
    public static JLabel createLabel(String text, int fontSize, boolean bold) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", bold ? Font.BOLD : Font.PLAIN, fontSize));
        label.setForeground(TEXT_COLOR);
        return label;
    }
    
    /**
     * Create a title label
     */
    public static JLabel createTitleLabel(String text) {
        return createLabel(text, 24, true);
    }
    
    /**
     * Create a subtitle label
     */
    public static JLabel createSubtitleLabel(String text) {
        return createLabel(text, 14, false);
    }
    
    /**
     * Create a card panel with shadow effect
     */
    public static JPanel createCard() {
        JPanel card = new JPanel();
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        return card;
    }
    
    /**
     * Create a sidebar menu item
     */
    public static JButton createSidebarItem(String text, Icon icon) {
        JButton button = new JButton(text, icon);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0, 0, 0, 0));
        button.setBorder(new EmptyBorder(12, 20, 12, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setIconTextGap(12);
        button.setOpaque(false);
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (!button.getBackground().equals(SECONDARY_COLOR)) {
                    button.setBackground(SIDEBAR_HOVER);
                }
            }
            public void mouseExited(MouseEvent e) {
                if (!button.getBackground().equals(SECONDARY_COLOR)) {
                    button.setBackground(new Color(0, 0, 0, 0));
                }
            }
        });
        
        return button;
    }
    
    /**
     * Create a scrollable table
     */
    public static JScrollPane createScrollPane(Component view) {
        JScrollPane scrollPane = new JScrollPane(view);
        scrollPane.setBorder(new LineBorder(BORDER_COLOR, 1));
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = BORDER_COLOR;
            }
        });
        return scrollPane;
    }
    
    /**
     * Style a JTable
     */
    public static void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(40);
        table.setSelectionBackground(SECONDARY_COLOR);
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(BORDER_COLOR);
        table.setShowGrid(true);
        
        // Header styling
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(PRIMARY_COLOR);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setOpaque(true);
        
        // Alternate row colors
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
                }
                return c;
            }
        });
    }
    
    /**
     * Create a toggle button for password visibility
     */
    public static JButton createToggleButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1),
            new EmptyBorder(8, 10, 8, 10)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(BG_COLOR);
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.WHITE);
            }
        });
        
        return button;
    }
}

