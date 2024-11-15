package com.mycompany.mavenproject1;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;


@WebServlet(name = "app", urlPatterns = {"/app"})
public class app extends HttpServlet {
    
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        String sId = session.getId();
        long sTime = session.getCreationTime();
        PrintWriter pw = response.getWriter();
        pw.println("Session ID = " + sId);
        pw.println("Session Time = " + sTime);
        pw.close();
    }
}
