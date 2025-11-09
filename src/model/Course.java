package model;

import java.util.regex.Pattern;

public class Course {
    private int id;
    private String name;
    private String code;
    private int cutoff;
    private int maxSeats;
    private int duration;  // in semesters
    private String description;
    private double fees;
    private String department;
    private boolean isActive;

    private static final Pattern CODE_PATTERN = Pattern.compile("^[A-Z]{2,4}\\d{3,4}$");

    public Course() {
        this.isActive = true;
    }

    public Course(String name, String code, int cutoff, int maxSeats, int duration, 
                 String description, double fees, String department) {
        this();
        setName(name);
        setCode(code);
        setCutoff(cutoff);
        setMaxSeats(maxSeats);
        setDuration(duration);
        setDescription(description);
        setFees(fees);
        setDepartment(department);
    }

    // Validation methods
    public static void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Course name cannot be empty");
        }
        if (name.length() < 3 || name.length() > 100) {
            throw new IllegalArgumentException("Course name must be between 3 and 100 characters");
        }
    }

    public static void validateCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Course code cannot be empty");
        }
        if (!CODE_PATTERN.matcher(code).matches()) {
            throw new IllegalArgumentException("Course code must be 2-4 uppercase letters followed by 3-4 digits");
        }
    }

    public static void validateCutoff(int cutoff) {
        if (cutoff < 0 || cutoff > 100) {
            throw new IllegalArgumentException("Cutoff marks must be between 0 and 100");
        }
    }

    public static void validateSeats(int seats) {
        if (seats <= 0) {
            throw new IllegalArgumentException("Maximum seats must be greater than 0");
        }
    }

    public static void validateDuration(int duration) {
        if (duration <= 0 || duration > 12) {
            throw new IllegalArgumentException("Duration must be between 1 and 12 semesters");
        }
    }

    public static void validateFees(double fees) {
        if (fees < 0) {
            throw new IllegalArgumentException("Fees cannot be negative");
        }
    }

    // Getters and Setters with validation
    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("ID cannot be negative");
        }
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        validateName(name);
        this.name = name.trim();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        validateCode(code);
        this.code = code.trim().toUpperCase();
    }

    public int getCutoff() {
        return cutoff;
    }

    public void setCutoff(int cutoff) {
        validateCutoff(cutoff);
        this.cutoff = cutoff;
    }

    public int getMaxSeats() {
        return maxSeats;
    }

    public void setMaxSeats(int maxSeats) {
        validateSeats(maxSeats);
        this.maxSeats = maxSeats;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        validateDuration(duration);
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }
        this.description = description.trim();
    }

    public double getFees() {
        return fees;
    }

    public void setFees(double fees) {
        validateFees(fees);
        this.fees = fees;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        if (department == null || department.trim().isEmpty()) {
            throw new IllegalArgumentException("Department cannot be empty");
        }
        this.department = department.trim();
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    // Validate all fields at once
    public void validate() {
        validateName(name);
        validateCode(code);
        validateCutoff(cutoff);
        validateSeats(maxSeats);
        validateDuration(duration);
        validateFees(fees);
        if (department == null || department.trim().isEmpty()) {
            throw new IllegalArgumentException("Department cannot be empty");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }
    }

    @Override
    public String toString() {
        return String.format("Course [%s-%s] %s\n  Department: %s\n  Duration: %d semesters\n  Fees: ₹%.2f\n  Seats: %d\n  Cutoff: %d%%\n  Status: %s",
            code, id, name, department, duration, fees, maxSeats, cutoff, 
            isActive ? "Active" : "Inactive");
    }
}
