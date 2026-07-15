package com.controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.utils.ControllerScanner;

/* SPRINT 1 : au demarrage (init), il scanne le package des controleurs
 *            et garde la liste des classes annotees @AnnotationController.
 */
public class FrontControllerServlet extends HttpServlet {


    private List<String> listController = new ArrayList<>();


    @Override
    public void init() throws ServletException {
        // 1. Lire le parametre defini dans web.xml
        String packages = getServletContext().getInitParameter("packageControllers");
        if (packages == null || packages.trim().isEmpty()) {
            throw new ServletException(
                "Parametre 'packageControllers' manquant dans web.xml");
        }

        // 2. Scanner les packages et remplir la liste
        try {
            this.listController = ControllerScanner.findControllers(packages);
        } catch (Exception e) {
            throw new ServletException("Erreur pendant le scan des controleurs", e);
        }

        // 3. Verification dans la console de Tomcat
        System.out.println("[Sprint1] Controleurs trouves : " + listController);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        String url = request.getRequestURI().substring(request.getContextPath().length());

        try (PrintWriter out = response.getWriter()) {
            out.println("<h2>FrontController servlet</h2>");
            out.println("<p><strong>URL saisie :</strong> " + url + "</p>");

            out.println("<h3>Controleurs detectes (" + listController.size() + ")</h3>");
            out.println("<ul>");
            for (String controller : listController) {
                out.println("<li>" + controller + "</li>");
            }
            out.println("</ul>");
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
