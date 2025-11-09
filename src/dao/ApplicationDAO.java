package dao;

import java.sql.*;
import java.util.*;
import model.Application;

public class ApplicationDAO {

    // Add an application record
    public void addApplication(Application app) {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "INSERT INTO Applications (student_id, course_id, status) VALUES (?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, app.getStudentId());
            ps.setInt(2, app.getCourseId());
            ps.setString(3, app.getStatus());
            ps.executeUpdate();
            System.out.println("✅ Application submitted!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Fetch all applications
    public List<Application> getAllApplications() {
        List<Application> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT * FROM Applications";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                Application app = new Application();
                app.setId(rs.getInt("app_id"));
                app.setStudentId(rs.getInt("student_id"));
                app.setCourseId(rs.getInt("course_id"));
                app.setStatus(rs.getString("status"));
                list.add(app);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
