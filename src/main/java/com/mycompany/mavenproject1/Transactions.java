package com.mycompany.mavenproject1;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.sql.*;

@WebServlet("/transactions")
@MultipartConfig
public class Transactions extends HttpServlet {

    // Database connection details
    private static final String DB_URL = "";
    private static final String DB_USER = "";
    private static final String DB_PASSWORD = "";

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
            PreparedStatement ps = con.prepareStatement("SELECT * FROM transactions;");
            ResultSet rs = ps.executeQuery();

            // Start JSON array
            StringBuilder json = new StringBuilder("[");
            boolean first = true;

            while (rs.next()) {
                if (!first) {
                    json.append(",\n"); // Add a comma between JSON objects
                }
                json.append("{");
                json.append("\"transaction_id\":").append(rs.getInt("Transaction_ID")).append(",");
                json.append("\"transaction_date\":\"").append(rs.getString("Transaction_Date")).append("\",");
                json.append("\"buyer_name\":\"").append(rs.getString("Buyer_Name")).append("\",");
                json.append("\"medicine_no\":").append(rs.getInt("Medicine_No")).append(",");
                json.append("\"medicine_name\":\"").append(rs.getString("Medicine_Name")).append("\",");
                json.append("\"quantity\":").append(rs.getInt("Quantity")).append(",");
                json.append("\"price\":").append(rs.getInt("Price")).append(",");
                json.append("\"total_price\":").append(rs.getInt("Total_Price"));
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

        String transactionDate = request.getParameter("transaction_date");
        String buyerName = request.getParameter("buyer_name");
        int medicineNo = Integer.parseInt(request.getParameter("medicine_no"));
        String medicineName = request.getParameter("medicine_name");
        int quantity = Integer.parseInt(request.getParameter("quantity"));
        int price = Integer.parseInt(request.getParameter("price"));
        int totalPrice = Integer.parseInt(request.getParameter("total_price"));

        try {
            // Load MySQL Driver and establish connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // SQL query to insert data
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO transactions (Transaction_Date, Buyer_Name, Medicine_No, Medicine_Name, Quantity, Price, Total_Price) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)"
            );
            ps.setString(1, transactionDate);
            ps.setString(2, buyerName);
            ps.setInt(3, medicineNo);
            ps.setString(4, medicineName);
            ps.setInt(5, quantity);
            ps.setInt(6, price);
            ps.setInt(7, totalPrice);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                out.write("{\"message\":\"Transaction added successfully.\"}");
            } else {
                out.write("{\"error\":\"Failed to add the transaction.\"}");
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

        String transactionId = request.getParameter("id");

        if (transactionId == null) {
            out.write("{\"error\":\"Missing 'id' parameter.\"}");
            return;
        }

        try {
            // Load MySQL Driver and establish connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // SQL query to delete data
            PreparedStatement ps = con.prepareStatement("DELETE FROM transactions WHERE Transaction_ID = ?");
            ps.setInt(1, Integer.parseInt(transactionId));

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                out.write("{\"message\":\"Transaction deleted successfully.\"}");
            } else {
                out.write("{\"error\":\"No transaction found with the provided ID.\"}");
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
