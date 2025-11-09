<%@ page import="java.util.List, model.Student" %>
<%
    List<Student> students = (List<Student>) request.getAttribute("students");
    Double avgMarks10th = (Double) request.getAttribute("averageMarks10th");
    Double avgMarks12th = (Double) request.getAttribute("averageMarks12th");
    Integer totalStudents = (Integer) request.getAttribute("totalStudents");
    List<String> categories = (List<String>) request.getAttribute("categories");
%>
<html>
<head>
    <title>Merit List</title>
    <style>
        .stats { 
            margin: 20px 0;
            padding: 10px;
            background-color: #f5f5f5;
        }
        .filters {
            margin: 20px 0;
        }
        table {
            width: 100%;
            border-collapse: collapse;
        }
        th {
            background-color: #4CAF50;
            color: white;
        }
        th, td {
            padding: 8px;
            text-align: left;
            border: 1px solid #ddd;
        }
        tr:nth-child(even) {
            background-color: #f2f2f2;
        }
        tr:hover {
            background-color: #ddd;
        }
        .error {
            color: red;
            padding: 10px;
        }
    </style>
</head>
<body>
<h2>🏆 Student Merit List</h2>

<% if (request.getAttribute("error") != null) { %>
    <div class="error">
        <%= request.getAttribute("error") %>
    </div>
<% } %>

<div class="stats">
    <h3>Statistics</h3>
    <p>Total Students: <%= totalStudents %></p>
    <p>Average 10th Marks: <%= String.format("%.2f%%", avgMarks10th) %></p>
    <p>Average 12th Marks: <%= String.format("%.2f%%", avgMarks12th) %></p>
</div>

<div class="filters">
    <form action="${pageContext.request.contextPath}/list-students" method="get">
        <label>Sort By:</label>
        <select name="sortBy">
            <option value="marks12th DESC">12th Marks (High to Low)</option>
            <option value="marks12th ASC">12th Marks (Low to High)</option>
            <option value="marks10th DESC">10th Marks (High to Low)</option>
            <option value="marks10th ASC">10th Marks (Low to High)</option>
            <option value="first_name">Name (A-Z)</option>
            <option value="created_at DESC">Registration Date (Recent First)</option>
        </select>
        
        <label>Category:</label>
        <select name="category">
            <option value="">All Categories</option>
            <% for (String category : categories) { %>
                <option value="<%= category %>"><%= category %></option>
            <% } %>
        </select>
        
        <label>Search:</label>
        <input type="text" name="search" placeholder="Search by name, email or registration number">
        
        <button type="submit">Apply</button>
    </form>
</div>

<table>
<tr>
    <th>Reg. No</th>
    <th>Name</th>
    <th>Email</th>
    <th>Category</th>
    <th>10th Marks</th>
    <th>12th Marks</th>
    <th>Phone</th>
    <th>Guardian</th>
    <th>State</th>
    <th>City</th>
</tr>
<%
    for (Student s : students) {
%>
<tr>
    <td><%= s.getRegistrationNo() %></td>
    <td><%= s.getFirstName() + " " + s.getLastName() %></td>
    <td><%= s.getEmail() %></td>
    <td><%= s.getCategory() %></td>
    <td><%= String.format("%.2f%%", s.getMarks10th()) %></td>
    <td><%= String.format("%.2f%%", s.getMarks12th()) %></td>
    <td><%= s.getPhone() %></td>
    <td><%= s.getGuardianName() %></td>
    <td><%= s.getState() %></td>
    <td><%= s.getCity() %></td>
</tr>
<% } %>
</table>

<br>
<a href="${pageContext.request.contextPath}/add-student">➕ Register New Student</a>
</body>
</html>
