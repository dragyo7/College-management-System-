package controller;

import dao.StudentDAO;
import model.Student;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/list-students")
public class ListStudentsServlet extends HttpServlet {
    private StudentDAO dao;

    public void init() {
        dao = new StudentDAO();
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String sortBy = req.getParameter("sortBy");
            String filterCategory = req.getParameter("category");
            String searchQuery = req.getParameter("search");

            List<Student> students;
            
            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                students = dao.searchStudents(searchQuery);
            } else if (filterCategory != null && !filterCategory.trim().isEmpty()) {
                students = dao.getStudentsByCategory(filterCategory);
            } else {
                students = sortBy != null ? dao.getAllStudentsSorted(sortBy) : dao.getAllStudents();
            }

            req.setAttribute("students", students);
            
            // Get statistics
            req.setAttribute("totalStudents", dao.getTotalStudents());
            req.setAttribute("averageMarks10th", dao.getAverageMarks10th());
            req.setAttribute("averageMarks12th", dao.getAverageMarks12th());
            req.setAttribute("categories", dao.getCategories());

            RequestDispatcher dispatcher = req.getRequestDispatcher("listStudents.jsp");
            dispatcher.forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", "Error fetching student data: " + e.getMessage());
            RequestDispatcher dispatcher = req.getRequestDispatcher("listStudents.jsp");
            dispatcher.forward(req, resp);
        }
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
