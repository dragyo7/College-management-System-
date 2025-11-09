package controller;

import dao.StudentDAO;
import model.Student;

import jakarta.servlet.ServletException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.List;

@WebServlet("/export-csv")
public class ExportCSVServlet extends HttpServlet {
    private StudentDAO dao;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public void init() {
        dao = new StudentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String sortBy = req.getParameter("sortBy");
            List<Student> students = sortBy != null ? dao.getAllStudentsSorted(sortBy) : dao.getAllStudents();

            resp.setContentType("text/csv");
            resp.setHeader("Content-Disposition", "attachment; filename=\"student_list.csv\"");
            resp.setCharacterEncoding("UTF-8");

            try (PrintWriter pw = resp.getWriter()) {
                // Write headers
                pw.println("Registration No,First Name,Last Name,Date of Birth,Gender,Email,Phone," +
                          "Address,City,State,Postal Code,Category,10th Marks (%),12th Marks (%)," +
                          "Previous Qualification,Guardian Name,Guardian Phone,Guardian Email");

                // Write data
                for (Student s : students) {
                    pw.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"," +
                             "\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%.2f\",\"%.2f\"," +
                             "\"%s\",\"%s\",\"%s\",\"%s\"%n",
                        escapeCSV(s.getRegistrationNo()),
                        escapeCSV(s.getFirstName()),
                        escapeCSV(s.getLastName()),
                        s.getDateOfBirth() != null ? DATE_FORMAT.format(s.getDateOfBirth()) : "",
                        escapeCSV(s.getGender()),
                        escapeCSV(s.getEmail()),
                        escapeCSV(s.getPhone()),
                        escapeCSV(s.getAddress()),
                        escapeCSV(s.getCity()),
                        escapeCSV(s.getState()),
                        escapeCSV(s.getPostalCode()),
                        escapeCSV(s.getCategory()),
                        s.getMarks10th(),
                        s.getMarks12th(),
                        escapeCSV(s.getPreviousQualification()),
                        escapeCSV(s.getGuardianName()),
                        escapeCSV(s.getGuardianPhone()),
                        escapeCSV(s.getGuardianEmail())
                    );
                }
            }
        } catch (Exception e) {
            throw new ServletException("Error exporting students to CSV", e);
        }
    }

    private String escapeCSV(String value) {
        if (value == null) return "";
        return value.replace("\"", "\"\"");
    }
}
