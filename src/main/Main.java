package main;

import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import model.Student;
import dao.StudentDAO;
import dao.CourseDAO;
import model.Course;
import service.AdmissionService;
import javax.swing.SwingUtilities;

/**
 * Main - Entry point for the College Admission Management System
 * 
 * This class serves as the primary launcher for the application, providing
 * two modes of operation:
 * 
 * 1. GUI Mode (Default): Launches the modern Swing-based graphical interface
 *    - User-friendly forms for student registration
 *    - Visual data tables for viewing and searching students
 *    - Professional dark/light theme support
 * 
 * 2. Console Mode: Command-line interface for terminal-based operations
 *    - Text-based menus for all operations
 *    - Suitable for server environments or quick operations
 *    - Full student registration and management capabilities
 * 
 * Usage:
 * - GUI Mode: java main.Main
 * - Console Mode: java main.Main --console
 * 
 * Features:
 * - Student registration with comprehensive validation
 * - Student search and filtering
 * - Course management
 * - Merit list generation
 * - Data persistence with MySQL database
 * 
 * @author College Admission Management System
 * @version 1.0
 */
public class Main {
    // Date formatter for consistent date parsing in console mode
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Main entry point for the application
     * 
     * @param args Command line arguments. Use "--console" for console mode
     */
    public static void main(String[] args) {"explanation":"Added comprehensive class-level Javadoc and enhanced main method documentation"}
        // Check for command line arguments
        if (args.length > 0 && args[0].equals("--console")) {
            runConsoleMode();
        } else {
            // Default to GUI mode
            SwingUtilities.invokeLater(() -> new MainGUI().setVisible(true));
        }
    }

