package dao;

import java.sql.*;
import java.util.*;
import model.Course;

public class CourseDAO {
    private static final String SQL_INSERT = 
        "INSERT INTO Courses (course_name, course_code, description, duration, cutoff_marks, " +
        "max_seats, fees, dept_id, is_active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    private static final String SQL_SELECT_ALL = 
        "SELECT c.*, d.dept_name FROM Courses c LEFT JOIN Departments d ON c.dept_id = d.dept_id ORDER BY c.course_name";
    
    private static final String SQL_SELECT_BY_ID = 
        "SELECT c.*, d.dept_name FROM Courses c LEFT JOIN Departments d ON c.dept_id = d.dept_id WHERE c.course_id=?";

    public Course save(Course course) throws DAOException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
            
            int paramIndex = 1;
            ps.setString(paramIndex++, course.getName());
            ps.setString(paramIndex++, course.getCode());
            ps.setString(paramIndex++, course.getDescription());
            ps.setInt(paramIndex++, course.getDuration());
            ps.setInt(paramIndex++, course.getCutoff());
            ps.setInt(paramIndex++, course.getMaxSeats());
            ps.setDouble(paramIndex++, course.getFees());
            ps.setInt(paramIndex++, 1); // Default to first department for now
            ps.setBoolean(paramIndex++, course.isActive());
            
            ps.executeUpdate();
            
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                course.setId(rs.getInt(1));
            }
            
            return course;
        } catch (SQLException e) {
            throw new DAOException("Error saving course: " + e.getMessage(), e);
        } finally {
            DBConnection.closeAll(conn, ps, rs);
        }
    }

    public List<Course> getAllCourses() throws DAOException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Course> courses = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(SQL_SELECT_ALL);
            rs = ps.executeQuery();

            while (rs.next()) {
                courses.add(mapResultSetToCourse(rs));
            }

            return courses;
        } catch (SQLException e) {
            throw new DAOException("Error retrieving courses: " + e.getMessage(), e);
        } finally {
            DBConnection.closeAll(conn, ps, rs);
        }
    }

    public Course findById(int id) throws DAOException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(SQL_SELECT_BY_ID);
            ps.setInt(1, id);
            
            rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToCourse(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new DAOException("Error finding course: " + e.getMessage(), e);
        } finally {
            DBConnection.closeAll(conn, ps, rs);
        }
    }

    private Course mapResultSetToCourse(ResultSet rs) throws SQLException {
        Course course = new Course();
        course.setId(rs.getInt("course_id"));
        course.setName(rs.getString("course_name"));
        course.setCode(rs.getString("course_code"));
        course.setDescription(rs.getString("description"));
        course.setDuration(rs.getInt("duration"));
        course.setCutoff(rs.getInt("cutoff_marks"));
        course.setMaxSeats(rs.getInt("max_seats"));
        course.setFees(rs.getDouble("fees"));
        course.setDepartment(rs.getString("dept_name"));
        course.setActive(rs.getBoolean("is_active"));
        return course;
    }
}
