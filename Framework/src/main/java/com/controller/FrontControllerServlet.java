package com.controller;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.exception.UrlNotFoundException;
import com.model.Mapping;
import com.model.UrlMethod;
import com.utils.RouteLoader;


public class FrontControllerServlet extends HttpServlet {

    private List<String> listController = new ArrayList<>();
    private Map<UrlMethod, Mapping> routes;

    @Override
    @SuppressWarnings("unchecked")
    public void init() throws ServletException {
        System.out.println("[Servlet] Initialisation du FrontController");

        ServletContext context = getServletContext();
        this.routes = (Map<UrlMethod, Mapping>) context.getAttribute("mesRoutes");
        List<String> controllers = (List<String>) context.getAttribute("listController");

        if (this.routes == null) {
            throw new ServletException("Le registre des routes n'a pas ete initialise au demarrage : "
                    + "AppContextListener est-il declare dans web.xml ?");
        }
        if (controllers != null) {
            this.listController = controllers;
        }

        System.out.println("[Servlet] " + routes.size() + " route(s) recuperee(s) depuis le ServletContext");
    }

    private Mapping findMapping(String url, String httpMethod) throws UrlNotFoundException {
        Mapping mapping = routes.get(new UrlMethod(url, httpMethod));
        if (mapping == null) {
            throw new UrlNotFoundException(url, httpMethod);
        }
        return mapping;
    }

    private Object invokeController(Mapping mapping) throws ServletException {
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            Class<?> cls = Class.forName(mapping.getClassName(), true, loader);
            Object controller = cls.getDeclaredConstructor().newInstance();
            Method method = cls.getDeclaredMethod(mapping.getMethod());
            System.out.println("[Servlet] Appel de " + mapping);
            return method.invoke(controller);

        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            throw new ServletException("La methode " + mapping
                    + " a lance une exception : " + cause, cause);
        } catch (ReflectiveOperationException e) {
            throw new ServletException("Impossible d'invoquer " + mapping + " : " + e, e);
        }
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        String url = request.getRequestURI().substring(request.getContextPath().length());
        String httpMethod = request.getMethod();

        Mapping mapping = null;
        Object result = null;
        String erreur = null;
        try {
            mapping = findMapping(url, httpMethod);
            result = invokeController(mapping);
        } catch (UrlNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            erreur = e.getMessage();
        }

        try (PrintWriter out = response.getWriter()) {
            out.println("<h2>FrontController servlet</h2>");
            out.println("<p><strong>URL saisie :</strong> " + url + "</p>");
            out.println("<p><strong>Methode HTTP :</strong> " + httpMethod + "</p>");

            if (mapping != null) {
                out.println("<p><strong>Controleur :</strong> " + mapping.getClassName() + "</p>");
                out.println("<p><strong>Methode executee :</strong> " + mapping.getMethod() + "()</p>");
                if (result != null) {
                    out.println("<p><strong>Valeur retournee :</strong> " + result + "</p>");
                } else {
                    out.println("<p><em>La methode ne retourne rien : regardez la console de Tomcat.</em></p>");
                }
            } else {
                out.println("<p style=\"color:red\">" + erreur + "</p>");
            }

            out.println("<h3>Routes enregistrees (" + routes.size() + ")</h3>");
            out.println("<ul>");
            for (Map.Entry<UrlMethod, Mapping> route : RouteLoader.sorted(routes)) {
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
