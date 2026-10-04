package com.controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.Comparator;

import com.exception.UrlNotFoundException;
import com.model.Mapping;
import com.utils.ControllerScanner;
import com.utils.RouteLoader;
import com.model.UrlMethod;


public class FrontControllerServlet extends HttpServlet {

    private List<String> listController = new ArrayList<>();      // Sprint 1
    private Map<UrlMethod, Mapping> routes = new HashMap<>();         // Sprint 2 : URL -> Mapping

    @Override
    public void init() throws ServletException {
        String packages = getServletContext().getInitParameter("packageControllers");
        if (packages == null || packages.trim().isEmpty()) {
            throw new ServletException(
                "Parametre 'packageControllers' manquant dans web.xml");
        }

        try {
            // Sprint 1 : quelles classes sont des controleurs ?
            this.listController = ControllerScanner.findControllers(packages);
            // Sprint 2 : quelles methodes de ces controleurs ont une URL ?
            this.routes = RouteLoader.buildRoutes(listController);
        } catch (Exception e) {
            // inclut DuplicateUrlException : l'application refuse de demarrer
            throw new ServletException("Erreur pendant l'initialisation du framework : "
                    + e.getMessage(), e);
        }

        System.out.println("[Sprint3] Controleurs trouves : " + listController);
        System.out.println("[Sprint3] Routes enregistrees :");
        for (Map.Entry<UrlMethod, Mapping> route : sortedRoutes()) {
            System.out.println("   " + route.getKey() + "  ->  " + route.getValue());
        }
    }

    private List<Map.Entry<UrlMethod, Mapping>> sortedRoutes() {
        List<Map.Entry<UrlMethod, Mapping>> list = new ArrayList<>(routes.entrySet());
        list.sort(Comparator
                .comparing((Map.Entry<UrlMethod, Mapping> e) -> e.getKey().getUrl())
                .thenComparing(e -> e.getKey().getMethod()));
        return list;
    }

    // Cherche l'URL dans la table ; la LANCE en erreur si elle n'y est pas
    private Mapping findMapping(String url, String httpMethod) throws UrlNotFoundException {
        Mapping mapping = routes.get(new UrlMethod(url, httpMethod));
        if (mapping == null) {
            throw new UrlNotFoundException(url, httpMethod);
        }
        return mapping;
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        String url = request.getRequestURI().substring(request.getContextPath().length());
        String httpMethod = request.getMethod();

        // 1. Chercher la route AVANT d'ecrire (le code HTTP doit etre fixe avant la reponse)
        Mapping mapping = null;
        String erreur = null;
        try {
            mapping = findMapping(url,httpMethod);
        } catch (UrlNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);   // 404
            erreur = e.getMessage();
        }

        // 2. Ecrire la reponse
        try (PrintWriter out = response.getWriter()) {
            out.println("<h2>FrontController servlet</h2>");
            out.println("<p><strong>URL saisie :</strong> " + url + "</p>");
            out.println("<p><strong>Methode HTTP :</strong> " + httpMethod + "</p>");

            if (mapping != null) {
                out.println("<p><strong>Controleur :</strong> " + mapping.getClassName() + "</p>");
                out.println("<p><strong>Methode :</strong> " + mapping.getMethod() + "()</p>");
            } else {
                out.println("<p style=\"color:red\">" + erreur + "</p>");
            }

            out.println("<h3>Routes enregistrees (" + routes.size() + ")</h3>");
            out.println("<ul>");
            for (Map.Entry<UrlMethod, Mapping> route : sortedRoutes()) {
                out.println("<li>" + route.getKey() + " &rarr; " + route.getValue() + "</li>");
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
