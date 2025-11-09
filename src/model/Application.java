package model;

public class Application {
    private int id;
    private int studentId;
    private int courseId;
    private String status;

    public Application() {}

    public Application(int id, int studentId, int courseId, String status) {
        this.id = id;
        this.studentId = studentId;
        this.courseId = courseId;
        this.status = status;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getStudentId() {
        return studentId;
    }
    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getCourseId() {
        return courseId;
    }
    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Application [ID=" + id + ", StudentID=" + studentId + 
               ", CourseID=" + courseId + ", Status=" + status + "]";
    }
}
