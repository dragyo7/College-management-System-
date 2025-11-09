package main;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.Map;
import model.Student;
import dao.StudentDAO;
import service.AdmissionService;
import dao.CourseDAO;
import dao.DAOException;

// Custom DatePicker component
class DatePicker extends JPanel {
    private JTextField dateField;
    private JButton button;
    private JDialog dialog;
    private JPanel calendar;
    private int month = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH);
    private int year = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
    private JLabel l = new JLabel("", JLabel.CENTER);
    private String day = "";
    private JButton[] button_table = new JButton[49];
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    
    public DatePicker() {
        this.setLayout(new BorderLayout());
        dateField = new JTextField(10);
        button = new JButton("...");
        
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                showCalendarDialog();
            }
        });
        
        this.add(dateField, BorderLayout.CENTER);
        this.add(button, BorderLayout.EAST);
    }
    
    private void showCalendarDialog() {
        dialog = new JDialog();
        dialog.setModal(true);
        String[] header = { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };
        JPanel p1 = new JPanel(new GridLayout(7, 7));
        p1.setPreferredSize(new Dimension(430, 120));
        
        for (int x = 0; x < button_table.length; x++) {
            final int selection = x;
            button_table[x] = new JButton();
            button_table[x].setFocusPainted(false);
            button_table[x].setBackground(Color.white);
            if (x > 6)
                button_table[x].addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        day = button_table[selection].getActionCommand();
                        dateField.setText(year + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", Integer.parseInt(day)));
                        dialog.dispose();
                    }
                });
            if (x < 7) {
                button_table[x].setText(header[x]);
                button_table[x].setForeground(Color.red);
            }
            p1.add(button_table[x]);
        }
        JPanel p2 = new JPanel(new GridLayout(1, 3));
        JButton previous = new JButton("<< Previous");
        previous.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                month--;
                if (month < 0) {
                    month = 11;
                    year--;
                }
                displayCalendar();
            }
        });
        p2.add(previous);
        p2.add(l);
        JButton next = new JButton("Next >>");
        next.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                month++;
                if (month > 11) {
                    month = 0;
                    year++;
                }
                displayCalendar();
            }
        });
        p2.add(next);
        dialog.add(p1, BorderLayout.CENTER);
        dialog.add(p2, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(button);
        displayCalendar();
        dialog.setVisible(true);
    }
    
    private void displayCalendar() {
        for (int x = 7; x < button_table.length; x++)
            button_table[x].setText("");
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMMM yyyy");
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(year, month, 1);
        int dayOfWeek = cal.get(java.util.Calendar.DAY_OF_WEEK);
        int daysInMonth = cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);
        for (int x = 6 + dayOfWeek, day = 1; day <= daysInMonth; x++, day++)
            button_table[x].setText("" + day);
        l.setText(sdf.format(cal.getTime()));
        dialog.setTitle("Date Picker");
    }
    
    public Date getDate() {
        try {
            return sdf.parse(dateField.getText());
        } catch (ParseException e) {
            return null;
        }
    }
    
    public void setDate(Date date) {
        dateField.setText(sdf.format(date));
    }
}

public class MainGUI extends JFrame {
    private final StudentDAO studentDAO;
    private final CourseDAO courseDAO;
    private final AdmissionService admissionService;
    private JTable studentsTable;
    private DefaultTableModel tableModel;

    public MainGUI() {
        this.studentDAO = new StudentDAO();
        this.courseDAO = new CourseDAO();
        this.admissionService = new AdmissionService();

        setTitle("College Admission Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        // Create menu bar
        JMenuBar menuBar = new JMenuBar();
        
        // File Menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        
        JMenuItem exportItem = new JMenuItem("Export Merit List");
        exportItem.setMnemonic(KeyEvent.VK_E);
        exportItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK));
        exportItem.addActionListener(e -> exportMeritList());
        fileMenu.add(exportItem);
        
        fileMenu.addSeparator();
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setMnemonic(KeyEvent.VK_X);
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        
        // View Menu
        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic(KeyEvent.VK_V);
        
        JMenuItem statsItem = new JMenuItem("Show Statistics");
        statsItem.setMnemonic(KeyEvent.VK_S);
        statsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        statsItem.addActionListener(e -> showStatistics());
        viewMenu.add(statsItem);
        
