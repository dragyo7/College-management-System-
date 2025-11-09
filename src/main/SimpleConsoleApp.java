package main;

import dao.SimpleStudentDAO;
import dao.DAOException;
import model.SimpleStudent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.sql.SQLException;

/**
 * Simplified Console Application for College Admission System
 */
public class SimpleConsoleApp {
    
    private final SimpleStudentDAO studentDAO;
    private final Scanner scanner;
    private final SimpleDateFormat dateFormat;
    
    public SimpleConsoleApp() {
        this.studentDAO = new SimpleStudentDAO();
        this.scanner = new Scanner(System.in);
        this.dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    }
    
    public void start() {
        System.out.println("🎓 College Admission Management System");
        System.out.println("=====================================");
        
        while (true) {
            showMenu();
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                processChoice(choice);
            } catch (NumberFormatException e) {
                System.out.println("❌ Please enter a valid number!");
            } catch (Exception e) {
                System.out.println("❌ Error: " + e.getMessage());
            }
        }
    }
    
    private void showMenu() {
        System.out.println("\n📋 Main Menu:");
        System.out.println("1. View All Students");
        System.out.println("2. Add New Student");
        System.out.println("3. Search Students");
        System.out.println("4. Exit");
        System.out.print("Enter your choice: ");
    }
    
    private void processChoice(int choice) {
        switch (choice) {
            case 1:
                viewAllStudents();
                break;
            case 2:
                addNewStudent();
                break;
            case 3:
                searchStudents();
                break;
            case 4:
                System.out.println("👋 Goodbye!");
                System.exit(0);
                break;
            default:
                System.out.println("❌ Invalid choice! Please try again.");
        }
    }
    
    private void viewAllStudents() {
        try {
            List<SimpleStudent> students = studentDAO.getAllStudents();
            
            if (students.isEmpty()) {
                System.out.println("No students found!");
                return;
            }
            
            System.out.println("\n📚 Student List:");
            System.out.println(String.format("%-5s %-12s %-20s %-15s %-15s %-10s %-10s", 
                "ID", "Reg No", "Name", "Email", "Phone", "10th %", "12th %"));
            System.out.println("-".repeat(95));
            
            for (SimpleStudent student : students) {
                String name = student.getFirstName() + " " + student.getLastName();
                System.out.println(String.format("%-5d %-12s %-20s %-15s %-15s %-10.1f %-10.1f",
                    student.getId(),
                    student.getRegistrationNo(),
                    name.length() > 20 ? name.substring(0, 17) + "..." : name,
                    student.getEmail().length() > 15 ? student.getEmail().substring(0, 12) + "..." : student.getEmail(),
                    student.getPhone().length() > 15 ? student.getPhone().substring(0, 12) + "..." : student.getPhone(),
                    student.getMarks10th(),
                    student.getMarks12th()
                ));
            }
            
            System.out.println("Total students: " + students.size());
            
        } catch (DAOException e) {
            System.out.println("❌ Error loading students: " + e.getMessage());
        }
    }
    
    private void addNewStudent() {
        System.out.println("\n📝 Add New Student");
        System.out.println("=================");
        
        try {
            SimpleStudent student = new SimpleStudent();
            
            System.out.print("First Name (required): ");
            String firstName = scanner.nextLine().trim();
            if (firstName.isEmpty()) {
                throw new IllegalArgumentException("First name is required!");
            }
            student.setFirstName(firstName);
            
            System.out.print("Last Name (required): ");
            String lastName = scanner.nextLine().trim();
            if (lastName.isEmpty()) {
                throw new IllegalArgumentException("Last name is required!");
            }
            student.setLastName(lastName);
            
            System.out.print("Email (required): ");
            String email = scanner.nextLine().trim();
            if (email.isEmpty()) {
                throw new IllegalArgumentException("Email is required!");
            }
            student.setEmail(email);
            
            System.out.print("Phone (optional): ");
            String phone = scanner.nextLine().trim();
            student.setPhone(phone.isEmpty() ? "N/A" : phone);
            
            System.out.print("Gender (M/F/O, optional): ");
            String genderInput = scanner.nextLine().trim().toUpperCase();
            String gender;
            switch (genderInput) {
                case "M":
                case "MALE":
                    gender = "M";
                    break;
                case "F":
                case "FEMALE":
                    gender = "F";
                    break;
                case "O":
                case "OTHER":
                    gender = "O";
                    break;
                default:
                    gender = "O"; // Default to Other if invalid input
            }
            student.setGender(gender);
            
            System.out.print("Address (optional): ");
            String address = scanner.nextLine().trim();
            student.setAddress(address.isEmpty() ? "N/A" : address);
            
            System.out.print("City (optional): ");
            String city = scanner.nextLine().trim();
            student.setCity(city.isEmpty() ? "N/A" : city);
            
            System.out.print("State (optional): ");
            String state = scanner.nextLine().trim();
            student.setState(state.isEmpty() ? "N/A" : state);
            
            System.out.print("10th Marks (%) (optional, 0-100): ");
            String marks10Str = scanner.nextLine().trim();
            try {
                double marks10 = marks10Str.isEmpty() ? 0.0 : Double.parseDouble(marks10Str);
                student.setMarks10th(Math.max(0.0, Math.min(100.0, marks10)));
            } catch (NumberFormatException e) {
                student.setMarks10th(0.0);
            }
            
            System.out.print("12th Marks (%) (optional, 0-100): ");
            String marks12Str = scanner.nextLine().trim();
            try {
                double marks12 = marks12Str.isEmpty() ? 0.0 : Double.parseDouble(marks12Str);
                student.setMarks12th(Math.max(0.0, Math.min(100.0, marks12)));
            } catch (NumberFormatException e) {
                student.setMarks12th(0.0);
            }
            
            System.out.print("Category (General/OBC/SC/ST, optional): ");
            String category = scanner.nextLine().trim();
            student.setCategory(category.isEmpty() ? "General" : category);
            
            System.out.print("Nationality (optional): ");
            String nationality = scanner.nextLine().trim();
            student.setNationality(nationality.isEmpty() ? "Indian" : nationality);
            
            System.out.print("Date of Birth (YYYY-MM-DD, optional): ");
            String dobStr = scanner.nextLine().trim();
            try {
                student.setDateOfBirth(dobStr.isEmpty() ? 
                    new java.sql.Date(System.currentTimeMillis()) : 
                    java.sql.Date.valueOf(dobStr));
            } catch (IllegalArgumentException e) {
                student.setDateOfBirth(new java.sql.Date(System.currentTimeMillis()));
            }
            
            System.out.print("Guardian Name (optional): ");
            String guardianName = scanner.nextLine().trim();
            student.setGuardianName(guardianName.isEmpty() ? "N/A" : guardianName);
            
            System.out.print("Guardian Phone (optional): ");
            String guardianPhone = scanner.nextLine().trim();
            student.setGuardianPhone(guardianPhone.isEmpty() ? "N/A" : guardianPhone);
            
            System.out.print("Guardian Email (optional): ");
            String guardianEmail = scanner.nextLine().trim();
            student.setGuardianEmail(guardianEmail.isEmpty() ? "N/A" : guardianEmail);
            
            System.out.print("Postal Code (optional): ");
            String postalCode = scanner.nextLine().trim();
            student.setPostalCode(postalCode.isEmpty() ? "N/A" : postalCode);
            
            System.out.print("Previous Qualification (optional): ");
            String prevQual = scanner.nextLine().trim();
            student.setPreviousQualification(prevQual.isEmpty() ? "N/A" : prevQual);
            
            // Set defaults for other required fields
            student.setActive(true);
            
            // Save student
            SimpleStudent savedStudent = studentDAO.save(student);
            
            System.out.println("✅ Student added successfully!");
            System.out.println("Registration Number: " + savedStudent.getRegistrationNo());
            System.out.println("Name: " + savedStudent.getFirstName() + " " + savedStudent.getLastName());
            
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Input Error: " + e.getMessage());
        } catch (DAOException e) {
            System.out.println("❌ Database Error: " + e.getMessage());
            if (e.getMessage().contains("Data truncated")) {
                System.out.println("💡 Hint: Check that all text fields are not too long for the database.");
            }
        } catch (Exception e) {
            System.out.println("❌ Unexpected Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void searchStudents() {
        System.out.print("\n🔍 Enter search term (name, email, or registration number): ");
        String query = scanner.nextLine().trim();
        
        if (query.isEmpty()) {
            System.out.println("❌ Please enter a search term!");
            return;
        }
        
        try {
            List<SimpleStudent> students = studentDAO.searchStudents(query);
            
            if (students.isEmpty()) {
                System.out.println("No students found matching '" + query + "'");
                return;
            }
            
            System.out.println("\n📋 Search Results:");
            System.out.println(String.format("%-5s %-12s %-20s %-15s %-15s %-10s %-10s", 
                "ID", "Reg No", "Name", "Email", "Phone", "10th %", "12th %"));
            System.out.println("-".repeat(95));
            
            for (SimpleStudent student : students) {
                String name = student.getFirstName() + " " + student.getLastName();
                System.out.println(String.format("%-5d %-12s %-20s %-15s %-15s %-10.1f %-10.1f",
                    student.getId(),
                    student.getRegistrationNo(),
                    name.length() > 20 ? name.substring(0, 17) + "..." : name,
                    student.getEmail().length() > 15 ? student.getEmail().substring(0, 12) + "..." : student.getEmail(),
                    student.getPhone().length() > 15 ? student.getPhone().substring(0, 12) + "..." : student.getPhone(),
                    student.getMarks10th(),
                    student.getMarks12th()
                ));
            }
            
            System.out.println("Found " + students.size() + " students");
            
        } catch (DAOException e) {
            System.out.println("❌ Error searching students: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        System.out.println("Starting College Admission System...");
        
        try {
            // Test database connection
            SimpleStudentDAO testDAO = new SimpleStudentDAO();
            testDAO.getAllStudents(); // This will throw exception if connection fails
            
            System.out.println("✅ Database connection successful!");
            
            SimpleConsoleApp app = new SimpleConsoleApp();
            app.start();
            
        } catch (Exception e) {
            System.out.println("❌ Failed to start application: " + e.getMessage());
            System.out.println("Please check your database configuration.");
            System.exit(1);
        }
    }
}