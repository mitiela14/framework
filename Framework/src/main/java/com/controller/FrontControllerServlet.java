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
import com.model.ModelView;
import com.model.UrlMethod;
import com.utils.RouteLoader;

/**
 * SPRINT 0 : toutes les URL passent par ce servlet.
 * SPRINT 1 a 3 : controleurs, routes, GET/POST.
 * SPRINT 3bis : execution de la methode par reflexion (invoke).
 * SPRINT SPECIAL : le scan et la table des routes ne sont plus faits ici mais dans
 *                  AppContextListener (au demarrage). Le servlet se contente de LIRE
 *                  le resultat dans le ServletContext.
 * SPRINT 5 : si la methode retourne un ModelView, on transmet ses donnees a la vue
 *            et on fait un forward vers la JSP (prefixe + nom + suffixe).
 */
public class FrontControllerServlet extends HttpServlet {

    private List<String> listController = new ArrayList<>();
    private Map<UrlMethod, Mapping> routes;
    private String viewPrefix;   // Sprint 5 : ex. /WEB-INF/Views/
    private String viewSuffix;   // Sprint 5 : ex. .jsp

    @Override
    @SuppressWarnings("unchecked")
    public void init() throws ServletException {
        System.out.println("[ORDRE 3/3] Servlet : init()");

        // Lire ce que le listener a prepare (il a deja ete execute : il passe en premier)
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

        // Sprint 5 : lire l'emplacement des vues prepare par le listener
        this.viewPrefix = (String) context.getAttribute("view-prefix");
        this.viewSuffix = (String) context.getAttribute("view-suffix");
        if (viewPrefix == null) viewPrefix = "/WEB-INF/Views/";
        if (viewSuffix == null) viewSuffix = ".jsp";

        System.out.println("[Servlet] " + routes.size() + " route(s) recuperee(s) depuis le ServletContext");
    }

    private Mapping findMapping(String url, String httpMethod) throws UrlNotFoundException {
        Mapping mapping = routes.get(new UrlMethod(url, httpMethod));
        if (mapping == null) {
            throw new UrlNotFoundException(url, httpMethod);
        }
        return mapping;
    }

    // Sprint 3bis : executer la methode du controleur par reflexion
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

    // SPRINT 5 : afficher une vue avec ses donnees
    private void forwardToView(ModelView mv, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (mv.getUrl() == null || mv.getUrl().trim().isEmpty()) {
            throw new ServletException("ModelView sans vue : utiliser new ModelView(\"NomDeLaVue\")");
        }

        // 1. Les donnees de la Map deviennent des ATTRIBUTS DE LA REQUETE :
        //    la JSP les lira avec request.getAttribute("cle") ou ${cle}
        for (Map.Entry<String, Object> donnee : mv.getData().entrySet()) {
            request.setAttribute(donnee.getKey(), donnee.getValue());
        }

        // 2. Concatener : prefixe + nom de la vue + suffixe
        //    "/WEB-INF/Views/" + "ListeFruits" + ".jsp"
        String viewPath = viewPrefix + mv.getUrl() + viewSuffix;

        // 3. Verifier que la vue existe (sinon message clair plutot qu'un 404 obscur)
        if (getServletContext().getResource(viewPath) == null) {
            throw new ServletException("Vue introuvable : " + viewPath
                    + " (verifier le nom dans new ModelView(\"" + mv.getUrl() + "\"))");
        }

        // 4. Forward : la requete est transmise a la JSP, SANS que le navigateur le sache
        System.out.println("[Servlet] forward vers " + viewPath);
        request.getRequestDispatcher(viewPath).forward(request, response);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

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

        if (result instanceof ModelView) {
            forwardToView((ModelView) result, request, response);
            return;
        }

        // Sinon (valeur brute, void, ou URL introuvable) : page de diagnostic comme avant
        response.setContentType("text/html;charset=UTF-8");
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
