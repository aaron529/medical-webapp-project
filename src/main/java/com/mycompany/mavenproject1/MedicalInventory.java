package com.mycompany.mavenproject1;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.sql.*;
import jakarta.servlet.annotation.MultipartConfig;

@WebServlet("/inventory")
@MultipartConfig
public class MedicalInventory extends HttpServlet {

    // Database connection details
    private static final String DB_URL = "jdbc:mysql://bycjkmy6kckotma9b5zf-mysql.services.clever-cloud.com/bycjkmy6kckotma9b5zf";
    private static final String DB_USER = "uugep1b32szf7jcb";
    private static final String DB_PASSWORD = "X9jKx53jVO38mauD5Bus";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Connection con = null;
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");

        try {
            // Load MySQL Driver and establish connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // SQL query to fetch data
            PreparedStatement ps = con.prepareStatement("SELECT * FROM medicine;");
            ResultSet rs = ps.executeQuery();

            // Start JSON array
            StringBuilder json = new StringBuilder("[");
            boolean first = true;

            while (rs.next()) {
                if (!first) {
                    json.append(",\n"); // Add a comma between JSON objects
                }
                json.append("{");
                json.append("\"id\":").append(rs.getInt("ID")).append(",");
                json.append("\"name\":\"").append(rs.getString("Medicine_Name")).append("\",");
                json.append("\"batch_no\":\"").append(rs.getString("Batch_no")).append("\",");
                json.append("\"quantity\":").append(rs.getInt("Quantity")).append(",");
                json.append("\"price\":").append(rs.getInt("Price")).append(",");
                json.append("\"expiry_date\":\"").append(rs.getString("Expiry_Date")).append("\"");
                json.append("}");
                first = false;
            }

            // End JSON array
            json.append("]");

            // Send JSON response
            out.write(json.toString());

        } catch (Exception ex) {
            // Send error message as JSON
            out.write("{\"error\":\"Couldn't load MySQL driver or fetch data.\", \"details\":\"" + ex.getMessage() + "\"}");
            ex.printStackTrace();
        } finally {
            try {
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Connection con = null;
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");

        String name = request.getParameter("name");
        String batchNo = request.getParameter("batch_no");
        String quantity = request.getParameter("quantity");
        String price = request.getParameter("price");
        String expiryDate = request.getParameter("expiry_date");

        try {
            // Load MySQL Driver and establish connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // SQL query to insert data
            PreparedStatement ps = con.prepareStatement("INSERT INTO medicine (Medicine_Name, Batch_no, Quantity, Price, Expiry_Date) VALUES (?, ?, ?, ?, ?)");
            ps.setString(1, name);
            ps.setString(2, batchNo);
            ps.setInt(3, (quantity == null ? -1 : Integer.parseInt(quantity)));
            ps.setInt(4, (price == null ? -1 : Integer.parseInt(price)));
            ps.setString(5, expiryDate);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                out.write("{\"message\":\"Record added successfully.\"}");
            } else {
                out.write("{\"error\":\"Failed to add the record.\"}");
            }

        } catch (Exception ex) {
            out.write("{\"error\":\"Couldn't load MySQL driver or insert data.\", \"details\":\"" + ex.getMessage() + "\"}");
            ex.printStackTrace();
        } finally {
            try {
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Connection con = null;
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");

        String id = request.getParameter("id");

        if (id == null) {
            out.write("{\"error\":\"Missing 'id' parameter.\"}");
            return;
        }

        try {
            // Load MySQL Driver and establish connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // SQL query to delete data
            PreparedStatement ps = con.prepareStatement("DELETE FROM medicine WHERE ID = ?");
            ps.setInt(1, Integer.parseInt(id));

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                out.write("{\"message\":\"Record deleted successfully.\"}");
            } else {
                out.write("{\"error\":\"No record found with the provided ID.\"}");
            }

        } catch (Exception ex) {
            out.write("{\"error\":\"Couldn't load MySQL driver or delete data.\", \"details\":\"" + ex.getMessage() + "\"}");
            ex.printStackTrace();
        } finally {
            try {
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
