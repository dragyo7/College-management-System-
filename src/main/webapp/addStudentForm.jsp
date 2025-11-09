<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Register Student</title>
    <style>
        .form-group { margin-bottom: 15px; }
        .form-group label { display: block; margin-bottom: 5px; }
        .error { color: red; }
    </style>
</head>
<body>
<h2>🎓 Register Student</h2>

<% if (request.getAttribute("error") != null) { %>
    <div class="error">
        <%= request.getAttribute("error") %>
    </div>
<% } %>

<form action="${pageContext.request.contextPath}/add-student" method="post">
    <div class="form-group">
        <label>First Name:</label>
        <input type="text" name="firstName" required pattern="[A-Za-z\s-']{2,50}">
    </div>
    
    <div class="form-group">
        <label>Last Name:</label>
        <input type="text" name="lastName" required pattern="[A-Za-z\s-']{2,50}">
    </div>
    
    <div class="form-group">
        <label>Email:</label>
        <input type="email" name="email" required>
    </div>

    <div class="form-group">
        <label>Date of Birth:</label>
        <input type="date" name="dateOfBirth" required>
    </div>

    <div class="form-group">
        <label>Gender:</label>
        <select name="gender" required>
            <option value="Male">Male</option>
            <option value="Female">Female</option>
            <option value="Other">Other</option>
        </select>
    </div>

    <div class="form-group">
        <label>Phone:</label>
        <input type="tel" name="phone" required pattern="\\d{10}">
    </div>

    <div class="form-group">
        <label>Address:</label>
        <textarea name="address" required></textarea>
    </div>

    <div class="form-group">
        <label>City:</label>
        <input type="text" name="city" required>
    </div>

    <div class="form-group">
        <label>State:</label>
        <input type="text" name="state" required>
    </div>

    <div class="form-group">
        <label>Postal Code:</label>
        <input type="text" name="postalCode" required pattern="\\d{6}">
    </div>

    <div class="form-group">
        <label>Category:</label>
        <select name="category" required>
            <option value="General">General</option>
            <option value="OBC">OBC</option>
            <option value="SC">SC</option>
            <option value="ST">ST</option>
        </select>
    </div>

    <div class="form-group">
        <label>10th Marks (%):</label>
        <input type="number" name="marks10th" min="0" max="100" step="0.01" required>
    </div>

    <div class="form-group">
        <label>12th Marks (%):</label>
        <input type="number" name="marks12th" min="0" max="100" step="0.01" required>
    </div>

    <div class="form-group">
        <label>Previous Qualification:</label>
        <input type="text" name="prevQualification" required>
    </div>

    <div class="form-group">
        <label>Guardian Name:</label>
        <input type="text" name="guardianName" required>
    </div>

    <div class="form-group">
        <label>Guardian Phone:</label>
        <input type="tel" name="guardianPhone" required pattern="\\d{10}">
    </div>

    <div class="form-group">
        <label>Guardian Email:</label>
        <input type="email" name="guardianEmail" required>
    </div>

    <button type="submit">Register</button>
</form>
<br>
<a href="${pageContext.request.contextPath}/list-students">View All Students</a>
</body>
</html>
