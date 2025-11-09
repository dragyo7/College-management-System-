-- Create the database if it doesn't exist
CREATE DATABASE IF NOT EXISTS college_admission_db;

USE college_admission_db;

-- Drop tables if they exist (for clean setup)
DROP TABLE IF EXISTS ApplicationDocuments;

DROP TABLE IF EXISTS Applications;

DROP TABLE IF EXISTS StudentContacts;

DROP TABLE IF EXISTS CoursePrerequisites;

DROP TABLE IF EXISTS Students;

DROP TABLE IF EXISTS Courses;

DROP TABLE IF EXISTS Departments;

DROP TABLE IF EXISTS AcademicYears;

-- Create AcademicYears table
CREATE TABLE AcademicYears (
    year_id INT PRIMARY KEY AUTO_INCREMENT,
    year_name VARCHAR(9) NOT NULL UNIQUE,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    admission_start DATE,
    admission_end DATE,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_year_name CHECK (
        year_name REGEXP '^[0-9]{4}-[0-9]{4}$'
    ),
    CONSTRAINT chk_dates CHECK (
        start_date < end_date
        AND admission_start < admission_end
    )
);

-- Create Departments table
CREATE TABLE Departments (
    dept_id INT PRIMARY KEY AUTO_INCREMENT,
    dept_code VARCHAR(10) NOT NULL UNIQUE,
    dept_name VARCHAR(100) NOT NULL UNIQUE,
    hod_name VARCHAR(100),
    office_email VARCHAR(100),
    office_phone VARCHAR(20),
    max_intake INT DEFAULT 60,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create Courses table with enhanced fields
CREATE TABLE Courses (
    course_id INT PRIMARY KEY AUTO_INCREMENT,
    dept_id INT NOT NULL,
    course_code VARCHAR(20) NOT NULL UNIQUE,
    course_name VARCHAR(100) NOT NULL,
    description TEXT,
    duration INT NOT NULL COMMENT 'Duration in semesters',
    cutoff_marks INT NOT NULL,
    max_seats INT NOT NULL,
    fees DECIMAL(10, 2) NOT NULL,
    start_date DATE,
    end_date DATE,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (dept_id) REFERENCES Departments (dept_id),
    CONSTRAINT chk_duration CHECK (duration > 0),
    CONSTRAINT chk_cutoff CHECK (
        cutoff_marks BETWEEN 0 AND 100
    ),
    CONSTRAINT chk_seats CHECK (max_seats > 0),
    CONSTRAINT chk_fees CHECK (fees >= 0)
);

-- Create CoursePrerequisites table
CREATE TABLE CoursePrerequisites (
    prerequisite_id INT PRIMARY KEY AUTO_INCREMENT,
    course_id INT NOT NULL,
    required_course_id INT NOT NULL,
    min_grade VARCHAR(2),
    is_mandatory BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES Courses (course_id),
    FOREIGN KEY (required_course_id) REFERENCES Courses (course_id),
    CONSTRAINT unique_prerequisite UNIQUE (course_id, required_course_id)
);

-- Create Students table with enhanced fields
CREATE TABLE Students (
    student_id INT PRIMARY KEY AUTO_INCREMENT,
    registration_no VARCHAR(20) UNIQUE,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    date_of_birth DATE NOT NULL,
    gender ENUM('M', 'F', 'O') NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    address TEXT,
    city VARCHAR(50),
    state VARCHAR(50),
    postal_code VARCHAR(10),
    nationality VARCHAR(50) DEFAULT 'Indian',
    category ENUM(
        'GENERAL',
        'SC',
        'ST',
        'OBC',
        'OTHER'
    ) NOT NULL,
    marks_10th DECIMAL(5, 2) NOT NULL,
    marks_12th DECIMAL(5, 2) NOT NULL,
    previous_qualification VARCHAR(100),
    guardian_name VARCHAR(100),
    guardian_phone VARCHAR(20),
    guardian_email VARCHAR(100),
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_marks_10 CHECK (marks_10th BETWEEN 0 AND 100),
    CONSTRAINT chk_marks_12 CHECK (marks_12th BETWEEN 0 AND 100),
    CONSTRAINT chk_email CHECK (
        email REGEXP '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$'
    )
);

-- Create StudentContacts table for additional contact information
CREATE TABLE StudentContacts (
    contact_id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    contact_type ENUM(
        'HOME',
        'WORK',
        'EMERGENCY',
        'OTHER'
    ) NOT NULL,
    contact_name VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(100),
    address TEXT,
    relation VARCHAR(50),
    is_primary BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES Students (student_id),
    CONSTRAINT unique_primary_contact UNIQUE (
        student_id,
        contact_type,
        is_primary
    )
);

-- Create Applications table
CREATE TABLE Applications (
    application_id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    course_id INT NOT NULL,
    academic_year_id INT NOT NULL,
    application_date DATE NOT NULL,
    status ENUM(
        'PENDING',
        'UNDER_REVIEW',
        'ACCEPTED',
        'REJECTED',
        'WAITLISTED',
        'CANCELLED'
    ) NOT NULL DEFAULT 'PENDING',
    merit_score DECIMAL(5, 2),
    interview_date DATE,
    interview_score DECIMAL(5, 2),
    remarks TEXT,
    payment_status ENUM(
        'PENDING',
        'PARTIAL',
        'COMPLETED',
        'REFUNDED'
    ) DEFAULT 'PENDING',
    payment_amount DECIMAL(10, 2),
    payment_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES Students (student_id),
    FOREIGN KEY (course_id) REFERENCES Courses (course_id),
    FOREIGN KEY (academic_year_id) REFERENCES AcademicYears (year_id),
    CONSTRAINT unique_application UNIQUE (
        student_id,
        course_id,
        academic_year_id
    )
);

-- Create ApplicationDocuments table
CREATE TABLE ApplicationDocuments (
    document_id INT PRIMARY KEY AUTO_INCREMENT,
    application_id INT NOT NULL,
    document_type VARCHAR(50) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    verification_status ENUM(
        'PENDING',
        'VERIFIED',
        'REJECTED'
    ) DEFAULT 'PENDING',
    verified_by VARCHAR(100),
    verification_date TIMESTAMP,
    remarks TEXT,
    FOREIGN KEY (application_id) REFERENCES Applications (application_id)
);

-- Create indexes for better query performance
CREATE INDEX idx_student_name ON Students (first_name, last_name);

CREATE INDEX idx_course_dept ON Courses (dept_id);

CREATE INDEX idx_application_status ON Applications (status);

CREATE INDEX idx_application_date ON Applications (application_date);

CREATE INDEX idx_academic_year ON AcademicYears (year_name);

-- Insert sample department data
INSERT INTO
    Departments (
        dept_code,
        dept_name,
        hod_name,
        office_email,
        office_phone,
        max_intake
    )
VALUES (
        'CSE',
        'Computer Science and Engineering',
        'Dr. Rajesh Kumar',
        'cse.hod@college.edu',
        '9876543210',
        120
    ),
    (
        'ECE',
        'Electronics and Communication',
        'Dr. Priya Singh',
        'ece.hod@college.edu',
        '9876543211',
        60
    ),
    (
        'ME',
        'Mechanical Engineering',
        'Dr. Suresh Patel',
        'me.hod@college.edu',
        '9876543212',
        60
    ),
    (
        'CE',
        'Civil Engineering',
        'Dr. Amit Shah',
        'ce.hod@college.edu',
        '9876543213',
        60
    );

-- Insert sample academic year
INSERT INTO
    AcademicYears (
        year_name,
        start_date,
        end_date,
        admission_start,
        admission_end
    )
VALUES (
        '2025-2026',
        '2025-07-01',
        '2026-06-30',
        '2025-03-01',
        '2025-07-31'
    );

-- Insert sample courses
INSERT INTO
    Courses (
        dept_id,
        course_code,
        course_name,
        description,
        duration,
        cutoff_marks,
        max_seats,
        fees
    )
VALUES (
        1,
        'CSE001',
        'B.Tech in Computer Science',
        'Bachelor of Technology in Computer Science and Engineering',
        8,
        85,
        120,
        125000.00
    ),
    (
        1,
        'CSE002',
        'M.Tech in Computer Science',
        'Master of Technology in Computer Science and Engineering',
        4,
        75,
        30,
        150000.00
    ),
    (
        2,
        'ECE001',
        'B.Tech in Electronics',
        'Bachelor of Technology in Electronics and Communication',
        8,
        80,
        60,
        125000.00
    ),
    (
        3,
        'ME001',
        'B.Tech in Mechanical',
        'Bachelor of Technology in Mechanical Engineering',
        8,
        75,
        60,
        125000.00
    );

-- Create Views for common queries
CREATE VIEW vw_course_availability AS
SELECT
    c.course_id,
    c.course_name,
    c.max_seats,
    COUNT(a.application_id) as applications_received,
    c.max_seats - COUNT(a.application_id) as seats_available
FROM Courses c
    LEFT JOIN Applications a ON c.course_id = a.course_id
WHERE
    c.is_active = true
GROUP BY
    c.course_id;

CREATE VIEW vw_application_status AS
SELECT s.first_name, s.last_name, c.course_name, a.status, a.application_date, a.merit_score, a.payment_status
FROM
    Applications a
    JOIN Students s ON a.student_id = s.student_id
    JOIN Courses c ON a.course_id = c.course_id
ORDER BY a.application_date DESC;

-- Create stored procedure for application processing
DELIMITER / /

CREATE PROCEDURE sp_process_application(
    IN p_application_id INT,
    IN p_status VARCHAR(20),
    IN p_remarks TEXT
)
BEGIN
    UPDATE Applications
    SET status = p_status,
        remarks = p_remarks,
        updated_at = CURRENT_TIMESTAMP
    WHERE application_id = p_application_id;
    
    IF p_status = 'ACCEPTED' THEN
        -- Send notification (placeholder)
        INSERT INTO ApplicationDocuments (application_id, document_type, file_name, file_path)
        VALUES (p_application_id, 'ACCEPTANCE_LETTER', 
                CONCAT('acceptance_', p_application_id, '.pdf'),
                CONCAT('/documents/acceptance/', p_application_id, '.pdf'));
    END IF;
END //

DELIMITER;

-- Create trigger for application status tracking
DELIMITER / /

CREATE TRIGGER before_application_update
BEFORE UPDATE ON Applications
FOR EACH ROW
BEGIN
    IF NEW.status != OLD.status THEN
        SET NEW.updated_at = CURRENT_TIMESTAMP;
    END IF;
END //

DELIMITER;