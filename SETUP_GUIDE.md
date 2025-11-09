# College Admission Management System - Complete Setup Guide

## Prerequisites

### System Requirements
- **Operating System**: Windows 10/11, Linux, or macOS
- **Java**: JDK 17 or higher (JDK 21 recommended)
- **MySQL**: MySQL Server 8.0 or higher
- **Memory**: Minimum 2GB RAM (4GB recommended)
- **Storage**: 500MB free space for application and dependencies

### Required Software
1. **Java Development Kit (JDK)**
   - Download from: https://adoptium.net/ or https://www.oracle.com/java/
   - Verify installation: `java -version` and `javac -version`

2. **MySQL Server**
   - Download from: https://dev.mysql.com/downloads/mysql/
   - MySQL Workbench recommended for database management

3. **Git** (optional, for version control)
   - Download from: https://git-scm.com/

## Step-by-Step Installation

### Step 1: Download and Extract the Project
```bash
# Clone from GitHub (if using git)
git clone https://github.com/your-username/CollegeAdmissionManagementSystem.git
cd CollegeAdmissionManagementSystem

# Or download and extract the ZIP file
# Extract to: C:\CollegeAdmissionManagementSystem (Windows) or /home/user/CollegeAdmissionManagementSystem (Linux)
```

### Step 2: Install MySQL Database

#### Windows Installation
1. Download MySQL Installer from https://dev.mysql.com/downloads/installer/
2. Run MySQL Installer and select:
   - MySQL Server 8.0+
   - MySQL Workbench (recommended)
   - MySQL Shell
3. Follow installation wizard:
   - Set root password (remember this!)
   - Create MySQL80 service
   - Start MySQL Server

#### Linux Installation (Ubuntu/Debian)
```bash
# Update package list
sudo apt update

# Install MySQL Server
sudo apt install mysql-server mysql-client

# Secure installation
sudo mysql_secure_installation

# Start MySQL service
sudo systemctl start mysql
sudo systemctl enable mysql
```

### Step 3: Configure MySQL Database

#### Connect to MySQL
```bash
# Windows (Command Prompt)
mysql -u root -p

# Linux/Mac (Terminal)
sudo mysql -u root -p
```

#### Create Database and User
```sql
-- Create database
CREATE DATABASE college_admission_db;

-- Create application user (recommended for security)
CREATE USER 'college_admin'@'localhost' IDENTIFIED BY 'your_secure_password';
GRANT ALL PRIVILEGES ON college_admission_db.* TO 'college_admin'@'localhost';
FLUSH PRIVILEGES;

-- Exit MySQL
EXIT;
```

#### Import Database Schema
```bash
# Navigate to database directory
cd database

# Import schema (using root or college_admin user)
mysql -u root -p college_admission_db < college_admission_db.sql

# Or using the application user
mysql -u college_admin -p college_admission_db < college_admission_db.sql
```

### Step 4: Configure Database Connection

#### Option 1: Using Application User (Recommended)
Edit `src/dao/DBConnection.java` and update these lines:
```java
private static final String URL = "jdbc:mysql://localhost:3306/college_admission_db";
private static final String USER = "college_admin";  // Your MySQL username
private static final String PASSWORD = "your_secure_password";  // Your MySQL password
```

#### Option 2: Using Root User (Development Only)
```java
private static final String USER = "root";  // Your MySQL root username
private static final String PASSWORD = "your_root_password";  // Your MySQL root password
```

### Step 5: Compile the Application

#### Windows (Command Prompt)
```cmd
# Navigate to project root
cd C:\CollegeAdmissionManagementSystem

# Compile all Java files
javac -cp "lib/*;src" src/main/Main.java src/main/SimpleGUI.java src/dao/*.java src/model/*.java src/service/*.java

# Or compile step by step
javac -cp "lib/*;src" src/dao/DBConnection.java
javac -cp "lib/*;src" src/dao/SimpleStudentDAO.java
javac -cp "lib/*;src" src/model/SimpleStudent.java
javac -cp "lib/*;src" src/main/SimpleGUI.java
javac -cp "lib/*;src" src/main/Main.java
```

#### Linux/Mac (Terminal)
```bash
# Navigate to project directory
cd /path/to/CollegeAdmissionManagementSystem

# Compile all Java files
javac -cp "lib/*:src" src/main/Main.java src/main/SimpleGUI.java src/dao/*.java src/model/*.java src/service/*.java
```

### Step 6: Run the Application

#### GUI Mode (Recommended)
```bash
# Windows
java -cp "lib/mysql-connector-j-9.5.0/mysql-connector-j-9.5.0.jar;lib/jcalendar-1.4.jar;src" main.Main

# Linux/Mac
java -cp "lib/mysql-connector-j-9.5.0/mysql-connector-j-9.5.0.jar:lib/jcalendar-1.4.jar:src" main.Main
```

