package dao;

import java.sql.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import model.SimpleStudent;

/**
 * SimpleStudentDAO - Data Access Object for Student Management
 * 
 * This class handles all database operations related to student records including:
 * - Saving new student records
 * - Retrieving all students
 * - Searching students by name, email, or registration number
 * - Finding students by ID
 * 
 * Features:
 * - Automatic registration number generation
 * - String length validation to prevent database errors
 * - Comprehensive error handling with DAOException
 * - Connection pooling for optimal performance
 * 
 * Database columns mapped:
 * - Personal information (name, DOB, gender, contact details)
 * - Academic records (10th/12th marks, previous qualification)
 * - Guardian information
 * - System fields (registration number, timestamps, active status)
 * 
 * @author College Admission Management System
 * @version 1.0
 */
public class SimpleStudentDAO {
    
    // SQL Queries for database operations
    // Insert new student with all required fields and timestamps
    private static final String SQL_INSERT = "INSERT INTO Students (registration_no, first_name, last_name, date_of_birth, " +
            "gender, email, phone, address, city, state, postal_code, nationality, " +
            "category, marks_10th, marks_12th, previous_qualification, " +
            "guardian_name, guardian_phone, guardian_email, is_active, created_at, updated_at) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";

    // Select all students ordered by creation date (newest first)
    private static final String SQL_SELECT_ALL = "SELECT * FROM Students ORDER BY created_at DESC";
    
    // Select student by primary key
    private static final String SQL_SELECT_BY_ID = "SELECT * FROM Students WHERE student_id=?";
    
    // Search students by name, email, or registration number
    private static final String SQL_SEARCH = "SELECT * FROM Students WHERE " +
            "CONCAT(first_name, ' ', last_name) LIKE ? OR email LIKE ? OR registration_no LIKE ?";