        JMenuItem searchItem = new JMenuItem("Search Students");
        searchItem.setMnemonic(KeyEvent.VK_F);
        searchItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK));
        searchItem.addActionListener(e -> showSearchDialog());
        viewMenu.add(searchItem);

        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        setJMenuBar(menuBar);

        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Create button panel
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Student");
        JButton refreshButton = new JButton("Refresh List");
        JButton generateMeritList = new JButton("Generate Merit List");

        buttonPanel.add(addButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(generateMeritList);

        // Create table
        createStudentsTable();
        JScrollPane scrollPane = new JScrollPane(studentsTable);

        mainPanel.add(buttonPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Add action listeners
        addButton.addActionListener(e -> showAddStudentDialog());
        refreshButton.addActionListener(e -> refreshStudentList());
        generateMeritList.addActionListener(e -> generateAndShowMeritList());

        add(mainPanel);
        refreshStudentList();
    }

    private void createStudentsTable() {
        String[] columnNames = {"ID", "Name", "Email", "Category", "10th Marks", "12th Marks"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        studentsTable = new JTable(tableModel);
        studentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentsTable.getTableHeader().setReorderingAllowed(false);
    }

    private void refreshStudentList() {
        tableModel.setRowCount(0);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            List<Student> students = studentDAO.getAllStudents();
            if (students.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "No students found in the database.",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            for (Student student : students) {
                Vector<Object> row = new Vector<>();
                row.add(student.getId());
                row.add(student.getFirstName() + " " + student.getLastName());
                row.add(student.getEmail());
                row.add(student.getCategory());
                row.add(String.format("%.1f%%", student.getMarks10th()));
                row.add(String.format("%.1f%%", student.getMarks12th()));
                tableModel.addRow(row);
            }
            // Auto-resize columns after adding data
            for (int column = 0; column < studentsTable.getColumnCount(); column++) {
                int width = 80; // minimum width
                for (int row = 0; row < studentsTable.getRowCount(); row++) {
                    TableCellRenderer renderer = studentsTable.getCellRenderer(row, column);
                    Component comp = studentsTable.prepareRenderer(renderer, row, column);
                    width = Math.max(comp.getPreferredSize().width + 20, width);
                }
                studentsTable.getColumnModel().getColumn(column).setPreferredWidth(width);
            }
        } catch (DAOException e) {
            JOptionPane.showMessageDialog(this,
                "Error loading students: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    private void showAddStudentDialog() {
        JDialog dialog = new JDialog(this, "Add New Student", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(800, 600);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.weightx = 1.0;

        JPanel contentPanel = new JPanel(new GridBagLayout());
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        dialog.add(scrollPane, BorderLayout.CENTER);

        // Personal Information Section
        addSectionHeader(contentPanel, "Personal Information", gbc, 0);
        
        JTextField firstNameField = new JTextField(20);
        JTextField lastNameField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JTextField phoneField = new JTextField(20);
        JComboBox<String> genderBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        
        DatePicker dobPicker = new DatePicker();
        JComboBox<String> categoryBox = new JComboBox<>(new String[]{"General", "OBC", "SC", "ST"});
        
        addFormField(contentPanel, "First Name:", firstNameField, gbc, 1);
        addFormField(contentPanel, "Last Name:", lastNameField, gbc, 2);
        addFormField(contentPanel, "Email:", emailField, gbc, 3);
        addFormField(contentPanel, "Phone:", phoneField, gbc, 4);
        addFormField(contentPanel, "Gender:", genderBox, gbc, 5);
        addFormField(contentPanel, "Date of Birth:", dobPicker, gbc, 6);
        addFormField(contentPanel, "Category:", categoryBox, gbc, 7);

        // Address Section
        addSectionHeader(contentPanel, "Address Information", gbc, 8);
        
        JTextField addressField = new JTextField(20);
        JTextField cityField = new JTextField(20);
        JTextField stateField = new JTextField(20);
        JTextField postalCodeField = new JTextField(20);
        
        addFormField(contentPanel, "Address:", addressField, gbc, 9);
        addFormField(contentPanel, "City:", cityField, gbc, 10);
        addFormField(contentPanel, "State:", stateField, gbc, 11);
        addFormField(contentPanel, "Postal Code:", postalCodeField, gbc, 12);

        // Academic Information Section
        addSectionHeader(contentPanel, "Academic Information", gbc, 13);
        
        JSpinner marks10thSpinner = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 100.0, 0.1));
        JSpinner marks12thSpinner = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 100.0, 0.1));
        JTextField qualificationField = new JTextField(20);
        
        addFormField(contentPanel, "10th Marks (%):", marks10thSpinner, gbc, 14);
        addFormField(contentPanel, "12th Marks (%):", marks12thSpinner, gbc, 15);
        addFormField(contentPanel, "Previous Qualification:", qualificationField, gbc, 16);

        // Guardian Information Section
        addSectionHeader(contentPanel, "Guardian Information", gbc, 17);
        
        JTextField guardianNameField = new JTextField(20);
        JTextField guardianPhoneField = new JTextField(20);
        JTextField guardianEmailField = new JTextField(20);
        
        addFormField(contentPanel, "Guardian Name:", guardianNameField, gbc, 18);
        addFormField(contentPanel, "Guardian Phone:", guardianPhoneField, gbc, 19);
        addFormField(contentPanel, "Guardian Email:", guardianEmailField, gbc, 20);
        
        dialog.add(scrollPane);
        
        // Add buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(e -> {
            try {
                Student student = new Student(
                    firstNameField.getText(),
                    lastNameField.getText(),
                    emailField.getText()
                );
                
                // Set all fields
                student.setDateOfBirth(dobPicker.getDate());
                student.setGender(genderBox.getSelectedItem().toString());
                student.setPhone(phoneField.getText());
                student.setAddress(addressField.getText());
                student.setCity(cityField.getText());
                student.setState(stateField.getText());
                student.setPostalCode(postalCodeField.getText());
                student.setCategory(categoryBox.getSelectedItem().toString());
                student.setMarks10th((Double) marks10thSpinner.getValue());
                student.setMarks12th((Double) marks12thSpinner.getValue());
                student.setPreviousQualification(qualificationField.getText());
                student.setGuardianName(guardianNameField.getText());
                student.setGuardianPhone(guardianPhoneField.getText());
                student.setGuardianEmail(guardianEmailField.getText());
                
                student.validate();
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
                    JOptionPane.ERROR_MESSAGE);
            } catch (DAOException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Error saving student: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void generateAndShowMeritList() {
        try {
            admissionService.generateMeritList();
            JOptionPane.showMessageDialog(this,
                "Merit list has been generated successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error generating merit list: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportMeritList() {
        try {
            admissionService.generateMeritList();
            JOptionPane.showMessageDialog(this,
                "Merit list has been exported successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error exporting merit list: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showStatistics() {
        try {
            int totalStudents = studentDAO.getTotalStudents();
            Map<String, Integer> categoryStats = studentDAO.getStudentsByCategory();
            Map<String, Double> avgMarks = studentDAO.getAverageMarks();

            StringBuilder stats = new StringBuilder();
            stats.append("Student Statistics:\n\n");
            stats.append(String.format("Total Students: %d\n\n", totalStudents));
            
            stats.append("Distribution by Category:\n");
            for (Map.Entry<String, Integer> entry : categoryStats.entrySet()) {
                stats.append(String.format("%s: %d students\n", entry.getKey(), entry.getValue()));
            }
            stats.append("\n");
            
            stats.append("Average Marks:\n");
            stats.append(String.format("10th Grade: %.2f%%\n", avgMarks.get("avg_10th")));
            stats.append(String.format("12th Grade: %.2f%%", avgMarks.get("avg_12th")));

            JTextArea textArea = new JTextArea(stats.toString());
            textArea.setEditable(false);
            textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(400, 300));
            
            JOptionPane.showMessageDialog(this,
                scrollPane,
                "Student Statistics",
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (DAOException e) {
            JOptionPane.showMessageDialog(this,
                "Error retrieving statistics: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showSearchDialog() {
        JDialog searchDialog = new JDialog(this, "Search Students", true);
        searchDialog.setLayout(new BorderLayout(10, 10));

        // Create search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        // Create result table
        DefaultTableModel searchModel = new DefaultTableModel(
            new String[] {"ID", "Name", "Email", "Category", "Marks 10th", "Marks 12th"},
            0
        );
        JTable searchTable = new JTable(searchModel);
        JScrollPane tableScroll = new JScrollPane(searchTable);

        // Add components to dialog
        searchDialog.add(searchPanel, BorderLayout.NORTH);
        searchDialog.add(tableScroll, BorderLayout.CENTER);

        // Add search functionality
        searchButton.addActionListener(e -> {
            try {
                searchModel.setRowCount(0);
                String searchTerm = searchField.getText().trim();
                List<Student> results = studentDAO.searchStudents(searchTerm);
                
                for (Student student : results) {
                    searchModel.addRow(new Object[] {
                        student.getId(),
                        student.getFirstName() + " " + student.getLastName(),
                        student.getEmail(),
                        student.getCategory(),
                        student.getMarks10th(),
                        student.getMarks12th()
                    });
                }
                
                if (results.isEmpty()) {
                    JOptionPane.showMessageDialog(searchDialog,
                        "No students found matching your search criteria.",
                        "No Results",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (DAOException ex) {
                JOptionPane.showMessageDialog(searchDialog,
                    "Error searching students: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        // Add key listener for Enter key
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchButton.doClick();
                }
            }
        });

        searchDialog.setSize(600, 400);
        searchDialog.setLocationRelativeTo(this);
        searchDialog.setVisible(true);
    }

    private void addSectionHeader(JPanel panel, String title, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        JLabel header = new JLabel(title);
        header.setFont(header.getFont().deriveFont(Font.BOLD, 14));
        header.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        panel.add(header, gbc);
        gbc.gridwidth = 1;
    }

    private void addFormField(JPanel panel, String label, JComponent field, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(label), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(field, gbc);
        gbc.fill = GridBagConstraints.NONE;
    }

    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            MainGUI gui = new MainGUI();
            gui.setVisible(true);
        });
    }
}