/**
     * Runs the console-based interface for the application.
     * 
     * Provides a text-based menu system with the following options:
     * 1. Register Student - Complete student registration with validation
     * 2. View All Students - Display all registered students
     * 3. View Active Students - Display only active students
     * 4. Add Course - Add new course to the system
     * 5. View All Courses - Display all available courses
     * 6. Generate Merit List - Create merit list based on academic performance
     * 7. Exit - Safely exit the application
     * 
     * Features comprehensive error handling and user-friendly prompts.
     */
    private static void runConsoleMode() {
        Scanner sc = new Scanner(System.in);  // Scanner for user input
        StudentDAO studentDAO = new StudentDAO();  // Data access for student operations
        CourseDAO courseDAO = new CourseDAO();  // Data access for course operations
        AdmissionService service = new AdmissionService();  // Service for admission processes

        while (true) {
            System.out.println("\n===== 🎓 College Admission Management System =====");
            System.out.println("1. Register Student");
            System.out.println("2. View All Students");
            System.out.println("3. View Active Students");
            System.out.println("4. Add Course");
            System.out.println("5. View All Courses");
            System.out.println("6. Generate Merit List");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");
            
            try {
                int choice = Integer.parseInt(sc.nextLine().trim());

                switch (choice) {
                    case 1:
                        registerStudent(sc, studentDAO);
                        break;

                    case 2:
                        displayAllStudents(studentDAO);
                        break;

                    case 3:
                        displayActiveStudents(studentDAO);
                        break;

                    case 4:
                        addCourse(sc, courseDAO);
                        break;

                    case 5:
                        displayAllCourses(courseDAO);
                        break;

                    case 6:
                        service.generateMeritList();
                        System.out.println("✅ Merit list generated successfully!");
                        break;

                    case 7:
                        System.out.println("👋 Exiting program...");
                        sc.close();
                        return;

                    default:
                        System.out.println("❌ Invalid choice!");
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Please enter a valid number!");
            } catch (Exception e) {
                System.out.println("❌ Error: " + e.getMessage());
            }
        }
    }

    private static void registerStudent(Scanner sc, StudentDAO studentDAO) {
        try {
            System.out.print("Enter first name: ");
            String firstName = sc.nextLine().trim();
            
            System.out.print("Enter last name: ");
            String lastName = sc.nextLine().trim();
            
            System.out.print("Enter email: ");
            String email = sc.nextLine().trim();
            
            Student student = new Student(firstName, lastName, email);
            
            System.out.print("Enter date of birth (dd/MM/yyyy): ");
            String dobStr = sc.nextLine().trim();
            try {
                LocalDate dob = LocalDate.parse(dobStr, DATE_FORMATTER);
                student.setDateOfBirth(new Date(dob.toEpochDay() * 24 * 60 * 60 * 1000));
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid date format. Please use dd/MM/yyyy");
            }

            System.out.print("Enter gender (M/F/O): ");
            String gender = sc.nextLine().trim().toUpperCase();
            if (!gender.matches("^[MFO]$")) {
                throw new IllegalArgumentException("Gender must be M, F, or O");
            }
            student.setGender(gender);

            System.out.print("Enter phone: ");
            student.setPhone(sc.nextLine().trim());

            System.out.print("Enter address: ");
            student.setAddress(sc.nextLine().trim());

            System.out.print("Enter city: ");
            student.setCity(sc.nextLine().trim());

            System.out.print("Enter state: ");
            student.setState(sc.nextLine().trim());

            System.out.print("Enter postal code: ");
            student.setPostalCode(sc.nextLine().trim());

            System.out.print("Enter category (GEN/OBC/SC/ST): ");
            student.setCategory(sc.nextLine().trim().toUpperCase());

            System.out.print("Enter 10th marks (percentage): ");
            student.setMarks10th(Double.parseDouble(sc.nextLine().trim()));

            System.out.print("Enter 12th marks (percentage): ");
            student.setMarks12th(Double.parseDouble(sc.nextLine().trim()));

            System.out.print("Enter previous qualification: ");
            student.setPreviousQualification(sc.nextLine().trim());

            System.out.print("Enter guardian name: ");
            student.setGuardianName(sc.nextLine().trim());

            System.out.print("Enter guardian phone: ");
            student.setGuardianPhone(sc.nextLine().trim());

            System.out.print("Enter guardian email: ");
            student.setGuardianEmail(sc.nextLine().trim());

            // Validate all fields
            student.validate();

            // Save to database
            studentDAO.save(student);
            System.out.println("✅ Student registered successfully!");

        } catch (IllegalArgumentException e) {
            System.out.println("❌ " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ An error occurred: " + e.getMessage());
        }
    }

    private static void displayAllStudents(StudentDAO studentDAO) {
        List<Student> students = studentDAO.getAllStudents();
        if (students.isEmpty()) {
            System.out.println("No students registered yet.");
            return;
        }
        
        System.out.println("\nAll Students:");
        System.out.println("-------------");
        for (Student st : students) {
            displayStudentDetails(st);
        }
    }

    private static void displayActiveStudents(StudentDAO studentDAO) {
        List<Student> students = studentDAO.findByActiveStatus(true);
        if (students.isEmpty()) {
            System.out.println("No active students found.");
            return;
        }
        
        System.out.println("\nActive Students:");
        System.out.println("----------------");
        for (Student st : students) {
            displayStudentDetails(st);
        }
    }

    private static void displayStudentDetails(Student st) {
        System.out.println("\nStudent ID: " + st.getId());
        if (st.getRegistrationNo() != null) {
            System.out.println("Registration No: " + st.getRegistrationNo());
        }
        System.out.println("Name: " + st.getFirstName() + " " + st.getLastName());
        System.out.println("Email: " + st.getEmail());
        System.out.println("Phone: " + st.getPhone());
        System.out.println("Address: " + st.getAddress());
        System.out.println("City: " + st.getCity() + ", " + st.getState() + " - " + st.getPostalCode());
        System.out.println("Category: " + st.getCategory());
        System.out.println("Academic Details:");
        System.out.println("  10th Marks: " + st.getMarks10th() + "%");
        System.out.println("  12th Marks: " + st.getMarks12th() + "%");
        System.out.println("  Previous Qualification: " + st.getPreviousQualification());
        System.out.println("Guardian Details:");
        System.out.println("  Name: " + st.getGuardianName());
        System.out.println("  Phone: " + st.getGuardianPhone());
        System.out.println("  Email: " + st.getGuardianEmail());
        System.out.println("Status: " + (st.isActive() ? "Active" : "Inactive"));
    }

    private static void addCourse(Scanner sc, CourseDAO courseDAO) {
        try {
            Course c = new Course();
            
            System.out.print("Enter course name: ");
            String name = sc.nextLine().trim();
            c.setName(name);

            System.out.print("Enter course code (e.g., CS101): ");
            String code = sc.nextLine().trim();
            c.setCode(code);

            System.out.print("Enter cutoff marks (0-100): ");
            try {
                int cutoff = Integer.parseInt(sc.nextLine().trim());
                c.setCutoff(cutoff);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid cutoff marks format");
            }

            System.out.print("Enter maximum seats: ");
            try {
                int maxSeats = Integer.parseInt(sc.nextLine().trim());
                c.setMaxSeats(maxSeats);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number of seats");
            }

            System.out.print("Enter duration (in semesters): ");
            try {
                int duration = Integer.parseInt(sc.nextLine().trim());
                c.setDuration(duration);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid duration format");
            }

            System.out.print("Enter course description: ");
            String description = sc.nextLine().trim();
            c.setDescription(description);

            System.out.print("Enter course fees: ");
            try {
                double fees = Double.parseDouble(sc.nextLine().trim());
                c.setFees(fees);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid fees format");
            }

            System.out.print("Enter department name: ");
            String department = sc.nextLine().trim();
            c.setDepartment(department);

            // Validate all fields at once
            c.validate();

            // Save to database
            courseDAO.save(c);
            System.out.println("\n✅ Course added successfully!\n");
            System.out.println(c.toString());

        } catch (IllegalArgumentException e) {
            System.out.println("❌ " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ An error occurred: " + e.getMessage());
        }
    }

    private static void displayAllCourses(CourseDAO courseDAO) {
        List<Course> courses = courseDAO.getAllCourses();
        if (courses.isEmpty()) {
            System.out.println("No courses available yet.");
            return;
        }
        
        System.out.println("\nAvailable Courses:");
        System.out.println("-----------------");
        for (Course course : courses) {
            System.out.println(course);
        }
    }
}