#### Console Mode
```bash
# Windows
java -cp "lib/mysql-connector-j-9.5.0/mysql-connector-j-9.5.0.jar;src" main.Main --console

# Linux/Mac
java -cp "lib/mysql-connector-j-9.5.0/mysql-connector-j-9.5.0.jar:src" main.Main --console
```

## Verification Steps

### 1. Database Connection Test
```bash
# Run this Java test to verify database connection
javac -cp "lib/*;src" src/dao/DBConnection.java
java -cp "lib/*;src" dao.DBConnection
```

### 2. GUI Application Test
- Launch the GUI application
- Check for "Connection pool initialized" message
- Try registering a test student
- Verify data appears in the table

### 3. Console Application Test
```bash
# Run console mode and test menu options
java -cp "lib/*;src" main.Main --console
# Select option 2 to view all students
# Select option 1 to register a test student
```

## Common Issues and Solutions

### Issue 1: "MySQL JDBC driver not found"
**Solution**: Ensure the MySQL connector path is correct:
```bash
# Check if file exists
ls lib/mysql-connector-j-9.5.0/mysql-connector-j-9.5.0.jar

# Correct classpath format
java -cp "lib/mysql-connector-j-9.5.0/mysql-connector-j-9.5.0.jar;src" main.Main
```

### Issue 2: "Access denied for user"
**Solution**: Check MySQL credentials and permissions:
```sql
-- Reset root password if needed
ALTER USER 'root'@'localhost' IDENTIFIED BY 'new_password';

-- Grant permissions
GRANT ALL PRIVILEGES ON college_admission_db.* TO 'your_user'@'localhost';
FLUSH PRIVILEGES;
```

### Issue 3: "Database doesn't exist"
**Solution**: Create the database manually:
```sql
CREATE DATABASE college_admission_db;
USE college_admission_db;
SOURCE database/college_admission_db.sql;
```

### Issue 4: "Compilation errors"
**Solution**: Check Java version and classpath:
```bash
# Check Java version
java -version
javac -version

# Ensure JDK 17+ is installed
# Update PATH if needed
```

### Issue 5: "Port 3306 already in use"
**Solution**: Check MySQL service status:
```bash
# Windows
net stop MySQL80
net start MySQL80

# Linux
sudo systemctl restart mysql
```

## Performance Optimization

### 1. MySQL Configuration
Edit `my.ini` or `my.cnf`:
```ini
[mysqld]
max_connections = 200
innodb_buffer_pool_size = 1G
query_cache_size = 64M
```

### 2. Connection Pool Tuning
Edit `src/dao/DBConnection.java`:
```java
private static final int POOL_SIZE = 20;  // Increase if needed
private static final int CONNECTION_TIMEOUT = 30000;  // 30 seconds
```

### 3. JVM Optimization
```bash
# Run with optimized JVM settings
java -Xms512m -Xmx2g -cp "lib/*;src" main.Main
```

## Security Best Practices

### 1. Database Security
- Use strong passwords for all MySQL users
- Limit user privileges (avoid using root for application)
- Enable SSL connections for remote access
- Regular security updates for MySQL

### 2. Application Security
- Validate all user inputs
- Use prepared statements (already implemented)
- Implement proper error handling
- Regular dependency updates

### 3. Network Security
- Configure firewall rules
- Use VPN for remote database access
- Enable MySQL encryption
- Regular security audits

## Backup and Recovery

### Database Backup
```bash
# Daily backup script
mysqldump -u root -p college_admission_db > backup_$(date +%Y%m%d).sql

# Automated backup (Linux cron)
0 2 * * * /usr/bin/mysqldump -u root -p'password' college_admission_db > /backups/college_backup_$(date +\%Y\%m\%d).sql
```

### Database Recovery
```bash
# Restore from backup
mysql -u root -p college_admission_db < backup_20240101.sql
```

## Monitoring and Maintenance

### Log Files
- Application logs: Check console output
- MySQL logs: `/var/log/mysql/` (Linux) or `C:\ProgramData\MySQL\MySQL Server 8.0\Data\` (Windows)
- Error logs: Monitor for connection issues and SQL errors

### Performance Monitoring
```sql
-- Check database performance
SHOW PROCESSLIST;
SHOW STATUS LIKE 'Threads_connected';
SHOW STATUS LIKE 'Max_used_connections';
```

### Regular Maintenance
1. Weekly: Check disk space and memory usage
2. Monthly: Optimize database tables
3. Quarterly: Update Java and MySQL versions
4. Annually: Security audit and performance review

## Support and Troubleshooting

### Getting Help
1. Check the [README.md](README.md) for basic setup
2. Review [PROJECT_STRUCTURE.md](PROJECT_STRUCTURE.md) for architecture details
3. Check MySQL and application logs for errors
4. Verify all prerequisites are installed correctly

### Reporting Issues
When reporting issues, include:
- Operating system and version
- Java version (`java -version`)
- MySQL version (`mysql --version`)
- Complete error messages
- Steps to reproduce the issue

### Community Support
- GitHub Issues: Report bugs and feature requests
- Documentation: Contribute to improving guides
- Code Contributions: Submit pull requests for improvements