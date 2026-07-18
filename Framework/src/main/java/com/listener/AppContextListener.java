package com.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import java.util.List;
import java.util.Map;

import com.model.Mapping;
import com.model.UrlMethod;
import com.utils.ControllerScanner;
import com.utils.RouteLoader;


public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("[ORDRE 1/3] Listener : contextInitialized()");

        ServletContext context = sce.getServletContext();

        String packages = context.getInitParameter("packageControllers");
        if (packages == null || packages.trim().isEmpty()) {
            throw new IllegalStateException(
                "Parametre 'packageControllers' manquant dans web.xml");
        }

        try {
            List<String> controllers = ControllerScanner.findControllers(packages);
            Map<UrlMethod, Mapping> routes = RouteLoader.buildRoutes(controllers);

            context.setAttribute("listController", controllers);
            context.setAttribute("mesRoutes", routes);

            System.out.println("[Listener] Controleurs trouves : " + controllers);
            System.out.println("[Listener] Routes enregistrees :");
            for (Map.Entry<UrlMethod, Mapping> route : RouteLoader.sorted(routes)) {
                System.out.println("   " + route.getKey() + "  ->  " + route.getValue());
            }

        } catch (Exception e) {
            throw new IllegalStateException(
                "Erreur lors de l'initialisation du framework : " + e.getMessage(), e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("[Listener] contextDestroyed() : application arretee");
    }
}
