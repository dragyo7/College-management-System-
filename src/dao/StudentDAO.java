package dao;

import java.sql.*;
import java.util.*;
import model.Student;

public class StudentDAO {
    private static final String SQL_INSERT = "INSERT INTO Students (registration_no, first_name, last_name, date_of_birth, " +
            "gender, email, phone, address, city, state, postal_code, nationality, " +
            "category, marks_10th, marks_12th, previous_qualification, " +
            "guardian_name, guardian_phone, guardian_email, is_active, created_at, updated_at) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";

    private static final String SQL_UPDATE = "UPDATE Students SET " +
            "registration_no=?, first_name=?, last_name=?, date_of_birth=?, " +
            "gender=?, email=?, phone=?, address=?, city=?, state=?, postal_code=?, " +
            "nationality=?, category=?, marks_10th=?, marks_12th=?, " +
            "previous_qualification=?, guardian_name=?, guardian_phone=?, " +
            "guardian_email=?, is_active=?, updated_at=NOW() WHERE id=?";

    private static final String SQL_SELECT_ALL = "SELECT * FROM Students ORDER BY created_at DESC";
    private static final String SQL_SELECT_BY_ID = "SELECT * FROM Students WHERE id=?";
    private static final String SQL_SELECT_ACTIVE = "SELECT * FROM Students WHERE is_active=?";
    private static final String SQL_SEARCH = "SELECT * FROM Students WHERE " +
            "CONCAT(first_name, ' ', last_name) LIKE ? OR email LIKE ? OR registration_no LIKE ?";
    private static final String SQL_SELECT_BY_CATEGORY = "SELECT * FROM Students WHERE category=?";
    private static final String SQL_TOTAL_STUDENTS = "SELECT COUNT(*) FROM Students";
    private static final String SQL_AVG_MARKS = "SELECT AVG(marks_10th) as avg_10th, AVG(marks_12th) as avg_12th FROM Students";
    private static final String SQL_CATEGORIES = "SELECT DISTINCT category FROM Students";
    private static final String SQL_CATEGORY_COUNTS = "SELECT category, COUNT(*) as count FROM Students GROUP BY category";

    public Student save(Student student) throws DAOException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();

            if (student.getId() == 0) {
                // Insert new student
                ps = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
            } else {
                // Update existing student
                ps = conn.prepareStatement(SQL_UPDATE);
                ps.setInt(21, student.getId());
            }

            int paramIndex = 1;
            ps.setString(paramIndex++, student.getRegistrationNo());
            ps.setString(paramIndex++, student.getFirstName());
            ps.setString(paramIndex++, student.getLastName());
            ps.setDate(paramIndex++, student.getDateOfBirth() != null ? 
                      new java.sql.Date(student.getDateOfBirth().getTime()) : null);
            ps.setString(paramIndex++, student.getGender());
            ps.setString(paramIndex++, student.getEmail());
            ps.setString(paramIndex++, student.getPhone());
            ps.setString(paramIndex++, student.getAddress());
            ps.setString(paramIndex++, student.getCity());
            ps.setString(paramIndex++, student.getState());
            ps.setString(paramIndex++, student.getPostalCode());
            ps.setString(paramIndex++, student.getNationality());
            ps.setString(paramIndex++, student.getCategory());
            ps.setDouble(paramIndex++, student.getMarks10th());
            ps.setDouble(paramIndex++, student.getMarks12th());
            ps.setString(paramIndex++, student.getPreviousQualification());
            ps.setString(paramIndex++, student.getGuardianName());
            ps.setString(paramIndex++, student.getGuardianPhone());
            ps.setString(paramIndex++, student.getGuardianEmail());
            ps.setBoolean(paramIndex++, student.isActive());

            ps.executeUpdate();

