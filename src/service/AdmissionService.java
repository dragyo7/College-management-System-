package service;

import java.io.*;
import java.util.*;
import model.Student;
import model.Course;
import dao.StudentDAO;

public class AdmissionService {
    private static final String MERIT_LIST_FILE = "merit_list.csv";
    private static final String BACKUP_FOLDER = "backups";
    private StudentDAO studentDAO;
    
    public AdmissionService() {
        this.studentDAO = new StudentDAO();
        // Create backups folder if it doesn't exist
        new File(BACKUP_FOLDER).mkdirs();
    }

    public void generateMeritList() throws IOException {
        // Get all students and sort by marks (descending)
        List<Student> students = studentDAO.getAllStudents();
        if (students.isEmpty()) {
            throw new IllegalStateException("No students found in the database");
        }

        // Sort by 12th marks and category priority
        students.sort((a, b) -> {
            int marksDiff = Double.compare(b.getMarks12th(), a.getMarks12th());
            if (marksDiff != 0) return marksDiff;
            
            // If marks are equal, sort by category priority
            return getCategoryPriority(a.getCategory()) - getCategoryPriority(b.getCategory());
        });

        // Create backup of existing merit list if it exists
        File meritFile = new File(MERIT_LIST_FILE);
        if (meritFile.exists()) {
            String timestamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File backupFile = new File(BACKUP_FOLDER, "merit_list_" + timestamp + ".csv");
            if (!meritFile.renameTo(backupFile)) {
                System.out.println("⚠️ Warning: Could not backup existing merit list");
            }
        }

        // Write sorted list to CSV
        try (PrintWriter writer = new PrintWriter(new FileWriter(MERIT_LIST_FILE))) {
            writer.println("Rank,Name,Email,12th Marks,Category,Previous Qualification,Status");
            int rank = 1;
            for (Student s : students) {
                writer.println(String.format("%d,%s %s,%s,%.2f,%s,%s,%s",
                    rank++,
                    s.getFirstName(),
                    s.getLastName(),
                    s.getEmail(),
                    s.getMarks12th(),
                    s.getCategory(),
                    s.getPreviousQualification(),
                    getAdmissionStatus(s)
                ));
            }
            System.out.println("✅ Merit list generated successfully at " + MERIT_LIST_FILE);
        } catch (IOException e) {
            System.err.println("❌ Error generating merit list: " + e.getMessage());
            throw e;
        }
    }

    private String getAdmissionStatus(Student s) {
        if (s.getMarks12th() >= 90) return "Direct Admission";
        if (s.getMarks12th() >= 75) return "Interview Required";
        if (s.getMarks12th() >= 60) return "Waiting List";
        return "Not Eligible";
    }

    private int getCategoryPriority(String category) {
        switch (category.toUpperCase()) {
            case "SC": return 1;
            case "ST": return 2;
            case "OBC": return 3;
            case "GEN": default: return 4;
        }
    }
}
