package controller;

import model.Student;
import dao.StudentDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;

@WebServlet("/add-student")
public class AddStudentServlet extends HttpServlet {
    private StudentDAO dao;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public void init() {
        dao = new StudentDAO();
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            // Create student with required fields
            Student student = new Student(
                req.getParameter("firstName"),
                req.getParameter("lastName"),
                req.getParameter("email")
            );

            // Set all other fields
            Date dob = DATE_FORMAT.parse(req.getParameter("dateOfBirth"));
            student.setDateOfBirth(dob);
            student.setGender(req.getParameter("gender"));
            student.setPhone(req.getParameter("phone"));
            student.setAddress(req.getParameter("address"));
            student.setCity(req.getParameter("city"));
            student.setState(req.getParameter("state"));
            student.setPostalCode(req.getParameter("postalCode"));
            student.setCategory(req.getParameter("category"));
            student.setMarks10th(Double.parseDouble(req.getParameter("marks10th")));
            student.setMarks12th(Double.parseDouble(req.getParameter("marks12th")));
            student.setPreviousQualification(req.getParameter("prevQualification"));
            student.setGuardianName(req.getParameter("guardianName"));
            student.setGuardianPhone(req.getParameter("guardianPhone"));
            student.setGuardianEmail(req.getParameter("guardianEmail"));
            
            // Validate and save
            student.validate();
            dao.save(student);
            
            resp.sendRedirect("list-students");
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            RequestDispatcher dispatcher = req.getRequestDispatcher("addStudentForm.jsp");
            dispatcher.forward(req, resp);
        }
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher dispatcher = req.getRequestDispatcher("addStudentForm.jsp");
        dispatcher.forward(req, resp);
    }
}
