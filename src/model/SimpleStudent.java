package model;

import java.util.Date;

/**
 * SimpleStudent - Model class representing a college admission applicant
 * 
 * This class encapsulates all information related to a student applying for college admission,
 * including personal details, academic records, contact information, and guardian details.
 * 
 * Key Features:
 * - Comprehensive student profile with 22 data fields
 * - Automatic activation status (defaults to true)
 * - Support for academic records (10th and 12th marks)
 * - Guardian contact information for communication
 * - Timestamps for audit trail (created_at, updated_at)
 * 
 * Database Mapping:
 * - Maps to 'Students' table in MySQL database
 * - Primary key: student_id (auto-increment)
 * - Registration number: Unique identifier for each student
 * 
 * Validation:
 * - Gender: M/F/O (Male/Female/Other)
 * - Category: GENERAL/SC/ST/OBC/OTHER
 * - Marks: Validated between 0-100%
 * - Email: Valid email format required
 * 
 * @author College Admission Management System
 * @version 1.0
 */
public class SimpleStudent {
    // Core identification fields
    private int id;                    // Primary key (auto-increment)
    private String registrationNo;     // Unique registration number (auto-generated)
    
    // Personal information
    private String firstName;          // Student's first name (max 50 chars)
    private String lastName;           // Student's last name (max 50 chars)
    private Date dateOfBirth;          // Date of birth for age verification
    private String gender;             // Gender: M/F/O (Male/Female/Other)
    
    // Contact information
    private String email;              // Primary email address (max 100 chars)
    private String phone;              // Primary phone number (max 20 chars)
    private String address;            // Full address (TEXT field)
    private String city;               // City name (max 50 chars)
    private String state;              // State name (max 50 chars)
    private String postalCode;         // Postal/ZIP code (max 10 chars)
    private String nationality;        // Nationality (max 50 chars)
    
    // Academic information
    private String category;           // Category: GENERAL/SC/ST/OBC/OTHER (max 20 chars)
    private double marks10th;          // 10th grade percentage (0-100%)
    private double marks12th;          // 12th grade percentage (0-100%)
    private String previousQualification; // Previous academic qualification (max 100 chars)
    
    // Guardian information
    private String guardianName;       // Guardian's full name (max 100 chars)
    private String guardianPhone;      // Guardian's phone number (max 20 chars)
    private String guardianEmail;      // Guardian's email (max 100 chars)
    
    // System fields
    private boolean isActive;          // Active status (default: true)
    private Date createdAt;            // Record creation timestamp
    private Date updatedAt;            // Last update timestamp

    /**
     * Default constructor - initializes a new student with active status.
     * Sets isActive to true by default for new student records.
     */
    public SimpleStudent() {
        this.isActive = true;
    }

    /**
     * Convenience constructor for quick student creation with essential information.
     * 
     * @param firstName Student's first name
     * @param lastName Student's last name  
     * @param email Student's email address
     * @param phone Student's phone number
     */
    public SimpleStudent(String firstName, String lastName, String email, String phone) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRegistrationNo() {
        return registrationNo;
    }

    public void setRegistrationNo(String registrationNo) {
        this.registrationNo = registrationNo;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getMarks10th() {
        return marks10th;
    }

    public void setMarks10th(double marks10th) {
        this.marks10th = marks10th;
    }

    public double getMarks12th() {
        return marks12th;
    }

    public void setMarks12th(double marks12th) {
        this.marks12th = marks12th;
    }

    public String getPreviousQualification() {
        return previousQualification;
    }

    public void setPreviousQualification(String previousQualification) {
        this.previousQualification = previousQualification;
    }

    public String getGuardianName() {
        return guardianName;
    }

    public void setGuardianName(String guardianName) {
        this.guardianName = guardianName;
    }

    public String getGuardianPhone() {
        return guardianPhone;
    }

    public void setGuardianPhone(String guardianPhone) {
        this.guardianPhone = guardianPhone;
    }

    public String getGuardianEmail() {
        return guardianEmail;
    }

    public void setGuardianEmail(String guardianEmail) {
        this.guardianEmail = guardianEmail;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return String.format("%s %s (%s)", firstName, lastName, registrationNo);
    }
}