package model;

import java.util.Date;
import java.util.regex.Pattern;

public class Student {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z\\s-']{2,50}$");
    private static final Pattern POSTAL_CODE_PATTERN = Pattern.compile("^\\d{6}$");
    private static final Pattern REG_NO_PATTERN = Pattern.compile("^\\d{4}[A-Z]{2}\\d{4}$");

    private int id;
    private String registrationNo;
    private String firstName;
    private String lastName;
    private Date dateOfBirth;
    private String gender;
    private String email;
    private String phone;
    private String address;
    private String city;
    private String state;
    private String postalCode;
    private String nationality;
    private String category;
    private double marks10th;
    private double marks12th;
    private String previousQualification;
    private String guardianName;
    private String guardianPhone;
    private String guardianEmail;
    private boolean isActive;
    private Date createdAt;
    private Date updatedAt;

    // Constructors
    public Student() {
        this.isActive = true;
        this.nationality = "Indian";
        this.createdAt = new Date();
        this.updatedAt = this.createdAt;
    }

    public Student(String firstName, String lastName, String email) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public void validate() {
        validateRegistrationNo();
        validateName();
        validateEmail();
        validatePhone();
        validateMarks();
        validateBasicFields();
    }

    private void validateRegistrationNo() {
        if (registrationNo != null && !registrationNo.isEmpty() && !REG_NO_PATTERN.matcher(registrationNo).matches()) {
            throw new IllegalArgumentException("Invalid registration number. Format: YYYYXXNNNN (YYYY=year, XX=department code, NNNN=number)");
        }
    }

    private void validateName() {
        if (firstName == null || !NAME_PATTERN.matcher(firstName).matches()) {
            throw new IllegalArgumentException("Invalid first name. Must be 2-50 characters, letters only.");
        }
        if (lastName == null || !NAME_PATTERN.matcher(lastName).matches()) {
            throw new IllegalArgumentException("Invalid last name. Must be 2-50 characters, letters only.");
        }
        if (guardianName != null && !guardianName.isEmpty() && !NAME_PATTERN.matcher(guardianName).matches()) {
            throw new IllegalArgumentException("Invalid guardian name. Must be 2-50 characters, letters only.");
        }
    }

    private void validateEmail() {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (guardianEmail != null && !guardianEmail.isEmpty() && !EMAIL_PATTERN.matcher(guardianEmail).matches()) {
            throw new IllegalArgumentException("Invalid guardian email format");
        }
    }

    private void validatePhone() {
        if (phone != null && !phone.isEmpty() && !PHONE_PATTERN.matcher(phone).matches()) {
            throw new IllegalArgumentException("Invalid phone number. Must be 10 digits");
        }
        if (guardianPhone != null && !guardianPhone.isEmpty() && !PHONE_PATTERN.matcher(guardianPhone).matches()) {
            throw new IllegalArgumentException("Invalid guardian phone number. Must be 10 digits");
        }
    }

    private void validateMarks() {
        if (marks10th < 0 || marks10th > 100) {
            throw new IllegalArgumentException("10th marks must be between 0 and 100");
        }
        if (marks12th < 0 || marks12th > 100) {
            throw new IllegalArgumentException("12th marks must be between 0 and 100");
        }
    }

    private void validateBasicFields() {
        // Check required fields
        if (dateOfBirth == null) {
            throw new IllegalArgumentException("Date of birth is required");
        }
        Date now = new Date();
        if (dateOfBirth.after(now)) {
            throw new IllegalArgumentException("Date of birth cannot be in the future");
        }
        
        if (gender == null || !gender.matches("^[MFO]$")) {
            throw new IllegalArgumentException("Gender must be M, F, or O");
        }
        
        if (category == null || !category.matches("^(GENERAL|SC|ST|OBC|OTHER)$")) {
            throw new IllegalArgumentException("Invalid category. Must be GENERAL, SC, ST, OBC, or OTHER");
        }
        
        // Optional fields validation
        if (postalCode != null && !postalCode.isEmpty() && !POSTAL_CODE_PATTERN.matcher(postalCode).matches()) {
            throw new IllegalArgumentException("Invalid postal code. Must be 6 digits");
        }
        
        if (city != null && !city.isEmpty() && !NAME_PATTERN.matcher(city).matches()) {
            throw new IllegalArgumentException("City name can only contain letters, spaces, and hyphens");
        }
        
        if (state != null && !state.isEmpty() && !NAME_PATTERN.matcher(state).matches()) {
            throw new IllegalArgumentException("State name can only contain letters, spaces, and hyphens");
        }
        
        if (previousQualification != null && !previousQualification.isEmpty() && 
            !previousQualification.matches("^[A-Za-z0-9\\s.,()-]{2,100}$")) {
            throw new IllegalArgumentException("Previous qualification contains invalid characters");
        }

        if (nationality == null || nationality.isEmpty()) {
            nationality = "Indian"; // Default value
        } else if (!NAME_PATTERN.matcher(nationality).matches()) {
            throw new IllegalArgumentException("Nationality can only contain letters, spaces, and hyphens");
        }

        // Additional validations for address if provided
        if (address != null && !address.isEmpty()) {
            if (address.length() < 5 || address.length() > 200) {
                throw new IllegalArgumentException("Address must be between 5 and 200 characters");
            }
            if (!address.matches("^[A-Za-z0-9\\s.,#-/()]{5,200}$")) {
                throw new IllegalArgumentException("Address contains invalid characters");
            }
        }
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getRegistrationNo() { return registrationNo; }
    public void setRegistrationNo(String registrationNo) { this.registrationNo = registrationNo; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public Date getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(Date dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getMarks10th() { return marks10th; }
    public void setMarks10th(double marks10th) { this.marks10th = marks10th; }

    public double getMarks12th() { return marks12th; }
    public void setMarks12th(double marks12th) { this.marks12th = marks12th; }

    public String getPreviousQualification() { return previousQualification; }
    public void setPreviousQualification(String previousQualification) { this.previousQualification = previousQualification; }

    public String getGuardianName() { return guardianName; }
    public void setGuardianName(String guardianName) { this.guardianName = guardianName; }

    public String getGuardianPhone() { return guardianPhone; }
    public void setGuardianPhone(String guardianPhone) { this.guardianPhone = guardianPhone; }

    public String getGuardianEmail() { return guardianEmail; }
    public void setGuardianEmail(String guardianEmail) { this.guardianEmail = guardianEmail; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
    @Override
    public String toString() {
        return String.format("Student[ID=%d, Name=%s %s, Email=%s, Category=%s, Status=%s]",
            id, firstName, lastName, email, category, isActive ? "Active" : "Inactive");
    }
}
