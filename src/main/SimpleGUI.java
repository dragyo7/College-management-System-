package main;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import model.SimpleStudent;
import dao.SimpleStudentDAO;

/**
 * College Admission Management System - Modern GUI Application
 * 
 * This class provides the main graphical user interface for the college admission system.
 * Features include:
 * - Modern, responsive design with dark mode support
 * - Student management (add, search, view)
 * - Real-time search functionality
 * - Professional styling with hover effects
 * - Comprehensive error handling
 * 
 * @author College Admission System Team
 * @version 2.0
 */
public class SimpleGUI extends JFrame {
    // Core components for student management
    private final SimpleStudentDAO studentDAO;  // Data access object for student operations
    private JTable studentsTable;               // Table displaying student records
    private DefaultTableModel tableModel;       // Model for the student table
    private JTextField searchField;             // Search input field
    private boolean isDarkMode = false;       // Dark mode toggle state
    
    // UI panels for organized layout
    private JPanel mainPanel;   // Main container panel
    private JPanel topPanel;    // Top panel with controls
    private JPanel buttonPanel; // Panel for action buttons
    private JPanel searchPanel; // Panel for search functionality
    private JScrollPane scrollPane; // Scrollable container for table
    private JLabel statusBar;     // Status bar for user feedback

    /**
     * Constructor for SimpleGUI
     * Initializes the main application window with modern UI components
     * Sets up database connection, creates UI panels, and loads initial data
     */
    public SimpleGUI() {
        // Initialize data access object for student operations
        this.studentDAO = new SimpleStudentDAO();
        
        // Configure main application window
        setTitle("College Admission System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);  // Set initial window size
        setLocationRelativeTo(null); // Center on screen
        setMinimumSize(new Dimension(800, 600)); // Minimum window size
        
        // Set modern look and feel for better UI experience
        setupLookAndFeel();
        
        // Create main container panel with proper spacing
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Create top panel with controls and search functionality
        topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Controls"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Create button panel with action buttons
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        
        // Create styled buttons with colors and icons
        JButton addButton = createStyledButton("➕ Add Student", new Color(46, 204, 113)); // Green
        JButton refreshButton = createStyledButton("🔄 Refresh", new Color(52, 152, 219)); // Blue
        JButton darkModeButton = createStyledButton("🌙 Dark Mode", new Color(155, 89, 182)); // Purple
        JButton exitButton = createStyledButton("❌ Exit", new Color(231, 76, 60)); // Red
        
        // Add buttons to panel with proper spacing
        buttonPanel.add(addButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(darkModeButton);
        buttonPanel.add(Box.createHorizontalStrut(30)); // Spacer
        buttonPanel.add(exitButton);
        
        // Create search panel for student lookup
        searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        searchField = new JTextField(25); // 25 character width
        JButton searchButton = createStyledButton("🔍 Search", new Color(241, 196, 15)); // Yellow
        
        // Style the search input field
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        // Add search components to panel
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        // Add button and search panels to top panel
        topPanel.add(buttonPanel, BorderLayout.WEST);
        topPanel.add(searchPanel, BorderLayout.EAST);
        
        // Create and configure student records table
        createStudentsTable();
        scrollPane = new JScrollPane(studentsTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Student Records"));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Smooth scrolling
        
        // Create status bar for user feedback
        statusBar = new JLabel("🚀 Ready");
        statusBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        statusBar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        // Add all components to main panel
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(statusBar, BorderLayout.SOUTH);
        
        // Configure action listeners for all buttons
        addButton.addActionListener(e -> showAddStudentDialog());
        refreshButton.addActionListener(e -> refreshStudentList());
        darkModeButton.addActionListener(e -> toggleDarkMode());
        exitButton.addActionListener(e -> System.exit(0));
        searchButton.addActionListener(e -> searchStudents());
        searchField.addActionListener(e -> searchStudents()); // Enter key support
        
        // Add main panel to frame
        add(mainPanel);
        
        // Apply initial theme (light mode by default)
        applyTheme();
        
        // Load initial student data from database
        refreshStudentList();
    }
    
    /**
     * Sets up the modern look and feel for the application
     * Uses Nimbus Look and Feel for better visual appearance
     * Falls back to default if Nimbus is not available
     */
    private void setupLookAndFeel() {
        try {
            // Set modern look and feel
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            // Use default look and feel if Nimbus is not available
            System.err.println("Nimbus Look and Feel not available, using default");
        }
    }
    
    /**
     * Creates a professionally styled button with hover effects
     * 
     * @param text The button text including emoji icons
     * @param color The base background color for the button
     * @return A fully styled JButton with hover effects and modern appearance
     */
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker(), 2),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        
        // Add hover effect for better user interaction
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.brighter());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });
        
        return button;
    }
    
    private void toggleDarkMode() {
        isDarkMode = !isDarkMode;
        applyTheme();
        
        // Update dark mode button text
        JButton darkModeButton = (JButton) buttonPanel.getComponent(2);
        darkModeButton.setText(isDarkMode ? "☀️ Light Mode" : "🌙 Dark Mode");
    }
    
    private void applyTheme() {
        if (isDarkMode) {
            applyDarkTheme();
        } else {
            applyLightTheme();
        }
        
        // Refresh the UI
        SwingUtilities.updateComponentTreeUI(this);
    }
    
    private void applyDarkTheme() {
        // Dark background colors
        Color darkBg = new Color(45, 45, 45);
        Color darkerBg = new Color(35, 35, 35);
        Color lightText = new Color(220, 220, 220);
        Color accentColor = new Color(66, 133, 244);
        
        // Set component colors
        mainPanel.setBackground(darkBg);
        topPanel.setBackground(darkerBg);
        buttonPanel.setBackground(darkerBg);
        searchPanel.setBackground(darkerBg);
        
        // Update table colors
        studentsTable.setBackground(darkerBg);
        studentsTable.setForeground(lightText);
        studentsTable.setSelectionBackground(new Color(66, 133, 244));
        studentsTable.setSelectionForeground(Color.WHITE);
        studentsTable.getTableHeader().setBackground(darkerBg);
        studentsTable.getTableHeader().setForeground(lightText);
        
        // Update scroll pane
        scrollPane.getViewport().setBackground(darkerBg);
        scrollPane.setBackground(darkerBg);
        
        // Update status bar
        statusBar.setBackground(darkerBg);
        statusBar.setForeground(lightText);
        
        // Update search field
        searchField.setBackground(darkerBg);
        searchField.setForeground(lightText);
        searchField.setCaretColor(lightText);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 100)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        // Update labels
        for (Component comp : searchPanel.getComponents()) {
            if (comp instanceof JLabel) {
                ((JLabel) comp).setForeground(lightText);
            }
        }
        
        // Update borders
        topPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(accentColor), "Controls"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(accentColor), "Student Records", 0, 0, 
            new Font("Segoe UI", Font.BOLD, 12), accentColor));
    }
    
    private void applyLightTheme() {
        // Light theme colors
        Color lightBg = new Color(250, 250, 250);
        Color whiteBg = Color.WHITE;
        Color darkText = new Color(50, 50, 50);
        Color accentColor = new Color(52, 152, 219);
        
        // Set component colors
        mainPanel.setBackground(lightBg);
        topPanel.setBackground(whiteBg);
        buttonPanel.setBackground(whiteBg);
        searchPanel.setBackground(whiteBg);
        
        // Update table colors
        studentsTable.setBackground(whiteBg);
        studentsTable.setForeground(darkText);
        studentsTable.setSelectionBackground(new Color(52, 152, 219));
        studentsTable.setSelectionForeground(Color.WHITE);
        studentsTable.getTableHeader().setBackground(whiteBg);
        studentsTable.getTableHeader().setForeground(darkText);
        
        // Update scroll pane
        scrollPane.getViewport().setBackground(whiteBg);
        scrollPane.setBackground(whiteBg);
        
        // Update status bar
        statusBar.setBackground(whiteBg);
        statusBar.setForeground(darkText);
        
        // Update search field
        searchField.setBackground(whiteBg);
        searchField.setForeground(darkText);
        searchField.setCaretColor(darkText);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        // Update labels
        for (Component comp : searchPanel.getComponents()) {
            if (comp instanceof JLabel) {
                ((JLabel) comp).setForeground(darkText);
            }
        }
        
        // Update borders
        topPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(accentColor), "Controls"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(accentColor), "Student Records"));
    }
    
    private void createStudentsTable() {
        String[] columnNames = {"ID", "Name", "Email", "Phone", "10th %", "12th %", "Category", "Date Added"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        studentsTable = new JTable(tableModel);
        studentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentsTable.getTableHeader().setReorderingAllowed(false);
        studentsTable.setRowHeight(25);
        studentsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        studentsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        // Set column widths
        studentsTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        studentsTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Name
        studentsTable.getColumnModel().getColumn(2).setPreferredWidth(200); // Email
        studentsTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Phone
        studentsTable.getColumnModel().getColumn(4).setPreferredWidth(60);  // 10th
        studentsTable.getColumnModel().getColumn(5).setPreferredWidth(60);  // 12th
        studentsTable.getColumnModel().getColumn(6).setPreferredWidth(80);  // Category
        studentsTable.getColumnModel().getColumn(7).setPreferredWidth(100); // Date
    }
    
    private void refreshStudentList() {
        tableModel.setRowCount(0);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        try {
            List<SimpleStudent> students = studentDAO.getAllStudents();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

            for (SimpleStudent student : students) {
                Vector<Object> row = new Vector<>();
                row.add(student.getId());
                row.add(student.getFirstName() + " " + student.getLastName());
                row.add(student.getEmail());
                row.add(student.getPhone() != null ? student.getPhone() : "N/A");
                row.add(String.format("%.1f", student.getMarks10th()));
                row.add(String.format("%.1f", student.getMarks12th()));
                row.add(student.getCategory());
                row.add(student.getCreatedAt() != null ? dateFormat.format(student.getCreatedAt()) : "N/A");
                tableModel.addRow(row);
            }

            updateStatus("Loaded " + students.size() + " students");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading students: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
            updateStatus("Error loading students");
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }
    
    private void searchStudents() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            refreshStudentList();
            return;
        }
        
        tableModel.setRowCount(0);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        try {
            List<SimpleStudent> students = studentDAO.searchStudents(searchTerm);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            
            for (SimpleStudent student : students) {
                Vector<Object> row = new Vector<>();
                row.add(student.getId());
                row.add(student.getFirstName() + " " + student.getLastName());
                row.add(student.getEmail());
                row.add(student.getPhone() != null ? student.getPhone() : "N/A");
                row.add(String.format("%.1f", student.getMarks10th()));
                row.add(String.format("%.1f", student.getMarks12th()));
                row.add(student.getCategory());
                row.add(student.getCreatedAt() != null ? dateFormat.format(student.getCreatedAt()) : "N/A");
                tableModel.addRow(row);
            }
            
            updateStatus("Found " + students.size() + " students matching '" + searchTerm + "'");
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error searching students: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
            updateStatus("Error searching students");
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }
    
    private void showAddStudentDialog() {
        JDialog dialog = new JDialog(this, "Add New Student", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);
        
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Form fields
        JTextField firstNameField = new JTextField(20);
        JTextField lastNameField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JTextField phoneField = new JTextField(20);
        
        // Academic fields
        JSpinner marks10thSpinner = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 100.0, 0.1));
        JSpinner marks12thSpinner = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 100.0, 0.1));
        
        // Category and gender
        JComboBox<String> categoryBox = new JComboBox<>(new String[]{"GENERAL", "OBC", "SC", "ST", "OTHER"});
        JComboBox<String> genderBox = new JComboBox<>(new String[]{"M", "F", "O"});
        
        // Add form fields
        gbc.gridx = 0; gbc.gridy = 0;
        contentPanel.add(new JLabel("First Name:*"), gbc);
        gbc.gridx = 1;
        contentPanel.add(firstNameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        contentPanel.add(new JLabel("Last Name:*"), gbc);
        gbc.gridx = 1;
        contentPanel.add(lastNameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        contentPanel.add(new JLabel("Email:*"), gbc);
        gbc.gridx = 1;
        contentPanel.add(emailField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        contentPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        contentPanel.add(phoneField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        contentPanel.add(new JLabel("Gender:*"), gbc);
        gbc.gridx = 1;
        contentPanel.add(genderBox, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        contentPanel.add(new JLabel("Category:*"), gbc);
        gbc.gridx = 1;
        contentPanel.add(categoryBox, gbc);
        
        gbc.gridx = 0; gbc.gridy = 6;
        contentPanel.add(new JLabel("10th Marks (%):*"), gbc);
        gbc.gridx = 1;
        contentPanel.add(marks10thSpinner, gbc);
        
        gbc.gridx = 0; gbc.gridy = 7;
        contentPanel.add(new JLabel("12th Marks (%):*"), gbc);
        gbc.gridx = 1;
        contentPanel.add(marks12thSpinner, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton saveButton = new JButton("Save Student");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.setBackground(new Color(46, 204, 113));
        saveButton.setForeground(Color.WHITE);
        cancelButton.setBackground(new Color(149, 165, 166));
        cancelButton.setForeground(Color.WHITE);
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // Action listeners
        saveButton.addActionListener(e -> {
            try {
                // Validate required fields
                if (firstNameField.getText().trim().isEmpty() || 
                    lastNameField.getText().trim().isEmpty() || 
                    emailField.getText().trim().isEmpty()) {
                    throw new IllegalArgumentException("Please fill all required fields");
                }
                
                // Create student object
                SimpleStudent student = new SimpleStudent();
                
                // Set basic fields
                student.setFirstName(firstNameField.getText().trim());
                student.setLastName(lastNameField.getText().trim());
                student.setEmail(emailField.getText().trim());
                student.setPhone(phoneField.getText().trim());
                student.setGender(genderBox.getSelectedItem().toString());
                student.setCategory(categoryBox.getSelectedItem().toString());
                student.setMarks10th((Double) marks10thSpinner.getValue());
                student.setMarks12th((Double) marks12thSpinner.getValue());
                student.setNationality("Indian");
                student.setActive(true);
                
                // Basic validation
                if (student.getFirstName().isEmpty() || student.getLastName().isEmpty() || 
                    student.getEmail().isEmpty()) {
                    throw new IllegalArgumentException("Please fill in all required fields");
                }
                studentDAO.save(student);
                
                dialog.dispose();
                refreshStudentList();
                
                JOptionPane.showMessageDialog(this,
                    "Student added successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog,
                    ex.getMessage(),
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Error saving student: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void updateStatus(String message) {
        // Update status bar - implementation depends on how you want to show status
        System.out.println("Status: " + message);
    }
    
    public static void main(String[] args) {
        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Use default look and feel
        }
        
        // Create and show GUI
        SwingUtilities.invokeLater(() -> {
            SimpleGUI gui = new SimpleGUI();
            gui.setVisible(true);
        });
    }
}