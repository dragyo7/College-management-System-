# College Admission Management System

A comprehensive Java-based desktop application for managing college admissions with a modern GUI interface, database integration, and advanced features like dark mode.

## 🌟 Features

- **Modern GUI Interface**: Clean, responsive design with dark mode support
- **Student Management**: Add, search, and manage student records
- **Database Integration**: MySQL database with comprehensive schema
- **Dark Mode**: Toggle between light and dark themes
- **Real-time Search**: Instant student search functionality
- **Data Validation**: Robust input validation and error handling
- **Export Capabilities**: Export student data to CSV format

## 🏗️ Project Structure

```
CollegeAdmissionManagementSystem/
├── src/
│   ├── main/                    # Main application classes
│   │   ├── SimpleGUI.java      # Main GUI application with dark mode
│   │   ├── SimpleConsoleApp.java # Console version
│   │   ├── MainGUI.java        # Alternative GUI implementation
│   │   └── Main.java           # Entry point
│   ├── dao/                    # Data Access Objects
│   │   ├── DBConnection.java   # Database connection management
│   │   ├── SimpleStudentDAO.java # Student data operations
│   │   └── DAOException.java   # Custom exception handling
│   ├── model/                  # Data models
│   │   ├── Student.java        # Student entity
│   │   ├── SimpleStudent.java  # Simplified student model
│   │   ├── Course.java         # Course entity
│   │   └── Application.java    # Application entity
│   ├── controller/             # Web controllers (for web version)
│   └── service/                # Business logic layer
├── database/
│   └── schema.sql             # Complete database schema
├── lib/                       # External libraries
│   └── mysql-connector-j-9.5.0/ # MySQL JDBC driver
├── build.ps1                  # PowerShell build script
└── README.md                  # This file
```

## 🚀 Getting Started

### Prerequisites

- **Java Development Kit (JDK) 17 or higher**
- **MySQL Server 8.0 or higher**
- **PowerShell** (for Windows users)
- **Git** (for version control)

### Installation Steps

1. **Clone the Repository**
   ```bash
   git clone https://github.com/yourusername/CollegeAdmissionManagementSystem.git
   cd CollegeAdmissionManagementSystem
   ```

2. **Set up MySQL Database**
   ```bash
   # Login to MySQL (use your root password)
   mysql -u root -p
   
   # Create the database
   CREATE DATABASE college_admission_db;
   
   # Exit MySQL
   exit
   
   # Import the schema
   mysql -u root -p college_admission_db < database/schema.sql
   ```

3. **Configure Database Connection**
   - Open `src/dao/DBConnection.java`
   - Update the following constants if needed:
     ```java
     private static final String URL = "jdbc:mysql://localhost:3306/college_admission_db";
     private static final String USER = "root";
     private static final String PASSWORD = "your_mysql_password";
     ```

4. **Build the Project**
   ```bash
   # Using PowerShell (Recommended)
   ./build.ps1
   
   # Or manually compile
   javac -cp "lib/*;src" -d out/classes src/main/*.java src/dao/*.java src/model/*.java
   ```

5. **Run the Application**
   ```bash
   # Run the modern GUI with dark mode
   java -cp "out/classes;lib/mysql-connector-j-9.5.0/*" main.SimpleGUI
   
   # Or run the console version
   java -cp "out/classes;lib/mysql-connector-j-9.5.0/*" main.SimpleConsoleApp
   ```

## 💻 Usage Guide

### GUI Application (Recommended)

1. **Launch**: Run `SimpleGUI.java` for the modern interface
2. **Add Students**: Click "➕ Add Student" button
3. **Search**: Use the search bar to find students by name or ID
4. **Refresh**: Click "🔄 Refresh" to update the student list
5. **Dark Mode**: Toggle "🌙 Dark Mode" for comfortable viewing
6. **Exit**: Click "❌ Exit" to close the application

### Console Application

1. **Launch**: Run `SimpleConsoleApp.java` for command-line interface
2. **Menu Options**:
   - Add new student records
   - View all students
   - Search for specific students
   - Update student information
   - Delete student records

## 🛠️ Database Schema

The system includes a comprehensive database with the following tables:

- **Students**: Student information (ID, name, email, phone, etc.)
- **Courses**: Course details and availability
- **Departments**: Academic departments
- **Applications**: Student admission applications
- **AcademicYears**: Academic year management
- **CoursePrerequisites**: Course prerequisite relationships
- **Views**: Predefined views for reporting

## 🔧 Troubleshooting

### Common Issues

1. **"MySQL JDBC driver not found"**
   - Ensure `mysql-connector-j-9.5.0.jar` is in the `lib/mysql-connector-j-9.5.0/` directory
   - Check that the classpath includes the correct path to the JAR file

2. **"Access denied for user 'root'@'localhost'"**
   - Verify MySQL credentials in `DBConnection.java`
   - Ensure MySQL server is running
   - Check that the root user has proper permissions

3. **"Database 'college_admission_db' doesn't exist"**
   - Run the database creation command: `mysql -u root -p -e "CREATE DATABASE college_admission_db"`
   - Import the schema: `mysql -u root -p college_admission_db < database/schema.sql`

4. **Compilation Errors**
   - Ensure JDK 17+ is installed: `java -version`
   - Check that all dependencies are in the `lib/` directory
   - Use the provided PowerShell build script for consistency

### Build Issues

If the PowerShell script doesn't work:
```bash
# Manual compilation
javac -cp "lib/*;src" -d bin src/main/*.java src/dao/*.java src/model/*.java

# Manual execution
java -cp "bin;lib/mysql-connector-j-9.5.0/*" main.SimpleGUI
```

## 📝 Code Comments

The codebase includes comprehensive comments explaining:

- **Database Operations**: Connection pooling and SQL queries
- **GUI Components**: Event handling and UI updates
- **Data Models**: Entity relationships and validation
- **Error Handling**: Exception management and user feedback
- **Dark Mode**: Theme switching implementation



## 🙏 Acknowledgments

- MySQL for the robust database system
- Java Swing for the GUI framework
- The open-source community for continuous inspiration

---

**⭐ Don't forget to star this repository if you find it helpful!**