            if (student.getId() == 0) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    student.setId(rs.getInt(1));
                }
            }

            return student;

        } catch (SQLException e) {
            throw new DAOException("Error saving student: " + e.getMessage(), e);
        } finally {
            DBConnection.closeAll(conn, ps, rs);
        }
    }

    public Student findById(int id) throws DAOException {
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

    public List<Student> getAllStudents() throws DAOException {
        return getAllStudentsSorted("created_at DESC");
    }

    public List<Student> getAllStudentsSorted(String sortBy) throws DAOException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Student> students = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            String sql = SQL_SELECT_ALL + " ORDER BY " + sortBy;
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                students.add(mapStudent(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error getting all students", e);
        } finally {
            DBConnection.close(rs, ps, conn);
        }

        return students;
    }

    public List<Student> searchStudents(String query) throws DAOException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Student> students = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(SQL_SEARCH);
            String likeQuery = "%" + query + "%";
            ps.setString(1, likeQuery);
            ps.setString(2, likeQuery);
            ps.setString(3, likeQuery);
            
            rs = ps.executeQuery();
            while (rs.next()) {
                students.add(mapStudent(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error searching students", e);
        } finally {
            DBConnection.close(rs, ps, conn);
        }

        return students;
    }

    public List<Student> getStudentsByCategory(String category) throws DAOException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Student> students = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(SQL_SELECT_BY_CATEGORY);
            ps.setString(1, category);
            
            rs = ps.executeQuery();
            while (rs.next()) {
                students.add(mapStudent(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error getting students by category", e);
        } finally {
            DBConnection.close(rs, ps, conn);
        }

        return students;
    }

    public int getTotalStudents() throws DAOException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(SQL_TOTAL_STUDENTS);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new DAOException("Error getting total students count", e);
        } finally {
            DBConnection.close(rs, ps, conn);
        }
    }

    public Map<String, Double> getAverageMarks() throws DAOException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Map<String, Double> averages = new HashMap<>();

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(SQL_AVG_MARKS);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                averages.put("avg_10th", rs.getDouble("avg_10th"));
                averages.put("avg_12th", rs.getDouble("avg_12th"));
            }
        } catch (SQLException e) {
            throw new DAOException("Error getting average marks", e);
        } finally {
            DBConnection.close(rs, ps, conn);
        }

        return averages;
    }

    public double getAverageMarks10th() throws DAOException {
        return getAverageMarks().get("avg_10th");
    }

    public double getAverageMarks12th() throws DAOException {
        return getAverageMarks().get("avg_12th");
    }

    public Map<String, Integer> getStudentsByCategory() throws DAOException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Map<String, Integer> categoryCounts = new HashMap<>();

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(SQL_CATEGORY_COUNTS);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                categoryCounts.put(rs.getString("category"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            throw new DAOException("Error getting student counts by category", e);
        } finally {
            DBConnection.close(rs, ps, conn);
        }

        return categoryCounts;
    }

    public List<String> getCategories() throws DAOException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<String> categories = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(SQL_CATEGORIES);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                categories.add(rs.getString("category"));
            }
        } catch (SQLException e) {
            throw new DAOException("Error getting categories", e);
        } finally {
            DBConnection.close(rs, ps, conn);
        }

        return categories;
    }

    private Student mapStudent(ResultSet rs) throws SQLException {
        Student student = new Student(
            rs.getString("first_name"),
            rs.getString("last_name"),
            rs.getString("email")
        );

        student.setId(rs.getInt("id"));
        student.setRegistrationNo(rs.getString("registration_no"));
        student.setDateOfBirth(rs.getTimestamp("date_of_birth"));
        student.setGender(rs.getString("gender"));
        student.setPhone(rs.getString("phone"));
        student.setAddress(rs.getString("address"));
        student.setCity(rs.getString("city"));
        student.setState(rs.getString("state"));
        student.setPostalCode(rs.getString("postal_code"));
        student.setCategory(rs.getString("category"));
        student.setMarks10th(rs.getDouble("marks_10th"));
        student.setMarks12th(rs.getDouble("marks_12th"));
        student.setPreviousQualification(rs.getString("previous_qualification"));
        student.setGuardianName(rs.getString("guardian_name"));
        student.setGuardianPhone(rs.getString("guardian_phone"));
        student.setGuardianEmail(rs.getString("guardian_email"));
        
        return student;
    }

    public List<Student> findByActiveStatus(boolean isActive) throws DAOException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Student> students = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(SQL_SELECT_ACTIVE);
            ps.setBoolean(1, isActive);
            
            rs = ps.executeQuery();
            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
            return students;

        } catch (SQLException e) {
            throw new DAOException("Error retrieving students: " + e.getMessage(), e);
        } finally {
            DBConnection.closeAll(conn, ps, rs);
        }
    }

    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setId(rs.getInt("id"));
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
        return student;
    }
}
