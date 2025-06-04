package com.congdinh;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.RequestDispatcher;

public class ViVuStoreApplication extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("[ViVuStoreApplication] doGet: Entered method."); // DEBUG

        String messageValue = "Welcome to ViVu Store! (Servlet Data)";
        request.setAttribute("message", messageValue);
        request.setAttribute("title", "ViVu Store - Home Page (Servlet Data)");
        request.setAttribute("description", "Your one-stop shop for all things ViVu. (Servlet Data)");
        request.setAttribute("keywords", "ViVu, Store, Shopping, Online Store (Servlet Data)");

        System.out.println("[ViVuStoreApplication] doGet: Attributes set. 'message' = " + request.getAttribute("message")); // DEBUG

        RequestDispatcher dispatcher = request.getRequestDispatcher("/index.jsp");
        System.out.println("[ViVuStoreApplication] doGet: Attempting to forward to /index.jsp"); // DEBUG
        try {
            dispatcher.forward(request, response);
            System.out.println("[ViVuStoreApplication] doGet: Forward to /index.jsp seems to have completed from servlet's perspective."); // DEBUG
        } catch (Exception e) {
            System.err.println("[ViVuStoreApplication] doGet: EXCEPTION DURING FORWARD!"); // DEBUG
            e.printStackTrace(System.err); // DEBUG
            throw e; // Re-throw the exception so Tomcat can handle it (or it gets logged further)
        }
    }
}