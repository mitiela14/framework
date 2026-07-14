package com.controller;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;

public class FrontControllerServlet extends HttpServlet {
     
     protected void processRequest(HttpServletRequest request, HttpServletResponse response) 
     throws ServletException, IOException{

        response.setContentType("text/html;charset=UTF-8");

        String uri = request.getRequestURI();

        String contextPath = request.getContextPath();

        //URL saisie sans le nom de l'application(FrameworkTest) : emp/list
        String url = uri.substring(contextPath.length());

        try(PrintWriter out = response.getWriter()) {
            out.println("<h2> FrontControllerServlet </h2>");
            out.println("<p><strong>URL saisie :</strong> " + url + "</p>");
            
        } 

     }

     @Override
     protected void doGet(HttpServletRequest request, HttpServletResponse response)
     throws ServletException, IOException {
        processRequest(request, response);

    }

     @Override
     protected void doPost(HttpServletRequest request, HttpServletResponse response)
     throws ServletException, IOException {
        processRequest(request, response);

    }
}