    /**
     * Save a student to the database with comprehensive validation and error handling.
     * 
     * This method performs the following operations:
     * 1. Generates registration number if not provided
     * 2. Validates and truncates string fields to prevent database errors
     * 3. Sets default values for optional fields
     * 4. Executes the INSERT statement with all student data
     * 5. Retrieves and sets the auto-generated student ID
     * 
     * @param student The SimpleStudent object containing student information to be saved
     * @return The saved SimpleStudent object with generated ID and registration number
     * @throws DAOException if there's an error during database operation or validation fails
     */
    public SimpleStudent save(SimpleStudent student) throws DAOException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);

            // Generate registration number if not exists
            String regNo = student.getRegistrationNo();
            if (regNo == null || regNo.trim().isEmpty()) {
                regNo = generateRegistrationNumber();
                student.setRegistrationNo(regNo);
            }

            // Set parameters with length validation
            ps.setString(1, regNo);
            ps.setString(2, truncateString(student.getFirstName(), 50));
            ps.setString(3, truncateString(student.getLastName(), 50));
            ps.setDate(4, student.getDateOfBirth() != null ? 
                      new java.sql.Date(student.getDateOfBirth().getTime()) : 
                      new java.sql.Date(System.currentTimeMillis()));
            ps.setString(5, truncateString(student.getGender(), 1)); // M, F, or O
            ps.setString(6, truncateString(student.getEmail(), 100));
            ps.setString(7, truncateString(student.getPhone(), 20));
            ps.setString(8, truncateString(student.getAddress(), 65535)); // TEXT field
            ps.setString(9, truncateString(student.getCity(), 50));
            ps.setString(10, truncateString(student.getState(), 50));
            ps.setString(11, truncateString(student.getPostalCode(), 10));
            ps.setString(12, truncateString(student.getNationality(), 50));
            ps.setString(13, truncateString(student.getCategory(), 20)); // GENERAL/SC/ST/OBC/OTHER
            ps.setDouble(14, Math.max(0.0, Math.min(100.0, student.getMarks10th())));
            ps.setDouble(15, Math.max(0.0, Math.min(100.0, student.getMarks12th())));
            ps.setString(16, truncateString(student.getPreviousQualification(), 100));
            ps.setString(17, truncateString(student.getGuardianName(), 100));
            ps.setString(18, truncateString(student.getGuardianPhone(), 20));
            ps.setString(19, truncateString(student.getGuardianEmail(), 100));
            ps.setBoolean(20, student.isActive());

            ps.executeUpdate();

            // Get generated ID
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                student.setId(rs.getInt(1));
            }

            return student;

        } catch (SQLException e) {
            throw new DAOException("Error saving student: " + e.getMessage(), e);
        } finally {
            DBConnection.closeAll(conn, ps, rs);
        }
    }

    /**
     * Get all students
     */
    public List<SimpleStudent> getAllStudents() throws DAOException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<SimpleStudent> students = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(SQL_SELECT_ALL);
            rs = ps.executeQuery();

            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error getting all students: " + e.getMessage(), e);
        } finally {
            DBConnection.closeAll(conn, ps, rs);
        }

        return students;
    }

    /**
     * Search students by name, email, or registration number
     */
    public List<SimpleStudent> searchStudents(String query) throws DAOException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<SimpleStudent> students = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(SQL_SEARCH);
            String likeQuery = "%" + query + "%";
            ps.setString(1, likeQuery);
            ps.setString(2, likeQuery);
            ps.setString(3, likeQuery);
            
            rs = ps.executeQuery();
            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error searching students: " + e.getMessage(), e);
        } finally {
            DBConnection.closeAll(conn, ps, rs);
        }

        return students;
    }

    /**
     * Find student by ID
     */
    public SimpleStudent findById(int id) throws DAOException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(SQL_SELECT_BY_ID);
            ps.setInt(1, id);
            
            rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToStudent(rs);
            }
            return null;

        } catch (SQLException e) {
            throw new DAOException("Error finding student: " + e.getMessage(), e);
        } finally {
            DBConnection.closeAll(conn, ps, rs);
        }
    }

    /**
     * Truncate string to specified length to prevent database errors.
     * 
     * This utility method ensures that string values don't exceed the maximum
     * length allowed by database columns, preventing SQL exceptions and data loss.
     * 
     * @param value The input string to be truncated
     * @param maxLength The maximum allowed length for the string
     * @return The original string if it's within limits, or truncated version
     */
    private String truncateString(String value, int maxLength) {
        if (value == null) return null;
        return value.length() > maxLength ? value.substring(0, maxLength) : value;
    }

    /**
     * Map ResultSet to Student object
     */
    private SimpleStudent mapResultSetToStudent(ResultSet rs) throws SQLException {
        SimpleStudent student = new SimpleStudent();
        
        student.setId(rs.getInt("student_id"));
        student.setRegistrationNo(rs.getString("registration_no"));
        student.setFirstName(rs.getString("first_name"));
        student.setLastName(rs.getString("last_name"));
        student.setDateOfBirth(rs.getDate("date_of_birth"));
        student.setGender(rs.getString("gender"));
        student.setEmail(rs.getString("email"));
        student.setPhone(rs.getString("phone"));
        student.setAddress(rs.getString("address"));
        student.setCity(rs.getString("city"));
        student.setState(rs.getString("state"));
        student.setPostalCode(rs.getString("postal_code"));
        student.setNationality(rs.getString("nationality"));
        student.setCategory(rs.getString("category"));
        student.setMarks10th(rs.getDouble("marks_10th"));
        student.setMarks12th(rs.getDouble("marks_12th"));
        student.setPreviousQualification(rs.getString("previous_qualification"));
        student.setGuardianName(rs.getString("guardian_name"));
        student.setGuardianPhone(rs.getString("guardian_phone"));
        student.setGuardianEmail(rs.getString("guardian_email"));
        student.setActive(rs.getBoolean("is_active"));
        student.setCreatedAt(rs.getTimestamp("created_at"));
        student.setUpdatedAt(rs.getTimestamp("updated_at"));
        
        return student;
    }

    /**
     * Generate a unique registration number
     */
    private String generateRegistrationNumber() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String date = sdf.format(new java.util.Date());
        String random = String.valueOf((int)(Math.random() * 1000));
        return "REG" + date + random;
    }
}