package com.controller;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.annotation.JsonResponse;
import com.annotation.UrlMapping;
import com.binding.BindingResult;
import com.binding.ParameterBinder;
import com.exception.BindingException;
import com.ioc.ApplicationContext;
import com.model.Mapping;
import com.model.ModelView;
import com.model.UrlMethod;
import com.session.SessionManager;
import com.utils.JsonConverter;

@WebServlet(name = "FrontController", urlPatterns = {"/"})
@MultipartConfig(
        maxFileSize = 10L * 1024 * 1024,      // 10 Mo maximum par fichier
        maxRequestSize = 20L * 1024 * 1024,   // 20 Mo maximum pour toute la requete
        fileSizeThreshold = 1024 * 1024       // au-dela de 1 Mo : stockage sur disque temporaire
)
public class FrontControllerServlet extends HttpServlet {

    private String viewPrefix;
    private String viewSuffix;
    private ApplicationContext appContext; // LE CONTENEUR IOC (singleton)

    @Override
    public void init() throws ServletException {
        ServletContext ctx = getServletContext();

        // Recuperation du conteneur (cree UNE SEULE FOIS dans le Listener)
        this.appContext = (ApplicationContext) ctx.getAttribute("appContext");
        if (this.appContext == null) {
            throw new ServletException("Le conteneur IoC n'a pas ete initialise au demarrage.");
        }

        // Recuperation des vues (via getAttribute, pas getInitParameter)
        this.viewPrefix = (String) ctx.getAttribute("view-prefix");
        this.viewSuffix = (String) ctx.getAttribute("view-suffix");

        if (viewPrefix == null) viewPrefix = "/WEB-INF/Views/";
        if (viewSuffix == null) viewSuffix = ".jsp";
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        // 1. Recuperation des routes depuis le contexte
        @SuppressWarnings("unchecked")
        Map<UrlMethod, Mapping> mappingUrls =
                (Map<UrlMethod, Mapping>) getServletContext().getAttribute("mesRoutes");

        if (mappingUrls == null) {
            throw new ServletException("Le registre des routes n'a pas ete initialise au demarrage.");
        }
        
        // 2. Parsing de l'URL
        String urlMain = request.getRequestURL().toString();
        String contextPath = request.getContextPath();
        String url = request.getRequestURI().substring(contextPath.length());
        String reqMethod = request.getMethod();
        UrlMethod urlMethod = new UrlMethod(url, reqMethod);

        // 3. Traitement de la route
        if (mappingUrls.containsKey(urlMethod)) {
            String methodName = "";
            try {
                Mapping mapping = mappingUrls.get(urlMethod);
                Class<?> controllerClass = Class.forName(mapping.getClassName());

                Object controller = appContext.getBean(controllerClass);
                if (controller == null) {
                    throw new ServletException("Controller non trouve dans le conteneur : " + controllerClass.getName());
                }

                methodName = mapping.getMethod();
                Method method = findMethod(controllerClass, methodName, url, reqMethod);

        
                BindingResult binding = ParameterBinder.bind(method, request, response);

            
                for (Map.Entry<String, Object> e : binding.getValues().entrySet()) {
                    request.setAttribute(e.getKey(), e.getValue());
                }

          
                Object result;
                try {
                    result = method.invoke(controller, binding.getArguments());
                } finally {
                    
                    SessionManager.synchroniser(request);
                }

                if (method.isAnnotationPresent(JsonResponse.class)
                        || controllerClass.isAnnotationPresent(JsonResponse.class)) {
                    response.setContentType("application/json;charset=UTF-8");
                    try (PrintWriter out = response.getWriter()) {
                        out.print(JsonConverter.toJson(result));
                    }
                    return;
                }

                
                if (result instanceof ModelView) {
                    ModelView mv = (ModelView) result;

                    if (mv.getData() != null) {
                        for (Map.Entry<String, Object> e : mv.getData().entrySet()) {
                            request.setAttribute(e.getKey(), e.getValue());
                        }
                    }

                    String view = viewPrefix + mv.getUrl() + viewSuffix;
                    request.getRequestDispatcher(view).forward(request, response);
                    return;

                } else {
                   
                    response.setContentType("text/html;charset=UTF-8");
                    try (PrintWriter out = response.getWriter()) {
                        out.println("<h2>FrontController servlet</h2>");
                        out.println("<p><strong>Current URL:</strong> " + urlMain + "</p>");
                        out.println("<p>La methode a retourne : " + result + "</p>");
                    }
                }

            } catch (BindingException e) {

                System.out.println("[BINDING][ERREUR] " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("text/html;charset=UTF-8");
                try (PrintWriter out = response.getWriter()) {
                    out.println("<h2>Erreur de binding (Sprint 7 / 7bis)</h2>");
                    out.println("<p><strong>URL :</strong> " + html(url) + " [" + reqMethod + "]</p>");
                    out.println("<p><strong>Methode :</strong> " + html(methodName) + "</p>");
                    out.println("<pre>" + html(e.getMessage()) + "</pre>");
                }

            } catch (Exception e) {
                throw new ServletException("Erreur lors de l'execution du controleur pour l'URL: " + url, e);
            }
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND,
                    "Aucune route trouvee pour l'URL : " + url + " [" + reqMethod + "]");
        }
    }


    private Method findMethod(Class<?> controllerClass, String methodName,
                              String url, String httpMethod) throws NoSuchMethodException {

        List<Method> memesNoms = new ArrayList<>();
        for (Method m : controllerClass.getDeclaredMethods()) {
            if (m.getName().equals(methodName)) memesNoms.add(m);
        }

        if (memesNoms.isEmpty()) {
            throw new NoSuchMethodException(controllerClass.getName() + "." + methodName + "()");
        }

        // 1. celle qui porte exactement la route demandee
        for (Method m : memesNoms) {
            UrlMapping um = m.getAnnotation(UrlMapping.class);
            if (um == null) continue;
            if (um.value().equals(url) && um.method().equalsIgnoreCase(httpMethod)) {
                m.setAccessible(true);
                if (memesNoms.size() > 1) {
                    System.out.println("[ROUTAGE] Surcharge de " + methodName + "() : on prend "
                            + Arrays.toString(m.getParameterTypes()) + " (celle mappee sur "
                            + url + " [" + httpMethod + "])");
                }
                return m;
            }
        }

        // 2. sinon, celle dont tous les arguments peuvent etre lies
        for (Method m : memesNoms) {
            boolean tousLieables = true;
            for (Class<?> type : m.getParameterTypes()) {
                if (!ParameterBinder.isBindable(type)) { tousLieables = false; break; }
            }
            if (tousLieables) {
                m.setAccessible(true);
                return m;
            }
        }

        // 3. aucune possibility : on rend la main, le binding signalera l'objet
        Method premiere = memesNoms.get(0);
        premiere.setAccessible(true);
        return premiere;
    }

    private String html(String texte) {
        if (texte == null) return "";
        return texte.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
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