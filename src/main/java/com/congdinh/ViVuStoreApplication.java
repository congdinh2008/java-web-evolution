package com.congdinh;

import java.io.IOException;
import java.io.PrintWriter;

// New jakarta imports
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ViVuStoreApplication extends HttpServlet { // Extends jakarta.servlet.http.HttpServlet
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Set the content type
        response.setContentType("text/html");
        
        try {
            PrintWriter out = response.getWriter();
            // Write the HTML response
            out.println("<html>");
            out.println("<head><title>ViVu Store</title></head>");
            out.println("<body>");
            out.println("<h1>Welcome to ViVu Store</h1>");
            out.println("<p>Your one-stop shop for all things ViVu!</p>");
            out.println("</body>");
            out.println("</html>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}