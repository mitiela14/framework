package com.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.ServletContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.annotation.*;
import com.ioc.ApplicationContext;
import com.model.Mapping;
import com.model.UrlMethod;
import com.utils.PackageScanner;
import com.utils.RouteLoader;

@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();

        String packageToScan = context.getInitParameter("packageControllers");
        String viewPrefix = context.getInitParameter("view-prefix");
        String viewSuffix = context.getInitParameter("view-suffix");

        if (packageToScan == null) packageToScan = "com.controller";
        if (viewPrefix == null) viewPrefix = "/WEB-INF/Views/";
        if (viewSuffix == null) viewSuffix = ".jsp";

        try {
            // 1. SCAN DU PACKAGE CONTROLEURS
            List<Class<?>> allClasses = PackageScanner.scanClasses(packageToScan);
            System.out.println("[SCAN] " + allClasses.size() + " classe(s) trouvee(s) dans " + packageToScan);

            if (allClasses.isEmpty()) {
                throw new RuntimeException("Aucune classe trouvee dans le package '" + packageToScan
                        + "'. Verifie le <context-param> packageControllers dans web.xml "
                        + "et que tes controleurs sont bien compiles dans WEB-INF/classes "
                        + "(ou dans un JAR de WEB-INF/lib).");
            }

            // 2. CREATION DU CONTENEUR IOC (UNE SEULE FOIS)
            ApplicationContext appContext = new ApplicationContext();

            // Enregistrer d'abord les Repository, puis Service, puis Controller
            for (Class<?> cls : allClasses) {
                if (cls.isAnnotationPresent(Repository.class)) {
                    appContext.register(cls);
                    System.out.println("[IOC] Repository enregistre : " + cls.getName());
                }
            }
            for (Class<?> cls : allClasses) {
                if (cls.isAnnotationPresent(Service.class)) {
                    appContext.register(cls);
                    System.out.println("[IOC] Service enregistre : " + cls.getName());
                }
            }
            for (Class<?> cls : allClasses) {
                if (cls.isAnnotationPresent(AnnotationController.class)) {
                    appContext.register(cls);
                    System.out.println("[IOC] Controller enregistre : " + cls.getName());
                }
            }

            context.setAttribute("appContext", appContext);

            // 3. CONSTRUCTION DES ROUTES
            Map<UrlMethod, Mapping> mappingUrls = new HashMap<>();
            RouteLoader.buildRoutingTable(packageToScan, mappingUrls);
            context.setAttribute("mesRoutes", mappingUrls);

            // 4. STOCKAGE DES VUES
            context.setAttribute("view-prefix", viewPrefix);
            context.setAttribute("view-suffix", viewSuffix);

            System.out.println("[INFO] Conteneur IoC, routes et vues charges avec succes !");

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'initialisation du framework", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("[INFO] Application arretee.");
    }
}