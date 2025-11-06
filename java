<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Login</title>
</head>
<body>
  <h2>Login</h2>
  <form action="login" method="post">
    <label>Username: <input type="text" name="username" required></label><br><br>
    <label>Password: <input type="password" name="password" required></label><br><br>
    <button type="submit">Login</button>
  </form>
</body>
</html>


package com.example;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "LoginServlet", urlPatterns = "/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String username = req.getParameter("username");
        String password = req.getParameter("password");

        resp.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = resp.getWriter()) {
            // Simple hardcoded check for demo
            if ("admin".equals(username) && "1234".equals(password)) {
                out.println("<!DOCTYPE html><html><body>");
                out.println("<h2>Welcome, " + escape(username) + "!</h2>");
                out.println("<p><a href='employees'>Go to Employees</a></p>");
                out.println("<p><a href='studentPortal.jsp'>Go to Student Portal</a></p>");
                out.println("</body></html>");
            } else {
                out.println("<!DOCTYPE html><html><body>");
                out.println("<h3>Invalid credentials</h3>");
                out.println("<p><a href='login.html'>Try again</a></p>");
                out.println("</body></html>");
            }
        }
    }

    private String escape(String s) {
        return s == null ? "" : s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;");
    }
}

package com.example;

import com.example.util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet(name = "EmployeeServlet", urlPatterns = "/employees")
public class EmployeeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String idParam = req.getParameter("id");
        resp.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = resp.getWriter()) {
            out.println("<!DOCTYPE html><html><body>");
            out.println("<h2>Employees</h2>");

            // Search form
            out.println("<form method='get' action='employees'>");
            out.println("Search by ID: <input type='number' name='id'/>");
            out.println("<button type='submit'>Search</button>");
            out.println("</form><hr/>");

            if (idParam != null && !idParam.isEmpty()) {
                showEmployeeById(out, idParam);
            } else {
                showAllEmployees(out);
            }
}

    private void showAllEmployees(PrintWriter out) {
        String sql = "SELECT id, name, dept, salary FROM employees ORDER BY id";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            out.println("<table border='1' cellpadding='6'>");
            out.println("<tr><th>ID</th><th>Name</th><th>Dept</th><th>Salary</th></tr>");
            while (rs.next()) {
                out.println("<tr>");
                out.println("<td>" + rs.getInt("id") + "</td>");
                out.println("<td>" + escape(rs.getString("name")) + "</td>");
                out.println("<td>" + escape(rs.getString("dept")) + "</td>");
                out.println("<td>" + rs.getBigDecimal("salary") + "</td>");
                out.println("</tr>");
            }
            out.println("</table>");

        } catch (Exception e) {
            out.println("<p style='color:red'>Error: " + escape(e.getMessage()) + "</p>");
        }
    }

    private void showEmployeeById(PrintWriter out, String idParam) {
        String sql = "SELECT id, name, dept, salary FROM employees WHERE id = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {


    private void showEmployeeById(PrintWriter out, String idParam) {
        String sql = "SELECT id, name, dept, salary FROM employees WHERE id = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, Integer.parseInt(idParam));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    out.println("<h3>Employee Details</h3>");
                    out.println("<ul>");
                    out.println("<li>ID: " + rs.getInt("id") + "</li>");
                    out.println("<li>Name: " + escape(rs.getString("name")) + "</li>");
                    out.println("<li>Dept: " + escape(rs.getString("dept")) + "</li>");
                    out.println("<li>Salary: " + rs.getBigDecimal("salary") + "</li>");
                    out.println("</ul>");
                } else {
                    out.println("<p>No employee found with ID " + idParam + "</p>");
                }
            }

        } catch (Exception e) {
            out.println("<p style='color:red'>Error: " + escape(e.getMessage()) + "</p>");
        }
    }

    private String escape(String s) {
        return s == null ? "" : s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;");
    }
}

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Student Portal - Attendance</title>
</head>
<body>
<h2>Student Portal - Attendance</h2>

<% String msg = (String) request.getAttribute("msg"); %>
<% if (msg != null) { %>
  <p style="color:green;"><%= msg %></p>
<% } %>

<form action="attendance" method="post">
  <label>Student ID:
    <input type="number" name="studentId" required>
  </label><br><br>
  <label>Date:
    <input type="date" name="date" required>
  </label><br><br>
  <label>Status:
    <select name="status" required>
      <option value="Present">Present</option>
      <option value="Absent">Absent</option>
    </select>
  </label><br><br>
  <button type="submit">Save Attendance</button>
</form>

<p><a href="employees">View Employees</a></p>
<p><a href="login.html">Logout</a></p>
</body>
</html>


package com.example;

import com.example.util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Date;
import java.time.LocalDate;

@WebServlet(name = "AttendanceServlet", urlPatterns = "/attendance")
public class AttendanceServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String studentIdStr = req.getParameter("studentId");
        String dateStr = req.getParameter("date");
        String status = req.getParameter("status");

        String msg;
        try {
            int studentId = Integer.parseInt(studentIdStr);
            LocalDate ld = LocalDate.parse(dateStr);

            String sql = "INSERT INTO attendance(student_id, att_date, status) VALUES(?,?,?)";
            try (Connection con = DBUtil.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, studentId);
                ps.setDate(2, Date.valueOf(ld));
                ps.setString(3, status);

            String sql = "INSERT INTO attendance(student_id, att_date, status) VALUES(?,?,?)";
            try (Connection con = DBUtil.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, studentId);
                ps.setDate(2, Date.valueOf(ld));
                ps.setString(3, status);
                int rows = ps.executeUpdate();
                msg = (rows > 0) ? "Attendance saved successfully." : "No row inserted.";
            }
        } catch (Exception e) {
            msg = "Error: " + e.getMessage();
        }

        req.setAttribute("msg", msg);
        req.getRequestDispatcher("studentPortal.jsp").forward(req, resp);
    }
}


-- Create database
CREATE DATABASE IF NOT EXISTS sampledb;
USE sampledb;

-- Employees table
CREATE TABLE IF NOT EXISTS employees (
  id INT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  dept VARCHAR(100) NOT NULL,
  salary DECIMAL(10,2) NOT NULL
);

INSERT INTO employees (id, name, dept, salary) VALUES
  (1, 'Alice', 'HR', 55000.00),
  (2, 'Bob', 'IT', 65000.00),
  (3, 'Charlie', 'Finance', 70000.00)
ON DUPLICATE KEY UPDATE name=VALUES(name), dept=VALUES(dept), salary=VALUES(salary);

-- Attendance table
CREATE TABLE IF NOT EXISTS attendance (
  id INT AUTO_INCREMENT PRIMARY KEY,
  student_id INT NOT NULL,
  att_date DATE NOT NULL,
  status VARCHAR(10) NOT NULL
);
